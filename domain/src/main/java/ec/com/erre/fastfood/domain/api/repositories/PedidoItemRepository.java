package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.models.api.PedidoItem;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;

import java.util.List;

/**
 * <b>Repositorio de grupo de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PedidoItemRepository {
	Long agregar(PedidoItem item);

	List<PedidoItem> listarPorPedido(Long pedidoId);

	PedidoItem buscarPorId(Long id) throws EntidadNoEncontradaException;

}
