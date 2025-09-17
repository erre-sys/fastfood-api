package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;

import java.util.List;

/**
 * <b>Repositorio de platos </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PlatoRepository {

	List<Plato> buscarTodos();

	Plato buscarPorId(Long platoId) throws EntidadNoEncontradaException;

	List<Plato> buscarPorGrupoId(Long grupoId);

	Plato buscarPorNombre(String nombre);

	void crear(Plato plato);

	void actualizar(Plato plato);

}