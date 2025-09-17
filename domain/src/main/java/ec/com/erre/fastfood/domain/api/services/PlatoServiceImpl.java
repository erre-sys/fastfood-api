package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.repositories.GrupoPlatoRepository;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PlatoServiceImpl implements PlatoService {
	private final GrupoPlatoRepository grupoPlatoRepository;
	private final PlatoRepository platoRepository;

	public PlatoServiceImpl(GrupoPlatoRepository grupoPlatoRepository, PlatoRepository platoRepository) {
		this.grupoPlatoRepository = grupoPlatoRepository;
		this.platoRepository = platoRepository;
	}

	@Override
	public List<Plato> buscarTodos() {
		return platoRepository.buscarTodos();
	}

	@Override
	public Plato buscarPorId(Long platoId) {
		try {
			return platoRepository.buscarPorId(platoId);
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("No existe el plato con ese id");
		}
	}

	@Override
	public List<Plato> buscarPorGrupoId(Long grupoId) {
		try {
			grupoPlatoRepository.buscarPorId(grupoId);
			return platoRepository.buscarPorGrupoId(grupoId);
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("No existe el grupo");
		}
	}

	@Override
	public Plato buscarPorNombre(String nombre) {
		if (!StringUtils.hasText(nombre))
			throw new IllegalArgumentException("El nombre es requerido");
		return platoRepository.buscarPorNombre(nombre.trim().toUpperCase());
	}

	@Override
	@Transactional
	public void crear(Plato plato) throws RegistroDuplicadoException {
		try {
			grupoPlatoRepository.buscarPorId(plato.getGrupoPlatoId());
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("El grupo no existe");
		}

		Plato existente = platoRepository.buscarPorNombre(plato.getNombre().trim().toUpperCase());
		if (existente != null) {
			throw new ServiceException("Ya existe un plato con ese nombre");
		}

		platoRepository.crear(plato);
	}

	@Override
	@Transactional
	public void actualizar(Plato cambios) {
		Plato actual;
		try {
			actual = platoRepository.buscarPorId(cambios.getId());
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("No existe el plato a actualizar");
		}

		Long grupoIdDestino = (cambios.getGrupoPlatoId() != null) ? cambios.getGrupoPlatoId()
				: actual.getGrupoPlatoId();
		try {
			grupoPlatoRepository.buscarPorId(grupoIdDestino);
		} catch (EntidadNoEncontradaException e) {
			throw new ServiceException("El grupo destino no existe");
		}

		String nombreNuevo = StringUtils.hasText(cambios.getNombre()) ? cambios.getNombre().trim().toUpperCase()
				: actual.getNombre();

		if (!nombreNuevo.equalsIgnoreCase(actual.getNombre()) || !grupoIdDestino.equals(actual.getGrupoPlatoId())) {
			try {
				Plato homonimo = platoRepository.buscarPorNombre(nombreNuevo);
				if (homonimo != null && !homonimo.getId().equals(actual.getId())) {
					throw new RegistroDuplicadoException("Ya existe otro plato con ese nombre en el grupo");
				}
			} catch (RegistroDuplicadoException noExiste) {
				throw new ServiceException("Registro duplicado del nombre");
			}
		}

		actual.setNombre(nombreNuevo);
		if (cambios.getGrupoPlatoId() != null) {
			actual.setGrupoPlatoId(cambios.getGrupoPlatoId());
		}

		platoRepository.actualizar(actual);
	}
}
