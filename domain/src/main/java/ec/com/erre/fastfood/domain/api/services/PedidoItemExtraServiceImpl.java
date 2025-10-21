package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.*;
import ec.com.erre.fastfood.domain.api.repositories.*;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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

		extra.setCantidad(scale3(extra.getCantidad()));

		Ingrediente ing = ingredienteRepo.buscarPorId(extra.getIngredienteId());
		if (!"A".equalsIgnoreCase(ing.getEstado()))
			throw new ReglaDeNegocioException("Ingrediente inactivo");
		if (!"S".equalsIgnoreCase(ing.getEsExtra()))
			throw new ReglaDeNegocioException("Ingrediente no es extra");

		// Validación de stock: exigir stock >= cantidad solicitada
		BigDecimal stock = inventarioRepo.obtenerStockActual(ing.getId());
		if (stock.compareTo(extra.getCantidad()) < 0)
			throw new ReglaDeNegocioException("Stock insuficiente para agregar extra");

		// precio unitario del extra = ingrediente.precioExtra (escala 2)
		BigDecimal unit = scale2(ing.getPrecioExtra());
		BigDecimal totalLinea = scale2(unit.multiply(extra.getCantidad()));

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
			throw new ReglaDeNegocioException("Stock insuficiente para actualizar extra");

		// precio unitario implícito = totalViejo / cantidadVieja
		if (extra.getCantidad().compareTo(BigDecimal.ZERO) <= 0)
			throw new ReglaDeNegocioException("Cantidad previa inválida");
		BigDecimal unit = extra.getPrecioExtra().divide(extra.getCantidad(), 4, RoundingMode.HALF_UP);
		BigDecimal nuevoTotal = scale2(unit.multiply(scale3(nuevaCantidad)));

		extraRepo.actualizarCantidadYTotal(extraId, scale3(nuevaCantidad), nuevoTotal);

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
	private boolean isFinalizado(String e) {
		return "E".equalsIgnoreCase(e) || "X".equalsIgnoreCase(e);
	}

	private void recalcularTotalesPedido(Long pedidoId) throws EntidadNoEncontradaException {
		Pedido p = pedidoRepo.buscarPorId(pedidoId);
		BigDecimal totalExtras = extraRepo.totalExtrasDePedido(pedidoId);
		BigDecimal totalBruto = p.getTotalBruto() == null ? BigDecimal.ZERO : p.getTotalBruto();
		BigDecimal totalNeto = scale2(totalBruto.add(totalExtras));
		pedidoRepo.actualizarTotales(pedidoId, scale2(totalBruto), scale2(totalExtras), scale2(totalNeto));
	}

	private BigDecimal scale2(BigDecimal v) {
		return v.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal scale3(BigDecimal v) {
		return v.setScale(3, RoundingMode.HALF_UP);
	}
}
