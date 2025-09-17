package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoPlato;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.PagerAndSortDto;
import ec.com.erre.fastfood.infrastructure.commons.repositories.Pagina;

import java.util.List;

/**
 * <b>Servicio de grupo de Platos </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface GrupoPlatoService {

	/**
	 * Crear un grupo de Platos
	 *
	 * @param grupoPlato
	 * @author eduardo.romero
	 */
	void crear(GrupoPlato grupoPlato) throws ReglaDeNegocioException, RegistroDuplicadoException;

	/**
	 * Busca y devuelve el listado de grupos de Platos por estado
	 *
	 * @return List<GrupoPlato>
	 * @author eduardo.romero
	 */
	List<GrupoPlato> buscarTodos();

	/**
	 * Busca y devuelve si existe un grupo por nombre
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
	 * Actualizar un grupo de Platos
	 *
	 * @param grupoPlato
	 * @author eduardo.romero
	 */
	void actualizar(GrupoPlato grupoPlato) throws EntidadNoEncontradaException, RegistroDuplicadoException;
}