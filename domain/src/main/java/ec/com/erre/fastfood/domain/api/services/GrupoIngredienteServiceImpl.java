package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.repositories.GrupoIngredienteRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoIngredienteServiceImpl implements GrupoIngredienteService {
	private final GrupoIngredienteRepository repository;

	public GrupoIngredienteServiceImpl(GrupoIngredienteRepository repository) {
		this.repository = repository;
	}

	@Override
	public void crear(GrupoIngrediente grupoIngrediente) throws RegistroDuplicadoException {
		if (repository.existePorNombre(grupoIngrediente.getNombre())) {
			throw new ServiceException("Duplicado");
		}
		this.repository.crear(grupoIngrediente);
	}

	@Override
	public List<GrupoIngrediente> buscarTodos() {
		return repository.buscarActivos();
	}

	@Override
	public GrupoIngrediente buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repository.buscarPorId(id);
	}

	@Override
	public Pagina<GrupoIngrediente> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters) {
		return repository.obtenerGrupoIngredientePaginadoPorFiltros(pager, filters);
	}

	@Override
	public void actualizar(GrupoIngrediente update) throws EntidadNoEncontradaException {
		GrupoIngrediente encontrado = repository.buscarPorId(update.getId());
		setearDatos(update, encontrado);
		this.repository.actualizar(encontrado);
	}

	@Override
	public void eliminarPorId(Long id) throws EntidadNoEncontradaException {
		GrupoIngrediente encontrado = repository.buscarPorId(id);
		this.repository.eliminar(encontrado);
	}

	private void setearDatos(GrupoIngrediente update, GrupoIngrediente encontrado) {
		encontrado.setEstado(update.getEstado() != null ? update.getEstado() : encontrado.getEstado());
		encontrado.setNombre(update.getNombre() != null ? update.getNombre() : encontrado.getNombre());
		encontrado.setAplicaComida(
				update.getAplicaComida() != null ? update.getAplicaComida() : encontrado.getAplicaComida());
	}
}
