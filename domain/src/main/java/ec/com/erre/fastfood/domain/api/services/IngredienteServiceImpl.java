package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.api.repositories.GrupoIngredienteRepository;
import ec.com.erre.fastfood.domain.api.repositories.IngredienteRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IngredienteServiceImpl implements IngredienteService {
	private final GrupoIngredienteRepository grupoIngredienteRepository;
	private final IngredienteRepository ingredienteRepository;

	public IngredienteServiceImpl(GrupoIngredienteRepository grupoIngredienteRepository,
			IngredienteRepository ingredienteRepository) {
		this.grupoIngredienteRepository = grupoIngredienteRepository;
		this.ingredienteRepository = ingredienteRepository;
	}

	@Override
	public List<Ingrediente> buscarTodos() {
		return ingredienteRepository.buscarTodos();
	}

	@Override
	public Ingrediente buscarPorId(Long ingredienteId) {
		try {
			return ingredienteRepository.buscarPorId(ingredienteId);
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("No existe el ingrediente con ese id");
		}
	}

	@Override
	public List<Ingrediente> buscarPorGrupoId(Long grupoId) {
		try {
			grupoIngredienteRepository.buscarPorId(grupoId);
			return ingredienteRepository.buscarPorGrupoId(grupoId);
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("No existe el grupo");
		}
	}

	@Override
	public Ingrediente buscarPorNombre(String nombre) {
		if (!StringUtils.hasText(nombre))
			throw new IllegalArgumentException("El nombre es requerido");
		return ingredienteRepository.buscarPorNombre(nombre.trim().toUpperCase());
	}

	@Override
	@Transactional
	public void crear(Ingrediente ingrediente) throws RegistroDuplicadoException {
		try {
			grupoIngredienteRepository.buscarPorId(ingrediente.getGrupoIngredienteId());
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("El grupo no existe");
		}

		Ingrediente existente = ingredienteRepository.buscarPorNombre(ingrediente.getNombre().trim().toUpperCase());
		if (existente != null) {
			throw new ServiceException("Ya existe un ingrediente con ese nombre");
		}

		ingredienteRepository.crear(ingrediente);
	}

	@Override
	@Transactional
	public void actualizar(Ingrediente cambios) {
		Ingrediente actual;
		try {
			actual = ingredienteRepository.buscarPorId(cambios.getId());
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("No existe el ingrediente a actualizar");
		}

		Long grupoIdDestino = (cambios.getGrupoIngredienteId() != null) ? cambios.getGrupoIngredienteId()
				: actual.getGrupoIngredienteId();
		try {
			grupoIngredienteRepository.buscarPorId(grupoIdDestino);
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("El grupo destino no existe");
		}

		String nombreNuevo = StringUtils.hasText(cambios.getNombre()) ? cambios.getNombre().trim().toUpperCase()
				: actual.getNombre();

		if (!nombreNuevo.equalsIgnoreCase(actual.getNombre())
				|| !grupoIdDestino.equals(actual.getGrupoIngredienteId())) {
			try {
				Ingrediente homonimo = ingredienteRepository.buscarPorNombre(nombreNuevo);
				if (homonimo != null && !homonimo.getId().equals(actual.getId())) {
					throw new RegistroDuplicadoException("Ya existe otro ingrediente con ese nombre en el grupo");
				}
			} catch (RegistroDuplicadoException noExiste) {
				throw new ServiceException("Registro duplicado del nombre");
			}
		}

		actual.setNombre(nombreNuevo);
		if (cambios.getGrupoIngredienteId() != null) {
			actual.setGrupoIngredienteId(cambios.getGrupoIngredienteId());
		}

		ingredienteRepository.actualizar(actual);
	}
}
