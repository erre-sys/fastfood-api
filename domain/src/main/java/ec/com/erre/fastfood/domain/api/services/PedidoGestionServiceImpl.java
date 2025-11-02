package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.models.api.PedidoItem;
import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.repositories.PedidoItemRepository;
import ec.com.erre.fastfood.domain.api.repositories.PedidoRepository;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.api.repositories.PedidoItemExtraRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoGestionServiceImpl implements PedidoGestionService {

	private final PedidoRepository pedidoRepo;
	private final PedidoItemRepository itemRepo;
	private final PlatoRepository platoRepo;
	private final PedidoItemExtraService extraService;
	private final PedidoItemExtraRepository extraRepo;

	public PedidoGestionServiceImpl(PedidoRepository pedidoRepo, PedidoItemRepository itemRepo,
			PlatoRepository platoRepo, PedidoItemExtraService extraService, PedidoItemExtraRepository extraRepo) {
		this.pedidoRepo = pedidoRepo;
		this.itemRepo = itemRepo;
		this.platoRepo = platoRepo;
		this.extraService = extraService;
		this.extraRepo = extraRepo;
	}

	@Override
	@Transactional
	public Long crear(Pedido pedido, String usuarioSub) throws ReglaDeNegocioException, EntidadNoEncontradaException {
		pedido.setEstado("C");
		pedido.setTotalBruto(BigDecimal.ZERO);
		pedido.setTotalExtras(BigDecimal.ZERO);
		pedido.setTotalNeto(BigDecimal.ZERO);
		pedido.setActualizadoEn(LocalDateTime.now());
		pedido.setCreadoEn(LocalDateTime.now());
		pedido.setCreadoPorSub(usuarioSub);

		Long pedidoId = pedidoRepo.crear(pedido);

		// Si el pedido trae items, agregarlos
		if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
			for (PedidoItem item : pedido.getItems()) {
				agregarItemInterno(pedidoId, item);
			}
			// Recalcular totales después de agregar todos los items
			actualizarTotales(pedidoId);
		}

		return pedidoId;
	}

	@Override
	@Transactional(readOnly = true)
	public Pedido obtenerDetalle(Long pedidoId) throws EntidadNoEncontradaException {
		Pedido p = pedidoRepo.buscarPorId(pedidoId);
		List<PedidoItem> items = itemRepo.listarPorPedido(pedidoId);

		// Cargar los extras de cada item
		for (PedidoItem item : items) {
			item.setExtras(extraRepo.listarPorItem(item.getId()));
		}

		p.setItems(items);
		return p;
	}

	@Override
	@Transactional
	public Long agregarItem(Long pedidoId, PedidoItem item)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		Pedido pedido = pedidoRepo.buscarPorId(pedidoId);
		// No permitir si finalizado (ENTREGADO o ANULADO)
		if ("E".equalsIgnoreCase(pedido.getEstado()) || "A".equalsIgnoreCase(pedido.getEstado())) {
			throw new ReglaDeNegocioException("Pedido finalizado: no se puede modificar");
		}

		Long itemId = agregarItemInterno(pedidoId, item);

		// Recalcular totales del pedido
		actualizarTotales(pedidoId);

		return itemId;
	}

	/**
	 * Método interno para agregar un item sin validar el estado del pedido Usado al crear el pedido con items y al
	 * agregar items manualmente. También procesa los extras si vienen en el item.
	 */
	private Long agregarItemInterno(Long pedidoId, PedidoItem item)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {
		// Validaciones del ítem
		if (item.getPlatoId() == null)
			throw new ReglaDeNegocioException("platoId es obligatorio");
		if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaDeNegocioException("La cantidad debe ser > 0");
		}

		Plato plato = platoRepo.buscarPorId(item.getPlatoId());
		if (!"A".equalsIgnoreCase(plato.getEstado())) {
			throw new ReglaDeNegocioException("El plato está inactivo");
		}

		// Precio efectivo = precio_base * (1 - desc/100) si está en promo
		BigDecimal precioBase = nvl2(plato.getPrecioBase());
		BigDecimal desc = ("S".equalsIgnoreCase(plato.getEnPromocion()) && plato.getDescuentoPct() != null)
				? nvl2(plato.getDescuentoPct())
				: BigDecimal.ZERO;

		if (desc.compareTo(BigDecimal.ZERO) < 0 || desc.compareTo(new BigDecimal("100")) > 0) {
			desc = BigDecimal.ZERO;
		}

		BigDecimal factor = BigDecimal.ONE.subtract(desc.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
		BigDecimal precioUnitario = scale2(precioBase.multiply(factor));
		BigDecimal descuentoMonto = scale2(precioBase.subtract(precioUnitario));
		BigDecimal subtotal = scale2(precioUnitario.multiply(item.getCantidad()));

		// El sistema calcula precios y subtotales
		item.setPedidoId(pedidoId);
		item.setPrecioUnitario(precioUnitario);
		item.setDescuentoPct(desc);
		item.setDescuentoMonto(descuentoMonto);
		item.setSubtotal(subtotal);

		Long itemId = itemRepo.agregar(item);

		// Procesar extras si vienen en el item (delegando al servicio especializado)
		if (item.getExtras() != null && !item.getExtras().isEmpty()) {
			for (var extra : item.getExtras()) {
				extraService.agregarExtra(pedidoId, itemId, extra);
			}
		}

		return itemId;
	}

	@Override
	@Transactional
	public void cambiarEstado(Long pedidoId, String nuevoEstado)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Pedido pedido = pedidoRepo.buscarPorId(pedidoId);

		String estadoActual = pedido.getEstado();
		String estadoNuevo = upper1(nuevoEstado);

		// Estados finales: no se pueden cambiar
		if ("E".equalsIgnoreCase(estadoActual) || "A".equalsIgnoreCase(estadoActual)) {
			throw new ReglaDeNegocioException("Pedido finalizado: no se puede cambiar estado");
		}

		// Solo permitir C -> L (CREADO -> LISTO)
		boolean ok = ("C".equalsIgnoreCase(estadoActual) && "L".equals(estadoNuevo))
				|| estadoActual.equalsIgnoreCase(estadoNuevo); // Idempotencia

		if (!ok) {
			throw new ReglaDeNegocioException(
					"Transición inválida (" + estadoActual + " → " + estadoNuevo + "). Solo se permite CREADO → LISTO");
		}

		boolean changed = pedidoRepo.cambiarEstadoSimple(pedidoId, estadoNuevo);
		if (!changed) {
			throw new ReglaDeNegocioException("No fue posible actualizar el estado");
		}
	}

	@Override
	@Transactional
	public void cancelar(Long pedidoId) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Pedido p = pedidoRepo.buscarPorId(pedidoId);
		// No se puede anular si ya está ENTREGADO o ANULADO
		if ("E".equalsIgnoreCase(p.getEstado()) || "A".equalsIgnoreCase(p.getEstado())) {
			throw new ReglaDeNegocioException("El pedido ya está finalizado");
		}
		boolean ok = pedidoRepo.anularSiProcede(pedidoId);
		if (!ok)
			throw new ReglaDeNegocioException("No fue posible anular el pedido");
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Pedido> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return pedidoRepo.paginadoPorFiltros(pager, filters);
	}

	/* ===== helpers ===== */
	private void actualizarTotales(Long pedidoId) {
		List<PedidoItem> items = itemRepo.listarPorPedido(pedidoId);
		BigDecimal totalBruto = items.stream().map(i -> nvl2(i.getSubtotal())).reduce(BigDecimal.ZERO, BigDecimal::add);
		// Calcular el total de extras desde la base de datos
		BigDecimal totalExtras = extraRepo.totalExtrasDePedido(pedidoId);
		BigDecimal totalNeto = scale2(totalBruto.add(totalExtras));
		pedidoRepo.actualizarTotales(pedidoId, scale2(totalBruto), scale2(totalExtras), scale2(totalNeto));
	}

	private BigDecimal nvl2(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

	private BigDecimal scale2(BigDecimal v) {
		return v.setScale(2, RoundingMode.HALF_UP);
	}

	private String upper1(String s) {
		return s == null ? null : s.trim().toUpperCase();
	}
}
