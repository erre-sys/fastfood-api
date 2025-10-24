package ec.com.erre.fastfood.infrastructure.api.in.rest;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.api.services.IngredienteService;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.mappers.IngredienteMapper;
import ec.com.erre.fastfood.infrastructure.commons.mappers.PaginaMapper;
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
		Ingrediente ingrediente = mapper.dtoToDomain(dto);
		service.crear(ingrediente);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Actualizar ingrediente")
	public ResponseEntity<Void> actualizar(@Validated(IngredienteDto.Actualizar.class) @RequestBody IngredienteDto dto)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException {
		Ingrediente ingrediente = mapper.dtoToDomain(dto);
		service.actualizar(ingrediente);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Obtener ingrediente por id")
	public ResponseEntity<IngredienteDto> obtenerPorId(@PathVariable Long id) throws EntidadNoEncontradaException {
		Ingrediente ingrediente = service.buscarPorId(id);
		return ResponseEntity.ok(mapper.domainToDto(ingrediente));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Listar ingredientes activos")
	public ResponseEntity<List<IngredienteDto>> listarActivos() {
		List<Ingrediente> ingredientes = service.activos();
		List<IngredienteDto> ingredientesDto = ingredientes.stream().map(mapper::domainToDto).toList();
		return ResponseEntity.ok(ingredientesDto);
	}

	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Eliminar ingrediente (reglas de stock/movimientos)")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		service.eliminarPorId(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Buscar ingredientes (paginado por filtros)")
	public Pagina<IngredienteDto> buscar(PagerAndSortDto pager, @RequestBody List<CriterioBusqueda> filters) {
		Pagina<Ingrediente> paginaIngredientes = service.paginado(pager, filters);
		return PaginaMapper.map(paginaIngredientes, mapper::domainToDto);
	}
}
