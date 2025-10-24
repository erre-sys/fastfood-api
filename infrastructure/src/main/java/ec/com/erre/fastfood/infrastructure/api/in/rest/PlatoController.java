package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.services.PlatoService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.PlatoMapper;
import ec.com.erre.fastfood.infrastructure.commons.mappers.PaginaMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.PlatoDto;
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
@RequestMapping("/platos")
@Tag(name = "Platos", description = "Gestión de platos del menú")
public class PlatoController {

	private final PlatoService service;
	private final PlatoMapper mapper;

	public PlatoController(PlatoService service, PlatoMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crear plato")
	public ResponseEntity<Map<String, Long>> crear(@Validated(PlatoDto.Crear.class) @RequestBody PlatoDto dto)
			throws RegistroDuplicadoException, ReglaDeNegocioException, EntidadNoEncontradaException {
		Plato plato = mapper.dtoToDomain(dto);
		service.crear(plato);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Actualizar plato")
	public ResponseEntity<Void> actualizar(@Validated(PlatoDto.Actualizar.class) @RequestBody PlatoDto dto)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException {
		Plato plato = mapper.dtoToDomain(dto);
		service.actualizar(plato);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obtener plato por id")
	public ResponseEntity<PlatoDto> obtenerPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		Plato plato = service.buscarPorId(id);
		return ResponseEntity.ok(mapper.domainToDto(plato));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar platos activos")
	public ResponseEntity<List<PlatoDto>> listarActivos() {
		List<Plato> platos = service.activos();
		List<PlatoDto> platosDto = platos.stream().map(mapper::domainToDto).toList();
		return ResponseEntity.ok(platosDto);
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar platos (paginado por filtros)")
	public Pagina<PlatoDto> buscar(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<Plato> paginaPlatos = service.obtenerPaginadoPorFiltros(pager, filters);
		return PaginaMapper.map(paginaPlatos, mapper::domainToDto);
	}
}
