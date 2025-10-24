package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Compra;
import ec.com.erre.fastfood.domain.api.models.api.CompraItem;
import ec.com.erre.fastfood.domain.api.services.CompraService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.CompraItemMapper;
import ec.com.erre.fastfood.infrastructure.api.mappers.CompraMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.infrastructure.commons.mappers.PaginaMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.CompraDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compras")
@Tag(name = "Compras", description = "Registro de compras a proveedores")
public class CompraController {

	private final CompraService service;
	private final CompraMapper compraMapper;
	private final CompraItemMapper itemMapper;

	public CompraController(CompraService service, CompraMapper compraMapper, CompraItemMapper itemMapper) {
		this.service = service;
		this.compraMapper = compraMapper;
		this.itemMapper = itemMapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crear compra con ítems")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Compra creada"),
			@ApiResponse(responseCode = "404", description = "Proveedor/Ingrediente no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Regla de negocio violada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Map<String, Long>> crear(@Valid @RequestBody CompraDto dto)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		Compra compra = compraMapper.dtoToDomain(dto);
		List<CompraItem> items = dto.getItems() == null ? List.of()
				: dto.getItems().stream().map(itemMapper::dtoToDomain).toList();

		Long compraId = service.crear(compra, items);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", compraId));
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obtener compra por id (incluye ítems)")
	public ResponseEntity<CompraDto> obtenerPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		Compra compra = service.buscarPorId(id);
		CompraDto dto = compraMapper.domainToDto(compra);
		dto.setItems(compra.getItems().stream().map(itemMapper::domainToDto).toList());
		return ResponseEntity.ok(dto);
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar compras con paginado por filtros (incluye items y total)")
	public Pagina<CompraDto> buscar(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<Compra> paginaCompras = service.buscarPaginado(pager, filters);

		return PaginaMapper.map(paginaCompras, compra -> {
			CompraDto dto = compraMapper.domainToDto(compra);
			dto.setItems(compra.getItems().stream().map(itemMapper::domainToDto).toList());
			dto.setTotal(compra.getTotal());
			return dto;
		});
	}
}
