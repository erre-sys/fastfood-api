package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Repositorio de platos </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PlatoRepository {

	boolean existePorCodigo(String codigo);

	void crear(Plato p);

	void actualizar(Plato p);

	void eliminar(Plato p);

	Plato buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<Plato> activos();

	Pagina<Plato> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);

}