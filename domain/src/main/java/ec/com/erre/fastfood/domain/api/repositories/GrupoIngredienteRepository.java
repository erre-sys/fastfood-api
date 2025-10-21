package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Repositorio de grupo de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface GrupoIngredienteRepository {
	boolean existePorNombre(String nombre);

	void crear(GrupoIngrediente create);

	void actualizar(GrupoIngrediente update);

	void eliminar(GrupoIngrediente delete);

	GrupoIngrediente buscarPorId(Long id) throws EntidadNoEncontradaException;

	Pagina<GrupoIngrediente> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters);

	List<GrupoIngrediente> buscarActivos();
}
