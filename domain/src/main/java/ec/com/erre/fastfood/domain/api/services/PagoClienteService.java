package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.PagoCliente;
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
public interface PagoClienteService {
	Long registrarPago(PagoCliente pago) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	PagoCliente buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<PagoCliente> listarPorPedido(Long pedidoId);

	Pagina<PagoCliente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);

	void cambiarEstado(Long pagoId, String nuevoEstado) throws EntidadNoEncontradaException, ReglaDeNegocioException;
}
