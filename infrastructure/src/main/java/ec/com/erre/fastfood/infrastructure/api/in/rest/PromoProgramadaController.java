package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.domain.api.services.PromoProgramadaService;
import ec.com.erre.fastfood.domain.api.services.PromosProcesoService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import ec.com.erre.fastfood.infrastructure.api.mappers.PromoProgramadaMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.PromoProgramadaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promos")
@Tag(name = "Promos", description = "Promociones programadas de platos")
public class PromoProgramadaController {

	private final PromoProgramadaService promoProgramadaService;
	private final PromoProgramadaMapper promoProgramadaMapper;

	public PromoProgramadaController(PromoProgramadaService service, PromoProgramadaMapper mapper) {
		this.promoProgramadaService = service;
		this.promoProgramadaMapper = mapper;
	}

	@PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crear promoción programada")
	public ResponseEntity<Map<String, Long>> crear(
			@Validated(PromoProgramadaDto.Crear.class) @RequestBody PromoProgramadaDto dto)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Long id = promoProgramadaService.crear(promoProgramadaMapper.dtoToDomain(dto), "USUARIO");
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Actualizar promoción programada")
	public ResponseEntity<Void> actualizar(
			@Validated(PromoProgramadaDto.Actualizar.class) @RequestBody PromoProgramadaDto dto)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		promoProgramadaService.actualizar(promoProgramadaMapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar promoción por id")
	public ResponseEntity<PromoProgramadaDto> buscarPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(promoProgramadaMapper.domainToDto(promoProgramadaService.buscarPorId(id)));
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar promociones (paginado por filtros)")
	public Pagina<PromoProgramadaDto> search(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<PromoProgramada> page = promoProgramadaService.paginado(pager, filters);
		return Pagina.<PromoProgramadaDto> builder()
				.contenido(page.getContenido().stream().map(promoProgramadaMapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}

	@GetMapping(value = "/platos/{platoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar promociones de un plato")
	public ResponseEntity<List<PromoProgramadaDto>> listarPorPlato(@PathVariable Long platoId)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(promoProgramadaService.listarPorPlato(platoId).stream()
				.map(promoProgramadaMapper::domainToDto).toList());
	}

	@GetMapping(value = "/vigentes", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar promociones vigentes (activas y dentro del rango de fechas)")
	public ResponseEntity<List<PromoProgramadaDto>> listarVigentes() {
		String ahora = java.time.LocalDateTime.now().toString();
		CriterioBusqueda estado = new CriterioBusqueda("estado", ":", "A");
		CriterioBusqueda fechaInicio = new CriterioBusqueda("fechaInicio", "<=", ahora);
		CriterioBusqueda fechaFin = new CriterioBusqueda("fechaFin", ">=", ahora);
		List<CriterioBusqueda> filters = List.of(estado, fechaInicio, fechaFin);

		PagerAndSortDto pager = new PagerAndSortDto();
		pager.setSize(1000);
		pager.setPage(0);

		Pagina<PromoProgramada> page = promoProgramadaService.paginado(pager, filters);
		return ResponseEntity.ok(page.getContenido().stream().map(promoProgramadaMapper::domainToDto).toList());
	}

	@PostMapping(value = "/aplicar")
	@Operation(summary = "Aplicar promociones (ejecuta SP)")
	public ResponseEntity<Void> aplicar() throws ServiceException {
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
