package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.GrupoPlato;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;

import java.util.List;

/**
 * <b>Repositorio de grupo de platos </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface GrupoPlatoRepository {

	/**
	 * Crear un grupo de platos
	 *
	 * @param grupoPlato
	 * @author eduardo.romero
	 */
	void crear(GrupoPlato grupoPlato) throws RegistroDuplicadoException;

	/**
	 * Busca y devuelve el listado de grupos por estado
	 *
	 * @return List<GrupoPlato>
	 * @author eduardo.romero
	 */
	List<GrupoPlato> buscarActivos();

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
	 * @return GrupoPlato
	 * @author eduardo.romero
	 */
	GrupoPlato buscarPorId(Long id) throws EntidadNoEncontradaException;

	/**
	 * Buscar todos los grupos con paginacion y filtro
	 *
	 * @param filters consulta para filtro
	 * @param pager paged
	 * @return list GrupoPlatoDto
	 */
	Pagina<GrupoPlato> obtenerGrupoPlatoPaginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);

	/**
	 * Actualizar un grupo de platos
	 *
	 * @param grupoPlato
	 * @author eduardo.romero
	 */
	void actualizar(GrupoPlato grupoPlato) throws EntidadNoEncontradaException;

}