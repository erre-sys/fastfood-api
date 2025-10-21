package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Inventario;
import ec.com.erre.fastfood.domain.api.models.api.InventarioMov;
import ec.com.erre.fastfood.domain.api.services.InventarioService;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.InventarioMapper;
import ec.com.erre.fastfood.infrastructure.api.mappers.KardexMapper;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.InventarioDto;
import ec.com.erre.fastfood.share.api.dtos.InventarioMovDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/inventario")
@Tag(name = "Inventario", description = "Inventario de items / ingredientes")
public class InventarioController {

	private final InventarioService inventarioService;
	private final InventarioMapper inventarioMapper;
	private final KardexMapper kardexMapper;

	public InventarioController(InventarioService inventarioService, InventarioMapper inventarioMapper,
			KardexMapper kardexMapper) {
		this.inventarioService = inventarioService;
		this.inventarioMapper = inventarioMapper;
		this.kardexMapper = kardexMapper;
	}

	/**
	 * Kardex de inventario (paginado) con filtro opcional por texto y bandera de "bajo mínimo".
	 */
	@PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar el inventario paginado")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Listado obtenido") })
	public Pagina<InventarioDto> search(PagerAndSortDto pager, @RequestParam(required = false) String q,
			@RequestParam(defaultValue = "false") boolean soloBajoMinimo) {

		Pagina<Inventario> page = inventarioService.listarInventario(pager, q, soloBajoMinimo);

		return Pagina.<InventarioDto> builder().paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).totalRegistros(page.getTotalRegistros())
				.contenido(page.getContenido().stream().map(inventarioMapper::domaintoDto).toList()).build();
	}

	/**
	 * Kardex (movimientos) paginado por ingrediente y rango de fechas opcional.
	 */
	@PostMapping(value = "/kardex/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Kardex (movimientos) paginado")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Listado obtenido"),
			@ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
	public Pagina<InventarioMovDto> kardexSearch(PagerAndSortDto pager, @RequestParam @NotNull Long ingredienteId,
			@RequestParam(required = false) String desde, // ISO-8601 (yyyy-MM-dd'T'HH:mm:ss)
			@RequestParam(required = false) String hasta, // ISO-8601
			@RequestParam(required = false) String tipo // COMPRA/CONSUMO/AJUSTE
	) throws ReglaDeNegocioException {

		LocalDateTime fDesde = desde != null && !desde.isBlank() ? LocalDateTime.parse(desde) : null;
		LocalDateTime fHasta = hasta != null && !hasta.isBlank() ? LocalDateTime.parse(hasta) : null;

		if (fDesde != null && fHasta != null && fDesde.isAfter(fHasta)) {
			throw new ReglaDeNegocioException("El parámetro 'desde' no puede ser mayor que 'hasta'");
		}

		Pagina<InventarioMov> page = inventarioService.listarKardex(ingredienteId, fDesde, fHasta, tipo, pager);

		return Pagina.<InventarioMovDto> builder().paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).totalRegistros(page.getTotalRegistros())
				.contenido(page.getContenido().stream().map(kardexMapper::domaintoDto).toList()).build();
	}
}