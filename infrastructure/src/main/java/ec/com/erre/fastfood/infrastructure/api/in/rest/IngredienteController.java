package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.services.IngredienteService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.infrastructure.api.mappers.IngredienteMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.share.api.dtos.IngredienteDto;
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
@RequestMapping("/ingredientes")
@Tag(name = "Ingredientes", description = "Ingredientes")
public class IngredienteController {
	private final IngredienteService ingredienteService;
	private final IngredienteMapper ingredienteMapper;

	public IngredienteController(IngredienteService ingredienteService, IngredienteMapper ingredienteMapper) {
		this.ingredienteService = ingredienteService;
		this.ingredienteMapper = ingredienteMapper;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar todos los ingredientes")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ingredientes encontrados"),
			@ApiResponse(responseCode = "404", description = "No existen ingredientes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<List<IngredienteDto>> buscarTodos() {
		return ResponseEntity.ok(ingredienteMapper.domainsToDtos(ingredienteService.buscarTodos()));
	}

	@GetMapping(value = "/{ingredienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar ingrediente por id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ingrediente encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el ingrediente con ese id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<IngredienteDto> buscarPorId(@RequestParam Long ingredienteId) {
		return ResponseEntity.ok(ingredienteMapper.domainToDto(ingredienteService.buscarPorId(ingredienteId)));
	}

	@GetMapping(value = "/grupo/{grupoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar ingredientes por ID de Grupo")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ingredientes encontrados"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con ese ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<List<IngredienteDto>> buscarPorGrupoId(@RequestParam Long grupoId)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(ingredienteMapper.domainsToDtos(ingredienteService.buscarPorGrupoId(grupoId)));
	}

	@GetMapping(value = "/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar ingrediente por nombre (case-sesitive)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ingrediente encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el ingrediente con ese nombre", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<IngredienteDto> buscarPorEstado(@RequestParam String nombre) {
		return ResponseEntity.ok(ingredienteMapper.domainToDto(ingredienteService.buscarPorNombre(nombre)));
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crea un nuevo ingrediente")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Ingrediente creado"),
			@ApiResponse(responseCode = "400", description = "Entrada incorrecta"),
			@ApiResponse(responseCode = "409", description = "Ingrediente ya existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> crear(@Validated(IngredienteDto.Crear.class) @RequestBody IngredienteDto dto)
			throws RegistroDuplicadoException {
		this.ingredienteService.crear(this.ingredienteMapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	@PutMapping()
	@Operation(summary = "Actualizar un ingrediente")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ingrediente actualizada correctamente"),
			@ApiResponse(responseCode = "404", description = "Ingrediente no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> actualizar(
			@Validated(IngredienteDto.Actualizar.class) @RequestBody IngredienteDto dto) {
		this.ingredienteService.actualizar(this.ingredienteMapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}
}
