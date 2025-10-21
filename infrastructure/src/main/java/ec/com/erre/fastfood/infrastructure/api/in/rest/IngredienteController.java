package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.api.services.IngredienteService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.IngredienteMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.api.dtos.IngredienteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredientes")
@Tag(name = "Ingredientes", description = "Gestión de ingredientes y misceláneos (aplica_comida)")
public class IngredienteController {

	private final IngredienteService service;
	private final IngredienteMapper mapper;

	public IngredienteController(IngredienteService service, IngredienteMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Crear ingrediente")
	public ResponseEntity<Void> crear(@Validated(IngredienteDto.Crear.class) @RequestBody IngredienteDto dto)
			throws RegistroDuplicadoException, ReglaDeNegocioException, EntidadNoEncontradaException {
		service.crear(mapper.dtoToDomain(dto));
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Actualizar ingrediente")
	public ResponseEntity<Void> actualizar(@Validated(IngredienteDto.Actualizar.class) @RequestBody IngredienteDto dto)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException {
		service.actualizar(mapper.dtoToDomain(dto));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar ingrediente por id")
	public ResponseEntity<IngredienteDto> buscarPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(mapper.domainToDto(service.buscarPorId(id)));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar ingredientes activos")
	public ResponseEntity<List<IngredienteDto>> activos() {
		return ResponseEntity.ok(service.activos().stream().map(mapper::domainToDto).toList());
	}

	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Eliminar ingrediente (reglas de stock/movimientos)")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		service.eliminarPorId(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar ingredientes (paginado por filtros)")
	public Pagina<IngredienteDto> search(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<Ingrediente> page = service.paginado(pager, filters);
		return Pagina.<IngredienteDto> builder()
				.contenido(page.getContenido().stream().map(mapper::domainToDto).toList())
				.totalRegistros(page.getTotalRegistros()).paginaActual(page.getPaginaActual())
				.totalpaginas(page.getTotalpaginas()).build();
	}
}
