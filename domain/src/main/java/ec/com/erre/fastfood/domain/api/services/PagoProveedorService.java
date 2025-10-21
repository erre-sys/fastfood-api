package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.PagoProveedor;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Servicio de pago proveedor </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PagoProveedorService {
	Long crear(PagoProveedor pago, String usuarioSub) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	PagoProveedor buscarPorId(Long id) throws EntidadNoEncontradaException;

	Pagina<PagoProveedor> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
