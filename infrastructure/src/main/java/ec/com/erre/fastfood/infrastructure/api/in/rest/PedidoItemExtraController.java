package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.services.PedidoItemExtraService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoItemExtraMapper;
import ec.com.erre.fastfood.share.api.dtos.PedidoItemExtraDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos/{pedidoId}")
@Tag(name = "Extras de Pedido", description = "Gestión de extras por ítem de pedido")
public class PedidoItemExtraController {

	private final PedidoItemExtraService service;
	private final PedidoItemExtraMapper mapper;

	public PedidoItemExtraController(PedidoItemExtraService service, PedidoItemExtraMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping(value = "/items/{itemId}/extras", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Agregar extra a un ítem")
	public ResponseEntity<Map<String, Long>> agregar(@PathVariable Long pedidoId, @PathVariable Long itemId,
			@Validated(PedidoItemExtraDto.Crear.class) @RequestBody PedidoItemExtraDto dto)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Long id = service.agregarExtra(pedidoId, itemId, mapper.dtoToDomain(dto));
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@GetMapping(value = "/items/{itemId}/extras", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar extras de un ítem")
	public ResponseEntity<List<PedidoItemExtraDto>> listar(@PathVariable Long pedidoId, @PathVariable Long itemId)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		return ResponseEntity
				.ok(service.listarExtrasDeItem(pedidoId, itemId).stream().map(mapper::domainToDto).toList());
	}

	@PutMapping(value = "/extras/{extraId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Actualizar cantidad de un extra")
	public ResponseEntity<Void> actualizarCantidad(@PathVariable Long pedidoId, @PathVariable Long extraId,
			@RequestBody Map<String, BigDecimal> body) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		service.actualizarCantidad(pedidoId, extraId, body.get("cantidad"));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "/extras/{extraId}")
	@Operation(summary = "Eliminar extra")
	public ResponseEntity<Void> eliminar(@PathVariable Long pedidoId, @PathVariable Long extraId)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		service.eliminarExtra(pedidoId, extraId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
