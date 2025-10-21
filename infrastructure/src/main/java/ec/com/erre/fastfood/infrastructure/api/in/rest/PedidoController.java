package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.services.PedidoGestionService;
import ec.com.erre.fastfood.domain.api.services.PedidosProcesoService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoItemMapper;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.PedidoDto;
import ec.com.erre.fastfood.share.api.dtos.PedidoItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos (POS)", description = "Creación y gestión de pedidos")
public class PedidoController {

	private final PedidoGestionService gestion;
	private final PedidosProcesoService proceso;
	private final PedidoMapper pedidoMapper;
	private final PedidoItemMapper itemMapper;

	public PedidoController(PedidoGestionService gestion, PedidosProcesoService proceso, PedidoMapper pedidoMapper,
			PedidoItemMapper itemMapper) {
		this.gestion = gestion;
		this.proceso = proceso;
		this.pedidoMapper = pedidoMapper;
		this.itemMapper = itemMapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crear pedido (puede incluir items)")
	public ResponseEntity<Map<String, Long>> crear(@RequestBody PedidoDto dto)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {
		Long id = gestion.crear(pedidoMapper.dtoToDomain(dto), "Usuario");
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obtener detalle de pedido")
	public ResponseEntity<PedidoDto> obtener(@PathVariable Long id) throws EntidadNoEncontradaException {
		Pedido p = gestion.obtenerDetalle(id);
		PedidoDto dto = pedidoMapper.domainToDto(p);
		dto.setItems(p.getItems() == null ? java.util.Collections.emptyList()
				: p.getItems().stream().map(itemMapper::domainToDto).toList());
		return ResponseEntity.ok(dto);
	}

	@PostMapping(value = "/{id}/items", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Agregar ítem al pedido")
	public ResponseEntity<Map<String, Long>> agregarItem(@PathVariable Long id, @RequestBody PedidoItemDto dto)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Long itemId = gestion.agregarItem(id, itemMapper.dtoToDomain(dto));
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", itemId));
	}

	@PostMapping(value = "/{id}/cambiar-estado", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Marcar pedido como LISTO (C→L)")
	public ResponseEntity<Void> marcarListo(@PathVariable Long id)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		gestion.cambiarEstado(id, "L");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/{id}/anular", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Anular pedido (si no está entregado/anulado)")
	public ResponseEntity<Void> anular(@PathVariable Long id)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		gestion.cancelar(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/{id}/entregar", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Entregar pedido (descuenta inventario vía SP)")
	public ResponseEntity<Void> entregar(@PathVariable Long id)
			throws EntidadNoEncontradaException, ReglaDeNegocioException, ServiceException {
		proceso.entregar(id, "USUARIO");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar pedidos con paginado por filtros")
	public Pagina<PedidoDto> search(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<Pedido> page = gestion.paginadoPorFiltros(pager, filters);
		return Pagina.<PedidoDto> builder()
				.contenido(page.getContenido().stream().map(pedidoMapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}
}
