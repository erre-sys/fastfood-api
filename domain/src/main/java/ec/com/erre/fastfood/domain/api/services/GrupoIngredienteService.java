package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Servicio de grupo de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface GrupoIngredienteService {

	/**
	 * Crear un grupo de ingredientes
	 *
	 * @param grupo
	 * @author eduardo.romero
	 */
	void crear(GrupoIngrediente grupo) throws RegistroDuplicadoException, ReglaDeNegocioException;

	void actualizar(GrupoIngrediente grupo)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException;

	void eliminarPorId(Long id) throws EntidadNoEncontradaException;

	GrupoIngrediente buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<GrupoIngrediente> buscarTodos();

	Pagina<GrupoIngrediente> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters);
}
