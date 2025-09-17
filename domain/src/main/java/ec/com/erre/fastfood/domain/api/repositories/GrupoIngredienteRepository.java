package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.PagerAndSortDto;
import ec.com.erre.fastfood.infrastructure.commons.repositories.Pagina;

import java.util.List;

/**
 * <b>Repositorio de grupo de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface GrupoIngredienteRepository {

	/**
	 * Crear un grupo de ingredientes
	 *
	 * @param grupoIngrediente
	 * @author eduardo.romero
	 */
	void crear(GrupoIngrediente grupoIngrediente) throws RegistroDuplicadoException;

	/**
	 * Busca y devuelve el listado de grupos por estado
	 *
	 * @return List<GrupoIngrediente>
	 * @author eduardo.romero
	 */
	List<GrupoIngrediente> buscarActivos();

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
	 * Buscar todos los grupos con paginacion y filtro
	 *
	 * @param filters consulta para filtro
	 * @param pager paged
	 * @return list GrupoIngredienteDto
	 */
	Pagina<GrupoIngrediente> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters);

	/**
	 * Actualizar un grupo de ingredientes
	 *
	 * @param grupoIngrediente
	 * @author eduardo.romero
	 */
	void actualizar(GrupoIngrediente grupoIngrediente) throws EntidadNoEncontradaException;

	void eliminar(GrupoIngrediente encontrado);
}