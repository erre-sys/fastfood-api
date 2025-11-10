package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.services.PedidoGestionService;
import ec.com.erre.fastfood.domain.api.services.PedidosProcesoService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoItemMapper;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoMapper;
import ec.com.erre.fastfood.infrastructure.commons.mappers.PaginaMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.PedidoDto;
import ec.com.erre.fastfood.share.api.dtos.PedidoItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

	public static final String ESTADO_LISTO = "L";
	private final PedidoGestionService pedidoGestionService;
	private final PedidosProcesoService pedidosProcesoService;
	private final PedidoMapper pedidoMapper;
	private final PedidoItemMapper itemMapper;

	public PedidoController(PedidoGestionService gestion, PedidosProcesoService proceso, PedidoMapper pedidoMapper,
			PedidoItemMapper itemMapper) {
		this.pedidoGestionService = gestion;
		this.pedidosProcesoService = proceso;
		this.pedidoMapper = pedidoMapper;
		this.itemMapper = itemMapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crear pedido con items y extras")
	public ResponseEntity<Map<String, Long>> crear(@Valid @RequestBody PedidoDto dto)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {
		Pedido pedido = pedidoMapper.dtoToDomain(dto);
		Long pedidoId = pedidoGestionService.crear(pedido);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", pedidoId));
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obtener detalle de pedido")
	public ResponseEntity<PedidoDto> obtenerPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		Pedido pedido = pedidoGestionService.obtenerDetalle(id);
		PedidoDto dto = pedidoMapper.domainToDto(pedido);
		dto.setItems(pedido.getItems() == null ? java.util.Collections.emptyList()
				: pedido.getItems().stream().map(itemMapper::domainToDto).toList());
		return ResponseEntity.ok(dto);
	}

	@PostMapping(value = "/{id}/items", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Agregar ítem al pedido")
	public ResponseEntity<Map<String, Long>> agregarItem(@PathVariable Long id, @Valid @RequestBody PedidoItemDto dto)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Long itemId = pedidoGestionService.agregarItem(id, itemMapper.dtoToDomain(dto));
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", itemId));
	}

	@PostMapping(value = "/{id}/marcar-listo", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Marcar pedido como LISTO (C→L)")
	public ResponseEntity<Void> marcarListo(@PathVariable Long id)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		pedidoGestionService.cambiarEstado(id, ESTADO_LISTO);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/{id}/anular", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Anular pedido (si no está entregado/anulado)")
	public ResponseEntity<Void> anular(@PathVariable Long id)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		pedidoGestionService.cancelar(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/{id}/entregar", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Entregar pedido (descuenta inventario vía SP)")
	public ResponseEntity<Void> entregar(@PathVariable Long id, @RequestParam String entregadoPor)
			throws EntidadNoEncontradaException, ReglaDeNegocioException, ServiceException {
		pedidosProcesoService.entregar(id, entregadoPor);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar pedidos con paginado por filtros")
	public Pagina<PedidoDto> buscar(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<Pedido> paginaPedidos = pedidoGestionService.paginadoPorFiltros(pager, filters);
		return PaginaMapper.map(paginaPedidos, pedidoMapper::domainToDto);
	}

}
