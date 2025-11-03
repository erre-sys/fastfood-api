package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.*;
import ec.com.erre.fastfood.domain.api.repositories.*;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static ec.com.erre.fastfood.domain.api.services.PedidoConstants.*;
import static ec.com.erre.fastfood.domain.api.services.PedidoUtils.*;

@Service
public class PedidoItemExtraServiceImpl implements PedidoItemExtraService {

	private final PedidoRepository pedidoRepo;
	private final PedidoItemRepository itemRepo;
	private final PedidoItemExtraRepository extraRepo;
	private final IngredienteRepository ingredienteRepo;
	private final InventarioRepository inventarioRepo;

	public PedidoItemExtraServiceImpl(PedidoRepository pedidoRepo, PedidoItemRepository itemRepo,
			PedidoItemExtraRepository extraRepo, IngredienteRepository ingredienteRepo,
			InventarioRepository inventarioRepo) {
		this.pedidoRepo = pedidoRepo;
		this.itemRepo = itemRepo;
		this.extraRepo = extraRepo;
		this.ingredienteRepo = ingredienteRepo;
		this.inventarioRepo = inventarioRepo;
	}

	@Override
	@Transactional
	public Long agregarExtra(Long pedidoId, Long itemId, PedidoItemExtra extra)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		Pedido pedido = pedidoRepo.buscarPorId(pedidoId);
		if (isFinalizado(pedido.getEstado()))
			throw new ReglaDeNegocioException("Pedido finalizado: no se puede modificar");

		PedidoItem item = itemRepo.buscarPorId(itemId);
		if (!pedidoId.equals(item.getPedidoId()))
			throw new ReglaDeNegocioException("El ítem no pertenece al pedido");

		if (extra.getIngredienteId() == null)
			throw new ReglaDeNegocioException("ingredienteId es obligatorio");
		if (extra.getCantidad() == null || extra.getCantidad().compareTo(new BigDecimal("0.000")) <= 0)
			throw new ReglaDeNegocioException("cantidad debe ser > 0");

		extra.setCantidad(escalarCantidad(extra.getCantidad()));

		Ingrediente ing = ingredienteRepo.buscarPorId(extra.getIngredienteId());
		if (!esActivo(ing.getEstado()))
			throw new ReglaDeNegocioException("El ingrediente '" + ing.getNombre() + "' no está disponible");
		if (!esIndicadorAfirmativo(ing.getEsExtra()))
			throw new ReglaDeNegocioException(
					"El ingrediente '" + ing.getNombre() + "' no está configurado como extra");

		// Validación de stock: exigir stock >= cantidad solicitada
		BigDecimal stock = inventarioRepo.obtenerStockActual(ing.getId());
		if (stock.compareTo(extra.getCantidad()) < 0)
			throw new ReglaDeNegocioException(
					"No hay suficiente '" + ing.getNombre() + "' en inventario (disponible: " + stock + ")");

		// precio unitario del extra = ingrediente.precioExtra (escala 2)
		BigDecimal unit = escalarPrecio(ing.getPrecioExtra());
		BigDecimal totalLinea = escalarPrecio(unit.multiply(extra.getCantidad()));

		extra.setPedidoItemId(itemId);
		extra.setPrecioExtra(totalLinea);

		Long extraId = extraRepo.agregar(extra);

		recalcularTotalesPedido(pedidoId);

		return extraId;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PedidoItemExtra> listarExtrasDeItem(Long pedidoId, Long itemId)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		PedidoItem item = itemRepo.buscarPorId(itemId);
		if (!pedidoId.equals(item.getPedidoId()))
			throw new ReglaDeNegocioException("El ítem no pertenece al pedido");

		return extraRepo.listarPorItem(itemId);
	}

	@Override
	@Transactional
	public void actualizarCantidad(Long pedidoId, Long extraId, BigDecimal nuevaCantidad)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		if (nuevaCantidad == null || nuevaCantidad.compareTo(new BigDecimal("0.000")) <= 0)
			throw new ReglaDeNegocioException("cantidad debe ser > 0");

		Pedido pedido = pedidoRepo.buscarPorId(pedidoId);
		if (isFinalizado(pedido.getEstado()))
			throw new ReglaDeNegocioException("Pedido finalizado: no se puede modificar");

		PedidoItemExtra extra = extraRepo.buscarPorId(extraId);
		PedidoItem item = itemRepo.buscarPorId(extra.getPedidoItemId());
		if (!pedidoId.equals(item.getPedidoId()))
			throw new ReglaDeNegocioException("El extra no pertenece al pedido");

		// validar stock con la cantidad NUEVA
		Ingrediente ing = ingredienteRepo.buscarPorId(extra.getIngredienteId());
		BigDecimal stock = inventarioRepo.obtenerStockActual(ing.getId());
		if (stock.compareTo(nuevaCantidad) < 0)
			throw new ReglaDeNegocioException("No hay suficiente '" + ing.getNombre()
					+ "' en inventario para actualizar (disponible: " + stock + ")");

		// precio unitario implícito = totalViejo / cantidadVieja
		if (extra.getCantidad().compareTo(BigDecimal.ZERO) <= 0)
			throw new ReglaDeNegocioException("Cantidad previa inválida");
		BigDecimal unit = extra.getPrecioExtra().divide(extra.getCantidad(), ESCALA_DIVISION,
				java.math.RoundingMode.HALF_UP);
		BigDecimal nuevoTotal = escalarPrecio(unit.multiply(escalarCantidad(nuevaCantidad)));

		extraRepo.actualizarCantidadYTotal(extraId, escalarCantidad(nuevaCantidad), nuevoTotal);

		recalcularTotalesPedido(pedidoId);
	}

	@Override
	@Transactional
	public void eliminarExtra(Long pedidoId, Long extraId)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		Pedido pedido = pedidoRepo.buscarPorId(pedidoId);
		if (isFinalizado(pedido.getEstado()))
			throw new ReglaDeNegocioException("Pedido finalizado: no se puede modificar");

		PedidoItemExtra extra = extraRepo.buscarPorId(extraId);
		PedidoItem item = itemRepo.buscarPorId(extra.getPedidoItemId());
		if (!pedidoId.equals(item.getPedidoId()))
			throw new ReglaDeNegocioException("El extra no pertenece al pedido");

		extraRepo.eliminar(extraId);

		recalcularTotalesPedido(pedidoId);
	}

	/* ===== helpers ===== */
	private boolean isFinalizado(String estado) {
		return esPedidoFinalizado(estado);
	}

	private void recalcularTotalesPedido(Long pedidoId) throws EntidadNoEncontradaException {
		List<PedidoItem> items = itemRepo.listarPorPedido(pedidoId);

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

		BigDecimal totalExtras = extraRepo.totalExtrasDePedido(pedidoId);
		BigDecimal totalNeto = escalarPrecio(totalBrutoSinDescuento.subtract(totalDescuentos).add(totalExtras));

		pedidoRepo.actualizarTotales(pedidoId, escalarPrecio(totalBrutoSinDescuento), escalarPrecio(totalDescuentos),
				escalarPrecio(totalExtras), escalarPrecio(totalNeto));
	}
}
