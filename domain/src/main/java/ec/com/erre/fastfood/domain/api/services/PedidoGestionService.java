package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.models.api.PedidoItem;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Servicio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PedidoGestionService {
	Long crear(Pedido pedido, String usuarioSub) throws ReglaDeNegocioException, EntidadNoEncontradaException;

	Pedido obtenerDetalle(Long pedidoId) throws EntidadNoEncontradaException;

	Long agregarItem(Long pedidoId, PedidoItem item) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	void cambiarEstado(Long pedidoId, String nuevoEstado) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	void cancelar(Long pedidoId) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	Pagina<Pedido> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
