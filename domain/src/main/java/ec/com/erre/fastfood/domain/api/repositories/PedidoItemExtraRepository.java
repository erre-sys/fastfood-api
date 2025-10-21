package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.PedidoItemExtra;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;

import java.math.BigDecimal;
import java.util.List;

/**
 * <b>Repositorio de grupo de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PedidoItemExtraRepository {
	Long agregar(PedidoItemExtra e);

	PedidoItemExtra buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<PedidoItemExtra> listarPorItem(Long pedidoItemId);

	void eliminar(Long extraId);

	void actualizarCantidadYTotal(Long extraId, BigDecimal nuevaCantidad, BigDecimal nuevoTotal);

	BigDecimal totalExtrasDePedido(Long pedidoId); // suma de TOTAL de líneas
}
