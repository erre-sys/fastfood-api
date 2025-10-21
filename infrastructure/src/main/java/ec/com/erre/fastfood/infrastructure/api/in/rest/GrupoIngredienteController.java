package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.services.GrupoIngredienteService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.GrupoIngredienteMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.GrupoIngredienteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

	private final GrupoIngredienteService service;
	private final GrupoIngredienteMapper mapper;

	public GrupoIngredienteController(GrupoIngredienteService service, GrupoIngredienteMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crea un nuevo grupo de ingrediente")
	public ResponseEntity<Void> crear(@Validated(GrupoIngredienteDto.Crear.class) @RequestBody GrupoIngredienteDto dto)
			throws RegistroDuplicadoException, ReglaDeNegocioException {
		service.crear(mapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar los grupos de ingredientes activos")
	public ResponseEntity<List<GrupoIngredienteDto>> buscarTodos() {
		return ResponseEntity.ok(mapper.domainsToDtos(service.buscarTodos()));
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar grupo por id")
	public ResponseEntity<GrupoIngredienteDto> buscarPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(mapper.domainToDto(service.buscarPorId(id)));
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Actualizar un grupo de ingrediente")
	public ResponseEntity<Void> actualizar(
			@Validated(GrupoIngredienteDto.Actualizar.class) @RequestBody GrupoIngredienteDto dto)
			throws RegistroDuplicadoException, EntidadNoEncontradaException, ReglaDeNegocioException {
		service.actualizar(mapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Eliminar grupo por id")
	public ResponseEntity<Void> eliminarPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		service.eliminarPorId(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obtener grupos con paginado por filtros")
	public Pagina<GrupoIngredienteDto> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			@RequestBody List<CriterioBusqueda> filters) {
		Pagina<GrupoIngrediente> page = service.obtenerGrupoIngredientePaginadoPorFiltros(pager, filters);
		return Pagina.<GrupoIngredienteDto> builder()
				.contenido(page.getContenido().stream().map(mapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}
}
