package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;

import java.util.List;

/**
 * <b>Repositorio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface IngredienteRepository {

	List<Ingrediente> buscarTodos();

	Ingrediente buscarPorId(Long ingredienteId) throws EntidadNoEncontradaException;

	List<Ingrediente> buscarPorGrupoId(Long grupoId);

	Ingrediente buscarPorNombre(String nombre);

	void crear(Ingrediente ingrediente);

	void actualizar(Ingrediente ingrediente);

}