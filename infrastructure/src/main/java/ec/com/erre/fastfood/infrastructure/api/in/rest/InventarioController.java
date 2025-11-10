package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Inventario;
import ec.com.erre.fastfood.domain.api.models.api.InventarioMov;
import ec.com.erre.fastfood.domain.api.services.InventarioProcesoService;
import ec.com.erre.fastfood.domain.api.services.InventarioService;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.InventarioMapper;
import ec.com.erre.fastfood.infrastructure.api.mappers.KardexMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.infrastructure.commons.mappers.PaginaMapper;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.AjusteInventarioDto;
import ec.com.erre.fastfood.share.api.dtos.InventarioDto;
import ec.com.erre.fastfood.share.api.dtos.InventarioMovDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/inventario")
@Tag(name = "Inventario", description = "Inventario de items / ingredientes")
public class InventarioController {

	private final InventarioService inventarioService;
	private final InventarioProcesoService inventarioProcesoService;
	private final InventarioMapper inventarioMapper;
	private final KardexMapper kardexMapper;

	public InventarioController(InventarioService inventarioService, InventarioProcesoService inventarioProcesoService,
			InventarioMapper inventarioMapper, KardexMapper kardexMapper) {
		this.inventarioService = inventarioService;
		this.inventarioProcesoService = inventarioProcesoService;
		this.inventarioMapper = inventarioMapper;
		this.kardexMapper = kardexMapper;
	}

	@PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar el inventario paginado")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Listado obtenido") })
	public Pagina<InventarioDto> buscar(PagerAndSortDto pager, @RequestParam(required = false) String q,
			@RequestParam(defaultValue = "false") boolean soloBajoMinimo) {

		Pagina<Inventario> paginaInventario = inventarioService.listarInventario(pager, q, soloBajoMinimo);
		return PaginaMapper.map(paginaInventario, inventarioMapper::domaintoDto);
	}

	@PostMapping(value = "/kardex/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Kardex (movimientos) paginado")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Listado obtenido"),
			@ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public Pagina<InventarioMovDto> buscarKardex(PagerAndSortDto pager, @RequestParam @NotNull Long ingredienteId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime desde,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime hasta,
			@RequestParam(required = false) String tipo) throws ReglaDeNegocioException {

		if (desde != null && hasta != null && desde.isAfter(hasta)) {
			throw new ReglaDeNegocioException("El parámetro 'desde' no puede ser mayor que 'hasta'");
		}

		Pagina<InventarioMov> paginaKardex = inventarioService.listarKardex(ingredienteId, desde, hasta, tipo, pager);
		return PaginaMapper.map(paginaKardex, kardexMapper::domaintoDto);
	}

	@PostMapping(value = "/ajustar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Ajustar inventario manualmente (entrada/salida)", description = "Permite sumar o restar stock de un ingrediente. Cantidad positiva = entrada, cantidad negativa = salida.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ajuste realizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Error de validación o stock insuficiente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public ResponseEntity<Void> ajustar(@Valid @RequestBody AjusteInventarioDto dto) throws ReglaDeNegocioException {
		boolean permitirNegativo = dto.getPermitirNegativo() != null ? dto.getPermitirNegativo() : false;
		inventarioProcesoService.ajustar(dto);
		return ResponseEntity.ok().build();
	}
}