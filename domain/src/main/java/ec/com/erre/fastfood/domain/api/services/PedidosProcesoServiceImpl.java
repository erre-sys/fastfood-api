package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.models.api.PedidoItem;
import ec.com.erre.fastfood.domain.api.models.api.PedidoItemExtra;
import ec.com.erre.fastfood.domain.api.repositories.PedidoItemExtraRepository;
import ec.com.erre.fastfood.domain.api.repositories.PedidoItemRepository;
import ec.com.erre.fastfood.domain.api.repositories.PedidoRepository;
import ec.com.erre.fastfood.domain.api.repositories.RecetaRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ec.com.erre.fastfood.domain.api.services.PedidoConstants.*;
import static ec.com.erre.fastfood.domain.api.services.PedidoUtils.*;

/**
 * Implementación del servicio de procesamiento de pedidos. Maneja la entrega de pedidos y la coordinación con el stored
 * procedure.
 */
@Service
public class PedidosProcesoServiceImpl implements PedidosProcesoService {

	private final PedidoRepository pedidoRepository;
	private final PedidoItemRepository pedidoItemRepository;
	private final PedidoItemExtraRepository pedidoItemExtraRepository;
	private final RecetaRepository recetaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public PedidosProcesoServiceImpl(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository,
			PedidoItemExtraRepository pedidoItemExtraRepository, RecetaRepository recetaRepository) {
		this.pedidoRepository = pedidoRepository;
		this.pedidoItemRepository = pedidoItemRepository;
		this.pedidoItemExtraRepository = pedidoItemExtraRepository;
		this.recetaRepository = recetaRepository;
	}

	/**
	 * Entrega un pedido validando, calculando totales y actualizando inventario.
	 *
	 * @param pedidoId el ID del pedido
	 * @param usuarioSub identificador del usuario que entrega
	 * @throws EntidadNoEncontradaException si el pedido no existe
	 * @throws ReglaDeNegocioException si las validaciones fallan
	 * @throws ServiceException si ocurre un error en el stored procedure
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void entregar(Long pedidoId, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException, ServiceException {

		Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
		List<PedidoItem> items = pedidoItemRepository.listarPorPedido(pedidoId);

		validarEstadoPedidoParaEntrega(pedido);
		validarItemsDelPedido(items);
		validarRecetasDeLosPlatos(items);

		TotalesPedido totales = calcularTotalesPedido(items, pedidoId);
		actualizarTotalesSiNecesario(pedidoId, pedido, totales);

		ejecutarCambioEstadoEntregado(pedidoId, usuarioSub);
	}

	/* ===== Métodos Privados - Validaciones ===== */

	/**
	 * Valida que el pedido esté en estado LISTO.
	 */
	private void validarEstadoPedidoParaEntrega(Pedido pedido) throws ReglaDeNegocioException {
		if (!ESTADO_LISTO.equalsIgnoreCase(pedido.getEstado())) {
			throw new ReglaDeNegocioException(MSG_PEDIDO_ESTADO_LISTO_REQUERIDO);
		}
	}

	/**
	 * Valida que los items del pedido sean válidos.
	 */
	private void validarItemsDelPedido(List<PedidoItem> items) throws ReglaDeNegocioException {
		if (items == null || items.isEmpty()) {
			throw new ReglaDeNegocioException(MSG_PEDIDO_SIN_ITEMS);
		}

		for (PedidoItem item : items) {
			validarCantidadItem(item);
			validarSubtotalItem(item);
		}
	}

