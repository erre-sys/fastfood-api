package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.services.PlatoService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.infrastructure.api.mappers.PlatoMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.share.api.dtos.PlatoDto;
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
@RequestMapping("/platos")
@Tag(name = "Platos", description = "Platos")
public class PlatoController {
	private final PlatoService platoService;
	private final PlatoMapper platoMapper;

	public PlatoController(PlatoService platoService, PlatoMapper platoMapper) {
		this.platoService = platoService;
		this.platoMapper = platoMapper;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar todos los platos")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Platos encontrados"),
			@ApiResponse(responseCode = "404", description = "No existen platos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<List<PlatoDto>> buscarTodos() {
		return ResponseEntity.ok(platoMapper.domainsToDtos(platoService.buscarTodos()));
	}

	@GetMapping(value = "/{platoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar plato por id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Plato encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el plato con ese id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<PlatoDto> buscarPorId(@RequestParam Long platoId) {
		return ResponseEntity.ok(platoMapper.domainToDto(platoService.buscarPorId(platoId)));
	}

	@GetMapping(value = "/grupo/{grupoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar platos por ID de Grupo")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Platos encontrados"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con ese ID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<List<PlatoDto>> buscarPorGrupoId(@PathVariable Long grupoId)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(platoMapper.domainsToDtos(platoService.buscarPorGrupoId(grupoId)));
	}

	@GetMapping(value = "/nombre/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar plato por nombre (case-sesitive)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Plato encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el plato con ese nombre", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<PlatoDto> buscarPorEstado(@NotBlank @RequestParam String nombre) {
		return ResponseEntity.ok(platoMapper.domainToDto(platoService.buscarPorNombre(nombre)));
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crea un nuevo plato")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Plato creado"),
			@ApiResponse(responseCode = "400", description = "Entrada incorrecta"),
			@ApiResponse(responseCode = "409", description = "Plato ya existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> crear(@Validated(PlatoDto.Crear.class) @RequestBody PlatoDto dto)
			throws RegistroDuplicadoException {
		this.platoService.crear(this.platoMapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	@PutMapping()
	@Operation(summary = "Actualizar un plato")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Plato actualizada correctamente"),
			@ApiResponse(responseCode = "404", description = "Plato no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> actualizar(@Validated(PlatoDto.Actualizar.class) @RequestBody PlatoDto dto) {
		this.platoService.actualizar(this.platoMapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}
}
