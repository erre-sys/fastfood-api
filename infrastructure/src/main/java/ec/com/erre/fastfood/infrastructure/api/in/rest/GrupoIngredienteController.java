package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.services.GrupoIngredienteService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.GrupoIngredienteMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.PagerAndSortDto;
import ec.com.erre.fastfood.infrastructure.commons.repositories.Pagina;
import ec.com.erre.fastfood.share.api.dtos.GrupoIngredienteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupo-ingredientes")
@Tag(name = "Grupo de ingredientes", description = "Grupo de ingredientes")
public class GrupoIngredienteController {
	private final GrupoIngredienteService grupoIngredienteService;
	private final GrupoIngredienteMapper grupoIngredienteMapper;

	public GrupoIngredienteController(GrupoIngredienteService grupoIngredienteService,
			GrupoIngredienteMapper grupoIngredienteMapper) {
		this.grupoIngredienteService = grupoIngredienteService;
		this.grupoIngredienteMapper = grupoIngredienteMapper;
	}

	/**
	 * Crear un nuevo grupo de ingredientes
	 *
	 * @param grupoIngredienteDto
	 * @return ResponseEntity
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crea un nuevo grupo de ingrediente")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Grupo de ingrediente creado"),
			@ApiResponse(responseCode = "400", description = "Entrada incorrecta"),
			@ApiResponse(responseCode = "409", description = "Grupo de ingrediente ya existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> crear(
			@Validated(GrupoIngredienteDto.Crear.class) @RequestBody GrupoIngredienteDto grupoIngredienteDto)
			throws RegistroDuplicadoException, ReglaDeNegocioException {
		this.grupoIngredienteService.crear(this.grupoIngredienteMapper.dtoToDomain(grupoIngredienteDto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	/**
	 * Buscar todos los grupos
	 *
	 * @return ResponseEntity
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar los grupos de ingredientes")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con esos parámetros", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<List<GrupoIngredienteDto>> buscarTodos() {
		return ResponseEntity.ok(grupoIngredienteMapper.domainsToDtos(grupoIngredienteService.buscarTodos()));
	}

	/**
	 * Buscar grupo por ID
	 *
	 * @param id
	 * @return GrupoIngredienteDto
	 */
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar grupo por id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con ese id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<GrupoIngredienteDto> buscarPorId(@NotBlank @PathVariable Long id)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(grupoIngredienteMapper.domainToDto(grupoIngredienteService.buscarPorId(id)));
	}

	/**
	 * Actualizar un nuevo grupo de ingredientes
	 *
	 * @param grupoIngredienteDto
	 * @return ResponseEntity
	 */
	@PutMapping()
	@Operation(summary = "Actualizar un grupo de ingrediente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Grupo de ingredientes actualizada correctamente"),
			@ApiResponse(responseCode = "404", description = "Grupo de ingrediente no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> actualizar(
			@Validated(GrupoIngredienteDto.Actualizar.class) @RequestBody GrupoIngredienteDto grupoIngredienteDto)
			throws RegistroDuplicadoException, EntidadNoEncontradaException {
		this.grupoIngredienteService.actualizar(this.grupoIngredienteMapper.dtoToDomain(grupoIngredienteDto));
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * Buscar todos los grupos con paginacion y filtro
	 *
	 * @param filters consulta para filtro
	 * @param pager paged
	 * @return list GrupoIngredienteDto
	 */
	@PostMapping("/search")
	@Operation(summary = "Obtener Departamento con paginado por filtros")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "NomDepartamentos encontradas"),
			@ApiResponse(responseCode = "404", description = "NomDepartamentos no encontradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public Pagina<GrupoIngredienteDto> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			@RequestBody List<CriterioBusqueda> filters) {
		Pagina<GrupoIngrediente> page = grupoIngredienteService.obtenerGrupoIngredientePaginadoPorFiltros(pager,
				filters);
		return Pagina.<GrupoIngredienteDto> builder()
				.contenido(page.getContenido().stream().map(grupoIngredienteMapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}

}