	/**
	 * Valida que el item tenga cantidad positiva.
	 */
	private void validarCantidadItem(PedidoItem item) throws ReglaDeNegocioException {
		if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaDeNegocioException(MSG_ITEMS_CANTIDAD_MAYOR_CERO);
		}
	}

	/**
	 * Valida que el subtotal no sea negativo.
	 */
	private void validarSubtotalItem(PedidoItem item) throws ReglaDeNegocioException {
		if (item.getSubtotal() != null && item.getSubtotal().compareTo(BigDecimal.ZERO) < 0) {
			throw new ReglaDeNegocioException(MSG_ITEMS_SUBTOTAL_NEGATIVO);
		}
	}

	/**
	 * Valida que todos los platos tengan receta cargada.
	 */
	private void validarRecetasDeLosPlatos(List<PedidoItem> items) throws ReglaDeNegocioException {
		Set<Long> platoIds = items.stream().map(PedidoItem::getPlatoId).collect(Collectors.toSet());

		for (Long platoId : platoIds) {
			var recetas = recetaRepository.obtenerPorPlato(platoId);
			if (recetas == null || recetas.isEmpty()) {
				throw new ReglaDeNegocioException(String.format(MSG_PLATO_SIN_RECETA, platoId));
			}
		}
	}

	/* ===== Métodos Privados - Cálculo de Totales ===== */

	/**
	 * Calcula todos los totales del pedido.
	 */
	private TotalesPedido calcularTotalesPedido(List<PedidoItem> items, Long pedidoId) throws ReglaDeNegocioException {
		TotalesPedido totales = new TotalesPedido();

		calcularTotalesItems(items, totales);
		calcularTotalesExtras(items, totales);
		calcularTotalNeto(totales);

		return totales;
	}

	/**
	 * Calcula totalBruto y totalDescuentos desde los items.
	 */
	private void calcularTotalesItems(List<PedidoItem> items, TotalesPedido totales) {
		BigDecimal totalBrutoSinDescuento = BigDecimal.ZERO;
		BigDecimal totalDescuentos = BigDecimal.ZERO;

		for (PedidoItem item : items) {
			BigDecimal subtotal = defaultSiNulo(item.getSubtotal());
			BigDecimal descuentoMonto = defaultSiNulo(item.getDescuentoMonto());
			BigDecimal cantidad = defaultSiNulo(item.getCantidad());

			BigDecimal descuentoTotalItem = escalarPrecio(descuentoMonto.multiply(cantidad));
			BigDecimal subtotalSinDescuento = subtotal.add(descuentoTotalItem);

			totalBrutoSinDescuento = totalBrutoSinDescuento.add(subtotalSinDescuento);
			totalDescuentos = totalDescuentos.add(descuentoTotalItem);
		}

		totales.totalBruto = escalarPrecio(totalBrutoSinDescuento);
		totales.totalDescuentos = escalarPrecio(totalDescuentos);
	}

	/**
	 * Calcula y valida el total de extras.
	 */
	private void calcularTotalesExtras(List<PedidoItem> items, TotalesPedido totales) throws ReglaDeNegocioException {
		BigDecimal totalExtras = BigDecimal.ZERO;

		for (PedidoItem item : items) {
			List<PedidoItemExtra> extras = pedidoItemExtraRepository.listarPorItem(item.getId());
			for (PedidoItemExtra extra : extras) {
				validarCantidadExtra(extra, item.getId());
				validarPrecioExtra(extra, item.getId());

				BigDecimal totalLineaExtra = defaultSiNulo(extra.getCantidad())
						.multiply(defaultSiNulo(extra.getPrecioExtra()));
				totalExtras = totalExtras.add(escalarPrecio(totalLineaExtra));
			}
		}

		totales.totalExtras = escalarPrecio(totalExtras);
	}

	/**
	 * Valida que el extra tenga cantidad positiva.
	 */
	private void validarCantidadExtra(PedidoItemExtra extra, Long itemId) throws ReglaDeNegocioException {
		if (extra.getCantidad() == null || extra.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaDeNegocioException(MSG_EXTRAS_CANTIDAD_INVALIDA + itemId);
		}
	}

	/**
	 * Valida que el extra tenga precio no negativo.
	 */
	private void validarPrecioExtra(PedidoItemExtra extra, Long itemId) throws ReglaDeNegocioException {
		if (extra.getPrecioExtra() == null || extra.getPrecioExtra().compareTo(BigDecimal.ZERO) < 0) {
			throw new ReglaDeNegocioException(MSG_EXTRAS_PRECIO_INVALIDO + itemId);
		}
	}

	/**
	 * Calcula el total neto del pedido.
	 */
	private void calcularTotalNeto(TotalesPedido totales) {
		totales.totalNeto = escalarPrecio(
				totales.totalBruto.subtract(totales.totalDescuentos).add(totales.totalExtras));
	}

	/**
	 * Actualiza los totales en la base de datos si difieren.
	 */
	private void actualizarTotalesSiNecesario(Long pedidoId, Pedido pedido, TotalesPedido totales) {
		BigDecimal totalNetoActual = escalarPrecio(defaultSiNulo(pedido.getTotalNeto()));

		if (!igualesConPrecision(totales.totalNeto, totalNetoActual)) {
			pedidoRepository.actualizarTotales(pedidoId, totales.totalBruto, totales.totalDescuentos,
					totales.totalExtras, totales.totalNeto);
		}
	}

	/* ===== Métodos Privados - Stored Procedure ===== */

	/**
	 * Ejecuta el stored procedure para cambiar estado a ENTREGADO.
	 */
	private void ejecutarCambioEstadoEntregado(Long pedidoId, String usuarioSub)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {
		try {
			Query query = entityManager.createNativeQuery(SP_PEDIDO_CAMBIAR_ESTADO);
			query.setParameter("p_id", pedidoId);
			query.setParameter("p_estado", ESTADO_ENTREGADO);
			query.setParameter("p_sub", usuarioSub);
			query.executeUpdate();
		} catch (RuntimeException excepcion) {
			manejarExcepcionStoredProcedure(excepcion);
		}
	}

	/**
	 * Maneja las excepciones lanzadas por el stored procedure.
	 */
	private void manejarExcepcionStoredProcedure(RuntimeException excepcion)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {
		String mensajeError = extraerMensajeMasProfundo(excepcion);

		if (mensajeError != null) {
			String mensajeLowerCase = mensajeError.toLowerCase();

			if (mensajeLowerCase.contains(SP_ERROR_STOCK_INSUFICIENTE)) {
				throw new ReglaDeNegocioException(MSG_PEDIDO_STOCK_INSUFICIENTE);
			}
			if (mensajeLowerCase.contains(SP_ERROR_PEDIDO_FINALIZADO)) {
				throw new ReglaDeNegocioException(MSG_PEDIDO_YA_FINALIZADO);
			}
			if (mensajeLowerCase.contains(SP_ERROR_PEDIDO_NO_EXISTE)) {
				throw new EntidadNoEncontradaException(MSG_PEDIDO_NO_EXISTE);
			}
		}

		// Re-lanzar la excepción original para que GlobalExceptionHandler la procese
		throw excepcion;
	}

	/* ===== Clase Interna - DTO de Totales ===== */

	/**
	 * Objeto de transferencia para totales del pedido.
	 */
	private static class TotalesPedido {
		BigDecimal totalBruto = BigDecimal.ZERO;
		BigDecimal totalDescuentos = BigDecimal.ZERO;
		BigDecimal totalExtras = BigDecimal.ZERO;
		BigDecimal totalNeto = BigDecimal.ZERO;
	}
}
