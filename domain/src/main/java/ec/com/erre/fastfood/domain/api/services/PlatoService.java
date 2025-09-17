package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;

import java.util.List;

/**
 * <b>Servicio de platos </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PlatoService {

	List<Plato> buscarTodos();

	Plato buscarPorId(Long platoId);

	List<Plato> buscarPorGrupoId(Long grupoId) throws EntidadNoEncontradaException;

	Plato buscarPorNombre(String nombre);

	void crear(Plato plato) throws RegistroDuplicadoException;

	void actualizar(Plato plato);
}