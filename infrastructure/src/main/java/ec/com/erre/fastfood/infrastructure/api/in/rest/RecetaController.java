package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.RecetaItem;
import ec.com.erre.fastfood.domain.api.services.RecetaService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.RecetaItemMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.share.api.dtos.RecetaItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platos/{platoId}/receta")
@Tag(name = "Receta", description = "Gestión de receta por plato")
public class RecetaController {

	private final RecetaService recetaService;
	private final RecetaItemMapper recetaItemMapper;

	public RecetaController(RecetaService recetaService, RecetaItemMapper recetaItemMapper) {
		this.recetaService = recetaService;
		this.recetaItemMapper = recetaItemMapper;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar receta vigente de un plato")
	public ResponseEntity<List<RecetaItemDto>> listar(@PathVariable Long platoId) throws EntidadNoEncontradaException {
		List<RecetaItem> receta = recetaService.listarPorPlato(platoId);
		return ResponseEntity.ok(receta.stream().map(recetaItemMapper::domainToDto).toList());
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Reemplazar receta completa del plato")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Receta reemplazada"),
			@ApiResponse(responseCode = "404", description = "Plato o ingrediente no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Regla de negocio violada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> reemplazar(@PathVariable Long platoId, @RequestBody List<RecetaItemDto> items)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		List<RecetaItem> domain = items == null ? List.of()
				: items.stream().map(recetaItemMapper::dtoToDomain).peek(i -> i.setPlatoId(platoId)).toList();
		recetaService.reemplazarReceta(platoId, domain);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
