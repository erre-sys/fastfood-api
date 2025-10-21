package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Proveedor;
import ec.com.erre.fastfood.domain.api.services.ProveedorService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.ProveedorMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.ProveedorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
@Tag(name = "Proveedores", description = "Gestión de proveedores")
public class ProveedorController {

	private final ProveedorService service;
	private final ProveedorMapper mapper;

	public ProveedorController(ProveedorService service, ProveedorMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crear proveedor")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Creado"),
			@ApiResponse(responseCode = "409", description = "Duplicado (nombre/RUC)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> crear(@Validated(ProveedorDto.Crear.class) @RequestBody ProveedorDto dto)
			throws RegistroDuplicadoException, ReglaDeNegocioException {
		service.crear(mapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar todos los proveedores")
	public ResponseEntity<List<ProveedorDto>> listarTodos() {
		return ResponseEntity.ok(service.listarTodos().stream().map(mapper::domainToDto).toList());
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar proveedor por id")
	public ResponseEntity<ProveedorDto> buscarPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(mapper.domainToDto(service.buscarPorId(id)));
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Actualizar proveedor")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Actualizado"),
			@ApiResponse(responseCode = "404", description = "No existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Duplicado (nombre/RUC)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> actualizar(@Validated(ProveedorDto.Actualizar.class) @RequestBody ProveedorDto dto)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException {
		service.actualizar(mapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Eliminar proveedor")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) throws EntidadNoEncontradaException {
		service.eliminarPorId(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar proveedores con paginado por filtros")
	public Pagina<ProveedorDto> search(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<Proveedor> page = service.paginadoPorFiltros(pager, filters);
		return Pagina.<ProveedorDto> builder().contenido(page.getContenido().stream().map(mapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}
}
