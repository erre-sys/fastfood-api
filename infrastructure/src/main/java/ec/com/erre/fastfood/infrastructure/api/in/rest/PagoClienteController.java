package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.PagoCliente;
import ec.com.erre.fastfood.domain.api.services.PagoClienteService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.PagoClienteMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.PagoClienteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pagos-cliente")
@Tag(name = "Pagos de Cliente", description = "Registro de pagos vinculados a pedidos")
public class PagoClienteController {

	private final PagoClienteService service;
	private final PagoClienteMapper mapper;

	public PagoClienteController(PagoClienteService service, PagoClienteMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Registrar pago de cliente")
	public ResponseEntity<Map<String, Long>> crear(
			@Validated(PagoClienteDto.Crear.class) @RequestBody PagoClienteDto dto)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Long id = service.registrarPago(mapper.dtoToDomain(dto));
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar pago por id")
	public ResponseEntity<PagoClienteDto> buscarPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(mapper.domainToDto(service.buscarPorId(id)));
	}

	@GetMapping(value = "/pedidos/{pedidoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar pagos por pedido")
	public ResponseEntity<List<PagoClienteDto>> listarPorPedido(@PathVariable Long pedidoId) {
		return ResponseEntity.ok(service.listarPorPedido(pedidoId).stream().map(mapper::domainToDto).toList());
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar pagos (paginado + filtros)")
	public Pagina<PagoClienteDto> search(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<PagoCliente> page = service.paginado(pager, filters);
		return Pagina.<PagoClienteDto> builder()
				.contenido(page.getContenido().stream().map(mapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}
}
