package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Repositorio de promociones </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PromoProgramadaRepository {
	Long crear(PromoProgramada p);

	void actualizar(PromoProgramada p);

	void eliminar(Long id);

	PromoProgramada buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<PromoProgramada> listarPorPlato(Long platoId);

	Pagina<PromoProgramada> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
