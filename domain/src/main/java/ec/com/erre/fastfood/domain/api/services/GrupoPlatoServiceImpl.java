package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoPlato;
import ec.com.erre.fastfood.domain.api.repositories.GrupoPlatoRepository;
import ec.com.erre.fastfood.domain.commons.constants.FastFoodExceptionMessages;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.PagerAndSortDto;
import ec.com.erre.fastfood.infrastructure.commons.repositories.Pagina;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoPlatoServiceImpl implements GrupoPlatoService {
	private final GrupoPlatoRepository repository;
	private final MessageSource messageSource;

	public GrupoPlatoServiceImpl(GrupoPlatoRepository repository, MessageSource messageSource) {
		this.repository = repository;
		this.messageSource = messageSource;
	}

	@Override
	public void crear(GrupoPlato grupoPlato) throws RegistroDuplicadoException {
		if (repository.existePorNombre(grupoPlato.getNombre())) {
			throw new RegistroDuplicadoException(
					messageSource.getMessage(FastFoodExceptionMessages.ERROR_EXISTE_PARAMETROS,
							new Object[] { grupoPlato.getNombre() }, LocaleContextHolder.getLocale()));
		}
		this.repository.crear(grupoPlato);
	}

	@Override
	public List<GrupoPlato> buscarTodos() {
		return repository.buscarActivos();
	}

	@Override
	public GrupoPlato buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repository.buscarPorId(id);
	}

	@Override
	public Pagina<GrupoPlato> obtenerGrupoPlatoPaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters) {
		return repository.obtenerGrupoPlatoPaginadoPorFiltros(pager, filters);
	}

	@Override
	public void actualizar(GrupoPlato grupoPlato) throws EntidadNoEncontradaException, RegistroDuplicadoException {
		if (!repository.existePorNombre(grupoPlato.getNombre())) {
			throw new ServiceException(
					String.format("Ya existe un grupo de platos con el nombre %s", grupoPlato.getNombre()));
		}
		GrupoPlato encontrado = repository.buscarPorId(grupoPlato.getId());
		if (null == encontrado) {
			throw new ServiceException(
					String.format("No existe un grupo de platos con el id %s", grupoPlato.getNombre()));
		}
		setearDatos(grupoPlato, encontrado);

		this.repository.actualizar(encontrado);
	}

	private void setearDatos(GrupoPlato grupoPlato, GrupoPlato encontrado) {
		encontrado.setEstado(grupoPlato.getEstado());
		encontrado.setNombre(grupoPlato.getNombre());
	}
}
