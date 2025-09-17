package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.GrupoPlato;
import ec.com.erre.fastfood.domain.api.services.GrupoPlatoService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.GrupoPlatoMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.PagerAndSortDto;
import ec.com.erre.fastfood.infrastructure.commons.repositories.Pagina;
import ec.com.erre.fastfood.share.api.dtos.GrupoPlatoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupo-platos")
@Tag(name = "Grupo de platos", description = "Grupo de platos")
public class GrupoPlatoController {
	private final GrupoPlatoService grupoPlatoService;
	private final GrupoPlatoMapper grupoPlatoMapper;

	public GrupoPlatoController(GrupoPlatoService grupoPlatoService, GrupoPlatoMapper grupoPlatoMapper) {
		this.grupoPlatoService = grupoPlatoService;
		this.grupoPlatoMapper = grupoPlatoMapper;
	}

	/**
	 * Crear un nuevo grupo de platos
	 *
	 * @param grupoPlatoDto
	 * @return ResponseEntity
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crea un nuevo grupo de plato")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Grupo de plato creado"),
			@ApiResponse(responseCode = "400", description = "Entrada incorrecta"),
			@ApiResponse(responseCode = "409", description = "Grupo de plato ya existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> crear(@Validated(GrupoPlatoDto.Crear.class) @RequestBody GrupoPlatoDto grupoPlatoDto)
			throws RegistroDuplicadoException, ReglaDeNegocioException {
		this.grupoPlatoService.crear(this.grupoPlatoMapper.dtoToDomain(grupoPlatoDto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	/**
	 * Buscar todos los grupos
	 *
	 * @return ResponseEntity
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar los grupos de platos")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con esos parámetros", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<List<GrupoPlatoDto>> buscarTodos() {
		return ResponseEntity.ok(grupoPlatoMapper.domainsToDtos(grupoPlatoService.buscarTodos()));
	}

	/**
	 * Buscar grupo por ID
	 *
	 * @param id
	 * @return GrupoPlatoDto
	 */
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar grupo por id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con ese id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<GrupoPlatoDto> buscarPorId(@NotNull @PathVariable Long id)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(grupoPlatoMapper.domainToDto(grupoPlatoService.buscarPorId(id)));
	}

	/**
	 * Actualizar un nuevo grupo de platos
	 *
	 * @param grupoPlatoDto
	 * @return ResponseEntity
	 */
	@PutMapping()
	@Operation(summary = "Actualizar un grupo de plato")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Grupo de platos actualizada correctamente"),
			@ApiResponse(responseCode = "404", description = "Grupo de plato no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> actualizar(
			@Validated(GrupoPlatoDto.Actualizar.class) @RequestBody GrupoPlatoDto grupoPlatoDto)
			throws RegistroDuplicadoException, EntidadNoEncontradaException {
		this.grupoPlatoService.actualizar(this.grupoPlatoMapper.dtoToDomain(grupoPlatoDto));
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * Buscar todos los grupos con paginacion y filtro
	 *
	 * @param filters consulta para filtro
	 * @param pager paged
	 * @return list GrupoPlatoDto
	 */
	@PostMapping("/search")
	@Operation(summary = "Obtener Departamento con paginado por filtros")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "NomDepartamentos encontradas"),
			@ApiResponse(responseCode = "404", description = "NomDepartamentos no encontradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public Pagina<GrupoPlatoDto> obtenerGrupoPlatoPaginadoPorFiltros(PagerAndSortDto pager,
			@RequestBody List<CriterioBusqueda> filters) {
		Pagina<GrupoPlato> page = grupoPlatoService.obtenerGrupoPlatoPaginadoPorFiltros(pager, filters);
		return Pagina.<GrupoPlatoDto> builder()
				.contenido(page.getContenido().stream().map(grupoPlatoMapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}

}