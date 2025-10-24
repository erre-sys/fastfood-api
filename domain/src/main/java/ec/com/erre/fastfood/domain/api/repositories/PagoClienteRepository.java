package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.PagoCliente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.math.BigDecimal;
import java.util.List;

/**
 * <b>Repositorio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PagoClienteRepository {
	Long crear(PagoCliente p);

	PagoCliente buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<PagoCliente> listarPorPedido(Long pedidoId);

	Pagina<PagoCliente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);

	BigDecimal totalPagadoPorPedido(Long pedidoId);

	boolean actualizarEstado(Long pagoId, String nuevoEstado);
}
