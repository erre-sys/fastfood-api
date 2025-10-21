package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.PagoProveedor;
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
public interface PagoProveedorRepository {
	Long crear(PagoProveedor pago);

	PagoProveedor buscarPorId(Long id) throws EntidadNoEncontradaException;

	Pagina<PagoProveedor> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
