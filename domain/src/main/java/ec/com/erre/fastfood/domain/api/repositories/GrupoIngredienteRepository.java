package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;

import java.util.List;

/**
 * <b>Repositorio de grupo de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface GrupoIngredienteRepository {

	/**
	 * Busca si existe un grupo por nombre
	 *
	 * @param nombre
	 * @return si o no
	 * @author eduardo.romero
	 * @version $1.0$
	 */
	boolean existePorNombre(String nombre);

	/**
	 * Busca y devuelve si existe un grupo por id
	 *
	 * @param id
	 * @return GrupoIngrediente
	 * @author eduardo.romero
	 */
	GrupoIngrediente buscarPorId(Long id) throws EntidadNoEncontradaException;

	/**
	 * Busca y devuelve si existe un grupo por nombre
	 *
	 * @param nombre
	 * @return GrupoIngrediente
	 * @author eduardo.romero
	 */
	GrupoIngrediente buscarPorNombre(String nombre) throws EntidadNoEncontradaException;

	/**
	 * Busca y devuelve si existe un grupo por nombre y por estado
	 *
	 * @param estado
	 * @return GrupoIngrediente
	 * @author eduardo.romero
	 */
	GrupoIngrediente buscarPorEstado(String estado) throws EntidadNoEncontradaException;

	/**
	 * Busca y devuelve si existe un grupo por nombre y por estado
	 *
	 * @param nombre
	 * @param estado
	 * @return GrupoIngrediente
	 * @author eduardo.romero
	 */
	GrupoIngrediente buscarPorNombreyEstado(String nombre, String estado) throws EntidadNoEncontradaException;

	/**
	 * Busca y devuelve el listado de grupos por estado
	 *
	 * @param estado
	 * @return List<GrupoIngrediente>
	 * @author eduardo.romero
	 */
	List<GrupoIngrediente> buscarActivos(String estado);

	/**
	 * Crear un grupo de ingredientes
	 *
	 * @param grupoIngrediente
	 * @author eduardo.romero
	 */
	void crear(GrupoIngrediente grupoIngrediente) throws RegistroDuplicadoException;

	/**
	 * Actualizar un grupo de ingredientes
	 *
	 * @param grupoIngrediente
	 * @author eduardo.romero
	 */
	void actualizar(GrupoIngrediente grupoIngrediente) throws EntidadNoEncontradaException;

}