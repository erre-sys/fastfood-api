package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Repositorio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface IngredienteRepository {
	boolean existePorCodigo(String codigo);

	void crear(Ingrediente i);

	void actualizar(Ingrediente i);

	void eliminar(Ingrediente i);

	Ingrediente buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<Ingrediente> activos();

	Pagina<Ingrediente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
