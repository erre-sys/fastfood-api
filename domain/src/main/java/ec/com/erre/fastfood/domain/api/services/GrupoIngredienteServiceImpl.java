package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.repositories.GrupoIngredienteRepository;
import ec.com.erre.fastfood.domain.commons.constants.FastFoodExceptionMessages;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
	public boolean existePorNombre(String nombre) {
		if (!StringUtils.hasText(nombre))
			return false;
		return repository.existePorNombre(nombre.trim());
	}

	@Override
	public GrupoIngrediente buscarPorNombre(String nombre) throws EntidadNoEncontradaException {
		if (!StringUtils.hasText(nombre)) {
			throw new IllegalArgumentException("El nombre es requerido");
		}
		return repository.buscarPorNombre(nombre.trim());
	}

	@Override
	public GrupoIngrediente buscarPorEstado(String estado) throws EntidadNoEncontradaException {
		if (!StringUtils.hasText(estado)) {
			throw new IllegalArgumentException("El estado es requerido");
		}
		return repository.buscarPorEstado(estado.trim());
	}

	@Override
	public GrupoIngrediente buscarPorNombreyEstado(String nombre, String estado) throws EntidadNoEncontradaException {
		if (!StringUtils.hasText(nombre)) {
			throw new IllegalArgumentException("El nombre es requerido");
		}
		if (!StringUtils.hasText(estado)) {
			throw new IllegalArgumentException("El estado es requerido");
		}
		return repository.buscarPorNombreyEstado(nombre.trim(), estado.trim().toUpperCase());
	}

	@Override
	public List<GrupoIngrediente> buscarActivos(String estado) {
		String normalized = StringUtils.hasText(estado) ? estado.trim().toUpperCase() : "A";
		return repository.buscarActivos(normalized);
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
