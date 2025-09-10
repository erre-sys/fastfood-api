package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.services.GrupoIngredienteService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.GrupoIngredienteMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.share.api.dtos.GrupoIngredienteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

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

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar los grupos de ingredientes")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con esos parámetros", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<List<GrupoIngredienteDto>> listarPorEstado(@RequestParam(required = false) String estado) {
		return ResponseEntity.ok(grupoIngredienteMapper.domainsToDtos(grupoIngredienteService.buscarActivos(estado)));
	}

	@GetMapping(value = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar grupo por nombre")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con ese nombre", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<GrupoIngredienteDto> buscarPorNombre(@RequestParam String nombre)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(grupoIngredienteMapper.domainToDto(grupoIngredienteService.buscarPorNombre(nombre)));
	}

	@GetMapping(value = "/buscar-estado", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar grupo por estado")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con ese nombre", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<GrupoIngredienteDto> buscarPorEstado(@RequestParam String nombre)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(grupoIngredienteMapper.domainToDto(grupoIngredienteService.buscarPorEstado(nombre)));
	}

	@GetMapping(value = "/buscar-nombre-estado", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar grupo por nombre y estado")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "No existe el grupo con esos parámetros", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<GrupoIngredienteDto> buscarPorNombreYEstado(@RequestParam String nombre,
			@RequestParam String estado) throws EntidadNoEncontradaException {

		return ResponseEntity
				.ok(grupoIngredienteMapper.domainToDto(grupoIngredienteService.buscarPorNombreyEstado(nombre, estado)));
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crea un nuevo grupo de ingrediente")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Grupo de ingrediente creado"),
			@ApiResponse(responseCode = "400", description = "Entrada incorrecta"),
			@ApiResponse(responseCode = "409", description = "Grupo de ingrediente ya existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> crear(@Validated(GrupoIngredienteDto.Crear.class) @RequestBody GrupoIngredienteDto dto)
			throws RegistroDuplicadoException, ReglaDeNegocioException {
		this.grupoIngredienteService.crear(this.grupoIngredienteMapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	@PutMapping()
	@Operation(summary = "Actualizar un grupo de ingrediente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Grupo de ingredientes actualizada correctamente"),
			@ApiResponse(responseCode = "404", description = "Grupo de ingrediente no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> actualizar(
			@Validated(GrupoIngredienteDto.Actualizar.class) @RequestBody GrupoIngredienteDto dto)
			throws RegistroDuplicadoException, EntidadNoEncontradaException {
		this.grupoIngredienteService.actualizar(this.grupoIngredienteMapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);

	}
}
