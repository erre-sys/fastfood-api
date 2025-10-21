package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Compra;
import ec.com.erre.fastfood.domain.api.models.api.CompraItem;
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
public interface CompraRepository {
	Long crearCompraConItems(Compra cabecera, List<CompraItem> items);

	Compra buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<CompraItem> listarItems(Long compraId);

	Pagina<Compra> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
