package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.repositories.GrupoIngredienteRepository;
import ec.com.erre.fastfood.domain.commons.constants.FastFoodExceptionMessages;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.PagerAndSortDto;
import ec.com.erre.fastfood.infrastructure.commons.repositories.Pagina;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoIngredienteServiceImpl implements GrupoIngredienteService {
	private final GrupoIngredienteRepository repository;
	private final MessageSource messageSource;

	public GrupoIngredienteServiceImpl(GrupoIngredienteRepository repository, MessageSource messageSource) {
		this.repository = repository;
		this.messageSource = messageSource;
	}

	@Override
	public void crear(GrupoIngrediente grupoIngrediente) throws RegistroDuplicadoException {
		if (repository.existePorNombre(grupoIngrediente.getNombre())) {
			throw new RegistroDuplicadoException(
					messageSource.getMessage(FastFoodExceptionMessages.ERROR_EXISTE_PARAMETROS,
							new Object[] { grupoIngrediente.getNombre() }, LocaleContextHolder.getLocale()));
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
	public void actualizar(GrupoIngrediente grupoIngrediente)
			throws EntidadNoEncontradaException, RegistroDuplicadoException {
		if (!repository.existePorNombre(grupoIngrediente.getNombre())) {
			throw new RegistroDuplicadoException(messageSource.getMessage(FastFoodExceptionMessages.ERROR_NO_EXISTE,
					new Object[] { grupoIngrediente.getNombre() }, LocaleContextHolder.getLocale()));
		}
		GrupoIngrediente encontrado = repository.buscarPorId(grupoIngrediente.getId());
		if (null == encontrado) {
			throw new EntidadNoEncontradaException(messageSource.getMessage(FastFoodExceptionMessages.ERROR_NO_EXISTE,
					new Object[] { grupoIngrediente.getNombre() }, LocaleContextHolder.getLocale()));
		}
		setearDatos(grupoIngrediente, encontrado);

		this.repository.actualizar(encontrado);
	}

	private void setearDatos(GrupoIngrediente grupoIngrediente, GrupoIngrediente encontrado) {
		encontrado.setEstado(grupoIngrediente.getEstado());
		encontrado.setNombre(grupoIngrediente.getNombre());
	}
}
