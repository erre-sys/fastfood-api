package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.PedidoItemExtra;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;

import java.math.BigDecimal;
import java.util.List;

/**
 * <b>Servicio de pedido extra </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PedidoItemExtraService {
	Long agregarExtra(Long pedidoId, Long itemId, PedidoItemExtra extra)
			throws EntidadNoEncontradaException, ReglaDeNegocioException;

	List<PedidoItemExtra> listarExtrasDeItem(Long pedidoId, Long itemId)
			throws EntidadNoEncontradaException, ReglaDeNegocioException;

	void actualizarCantidad(Long pedidoId, Long extraId, BigDecimal nuevaCantidad)
			throws EntidadNoEncontradaException, ReglaDeNegocioException;

	void eliminarExtra(Long pedidoId, Long extraId) throws EntidadNoEncontradaException, ReglaDeNegocioException;
}
