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
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service
public class PedidosProcesoServiceImpl implements PedidosProcesoService {

	private final PedidoRepository pedidoRepo;
	private final PedidoItemRepository itemRepo;
	private final PedidoItemExtraRepository extraRepo;
	private final RecetaRepository recetaRepo; // para validar que cada plato tenga receta

	@PersistenceContext
	private EntityManager em;

	public PedidosProcesoServiceImpl(PedidoRepository pedidoRepo, PedidoItemRepository itemRepo,
			PedidoItemExtraRepository extraRepo, RecetaRepository recetaRepo) {
		this.pedidoRepo = pedidoRepo;
		this.itemRepo = itemRepo;
		this.extraRepo = extraRepo;
		this.recetaRepo = recetaRepo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void entregar(Long pedidoId, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException, ServiceException {

		Pedido pedido = pedidoRepo.buscarPorId(pedidoId);

		if (!"L".equalsIgnoreCase(pedido.getEstado())) {
			throw new ReglaDeNegocioException("El pedido debe estar en estado LISTO para poder entregarse");
		}

		List<PedidoItem> items = itemRepo.listarPorPedido(pedidoId);
		if (items == null || items.isEmpty()) {
			throw new ReglaDeNegocioException("El pedido no tiene ítems");
		}
		for (PedidoItem it : items) {
			if (it.getCantidad() == null || it.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
				throw new ReglaDeNegocioException("Todos los ítems deben tener cantidad > 0");
			}
			if (it.getSubtotal() != null && it.getSubtotal().compareTo(BigDecimal.ZERO) < 0) {
				throw new ReglaDeNegocioException("Hay ítems con subtotal negativo");
			}
		}

		Set<Long> platos = items.stream().map(PedidoItem::getPlatoId).collect(java.util.stream.Collectors.toSet());
		for (Long platoId : platos) {
			var receta = recetaRepo.obtenerPorPlato(platoId);
			if (receta == null || receta.isEmpty()) {
				throw new ReglaDeNegocioException("El plato " + platoId + " no tiene receta cargada");
			}
		}

		BigDecimal totalBruto = items.stream().map(i -> nvl2(i.getSubtotal())).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalExtras = BigDecimal.ZERO;
		for (PedidoItem it : items) {
			List<PedidoItemExtra> extras = extraRepo.listarPorItem(it.getId());
			for (PedidoItemExtra e : extras) {
				if (e.getCantidad() == null || e.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
					throw new ReglaDeNegocioException("Hay extras con cantidad inválida en el ítem " + it.getId());
				}
				if (e.getPrecioExtra() == null || e.getPrecioExtra().compareTo(BigDecimal.ZERO) < 0) {
					throw new ReglaDeNegocioException("Hay extras con precio inválido en el ítem " + it.getId());
				}
				totalExtras = totalExtras.add(scale2(nvl2(e.getCantidad()).multiply(nvl2(e.getPrecioExtra()))));
			}
		}
		totalExtras = scale2(totalExtras);

		BigDecimal totalNeto = scale2(totalBruto.add(totalExtras));

		if (!eq2(totalNeto, scale2(nvl2(pedido.getTotalNeto())))) {
			pedidoRepo.actualizarTotales(pedidoId, scale2(totalBruto), scale2(totalExtras), scale2(totalNeto));
		}

		try {
			Query q = em.createNativeQuery("CALL fastfood.sp_pedido_cambiar_estado(:p_id, :p_estado, :p_sub)");
			q.setParameter("p_id", pedidoId);
			q.setParameter("p_estado", "E");
			q.setParameter("p_sub", usuarioSub);
			q.executeUpdate();
		} catch (RuntimeException ex) {
			String msg = deepestMessage(ex);
			if (msg != null) {
				String m = msg.toLowerCase();
				if (m.contains("stock insuficiente"))
					throw new ReglaDeNegocioException("No hay suficiente stock para completar el pedido");
				if (m.contains("pedido ya finalizado"))
					throw new ReglaDeNegocioException("El pedido ya fue finalizado anteriormente");
				if (m.contains("pedido no existe"))
					throw new EntidadNoEncontradaException("Pedido no existe");
			}
			// Re-lanzar la excepción original para que GlobalExceptionHandler la procese
			throw ex;
		}
	}

	private BigDecimal nvl2(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

	private BigDecimal scale2(BigDecimal v) {
		return v.setScale(2, RoundingMode.HALF_UP);
	}

	private boolean eq2(BigDecimal a, BigDecimal b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return a.setScale(2, RoundingMode.HALF_UP).compareTo(b.setScale(2, RoundingMode.HALF_UP)) == 0;
	}

	private String deepestMessage(Throwable t) {
		String last = null;
		Throwable cur = t;
		while (cur != null) {
			if (cur.getMessage() != null)
				last = cur.getMessage();
			cur = cur.getCause();
		}
		return last;
	}
}
