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
import java.util.Map;

import static ec.com.erre.fastfood.domain.api.services.PedidoConstants.*;
import static ec.com.erre.fastfood.domain.api.services.PedidoUtils.*;

/**
 * Implementación del servicio de gestión de pedidos. Maneja la creación, modificación y consulta de pedidos y sus
 * items.
 */
@Service
public class PedidoGestionServiceImpl implements PedidoGestionService {

	private final PedidoRepository pedidoRepository;
	private final PedidoItemRepository pedidoItemRepository;
	private final PlatoRepository platoRepository;
	private final PedidoItemExtraService pedidoItemExtraService;
	private final PedidoItemExtraRepository pedidoItemExtraRepository;

	public PedidoGestionServiceImpl(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository,
			PlatoRepository platoRepository, PedidoItemExtraService pedidoItemExtraService,
			PedidoItemExtraRepository pedidoItemExtraRepository) {
		this.pedidoRepository = pedidoRepository;
		this.pedidoItemRepository = pedidoItemRepository;
		this.platoRepository = platoRepository;
		this.pedidoItemExtraService = pedidoItemExtraService;
		this.pedidoItemExtraRepository = pedidoItemExtraRepository;
	}

	/**
	 * Crea un nuevo pedido en estado CREADO.
	 *
	 * @param pedido el pedido a crear (puede incluir items)
	 * @return el ID del pedido creado
	 */
	@Override
	@Transactional
	public Long crear(Pedido pedido) throws ReglaDeNegocioException, EntidadNoEncontradaException {
		inicializarPedido(pedido, pedido.getCreadoPorSub());

		Long pedidoId = pedidoRepository.crear(pedido);

		if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
			procesarItemsIniciales(pedidoId, pedido.getItems());
			actualizarTotales(pedidoId);
		}

		return pedidoId;
	}

	/**
	 * Obtiene el detalle completo de un pedido incluyendo items y extras.
	 *
	 * @param pedidoId el ID del pedido
	 * @return el pedido con todos sus detalles
	 */
	@Override
	@Transactional(readOnly = true)
	public Pedido obtenerDetalle(Long pedidoId) throws EntidadNoEncontradaException {
		Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
		List<PedidoItem> items = pedidoItemRepository.listarPorPedido(pedidoId);

		cargarExtrasDeItems(items);

		pedido.setItems(items);
		return pedido;
	}

	/**
	 * Agrega un nuevo item a un pedido existente.
	 *
	 * @param pedidoId el ID del pedido
	 * @param item el item a agregar
	 * @return el ID del item agregado
	 */
	@Override
	@Transactional
	public Long agregarItem(Long pedidoId, PedidoItem item)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		validarPedidoNoFinalizado(pedidoId);

		Long itemId = agregarItemInterno(pedidoId, item);
		actualizarTotales(pedidoId);

		return itemId;
	}

	/**
	 * Cambia el estado de un pedido (solo permite CREADO → LISTO).
	 *
	 * @param pedidoId el ID del pedido
	 * @param nuevoEstado el nuevo estado
	 */
	@Override
	@Transactional
	public void cambiarEstado(Long pedidoId, String nuevoEstado)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

		String estadoActual = pedido.getEstado();
		String estadoNormalizado = normalizarTexto(nuevoEstado);

		validarTransicionEstado(estadoActual, estadoNormalizado);

		boolean cambioExitoso = pedidoRepository.cambiarEstadoSimple(pedidoId, estadoNormalizado);
		if (!cambioExitoso) {
			throw new ReglaDeNegocioException(MSG_PEDIDO_NO_SE_PUDO_ACTUALIZAR);
		}
	}

	/**
	 * Anula un pedido si no está finalizado.
	 *
	 * @param pedidoId el ID del pedido
	 */
	@Override
	@Transactional
	public void cancelar(Long pedidoId) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

		if (esPedidoFinalizado(pedido.getEstado())) {
			throw new ReglaDeNegocioException(MSG_PEDIDO_YA_FINALIZADO);
		}

		boolean anulacionExitosa = pedidoRepository.anularSiProcede(pedidoId);
		if (!anulacionExitosa) {
			throw new ReglaDeNegocioException(MSG_PEDIDO_NO_SE_PUDO_ANULAR);
		}
	}

	/**
	 * Obtiene un listado paginado de pedidos con filtros.
	 *
	 * @param pager información de paginación y ordenamiento
	 * @param filters criterios de búsqueda
	 * @return página de pedidos
	 */
	@Override
	@Transactional(readOnly = true)
	public Pagina<Pedido> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return pedidoRepository.paginadoPorFiltros(pager, filters);
	}

	/* ===== Métodos Privados - Inicialización ===== */

	/**
	 * Inicializa un pedido nuevo con valores por defecto.
	 */
	private void inicializarPedido(Pedido pedido, String usuarioSub) {
		pedido.setEstado(ESTADO_CREADO);
		pedido.setTotalBruto(BigDecimal.ZERO);
		pedido.setTotalDescuentos(BigDecimal.ZERO);
		pedido.setTotalExtras(BigDecimal.ZERO);
		pedido.setTotalNeto(BigDecimal.ZERO);
		pedido.setActualizadoEn(LocalDateTime.now());
		pedido.setCreadoEn(LocalDateTime.now());
		pedido.setCreadoPorSub(usuarioSub);
	}

	/**
	 * Procesa los items iniciales de un pedido al crearlo.
	 */
	private void procesarItemsIniciales(Long pedidoId, List<PedidoItem> items)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {
		for (PedidoItem item : items) {
			agregarItemInterno(pedidoId, item);
		}
	}

	/**
	 * Carga los extras de cada item en la lista.
	 */
	private void cargarExtrasDeItems(List<PedidoItem> items) {
		for (PedidoItem item : items) {
			item.setExtras(pedidoItemExtraRepository.listarPorItem(item.getId()));
		}
	}

	/* ===== Métodos Privados - Validaciones ===== */

	/**
	 * Valida que el pedido no esté finalizado.
	 */
	private void validarPedidoNoFinalizado(Long pedidoId) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
		if (esPedidoFinalizado(pedido.getEstado())) {
			throw new ReglaDeNegocioException(MSG_PEDIDO_FINALIZADO);
		}
	}

	/**
	 * Valida que un item tenga los campos obligatorios.
	 */
	private void validarCamposObligatoriosItem(PedidoItem item) throws ReglaDeNegocioException {
		if (item.getPlatoId() == null) {
			throw new ReglaDeNegocioException(MSG_PLATO_ID_OBLIGATORIO);
		}
		if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaDeNegocioException(MSG_CANTIDAD_MAYOR_CERO);
		}
	}

	/**
	 * Valida que un plato exista y esté activo.
	 */
	private Plato validarYObtenerPlato(Long platoId) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Plato plato = platoRepository.buscarPorId(platoId);
		if (!esActivo(plato.getEstado())) {
			throw new ReglaDeNegocioException(MSG_PLATO_INACTIVO);
		}
		return plato;
	}

	/**
	 * Valida la transición de estado del pedido.
	 */
	private void validarTransicionEstado(String estadoActual, String estadoNuevo) throws ReglaDeNegocioException {
		if (esPedidoFinalizado(estadoActual)) {
			throw new ReglaDeNegocioException(MSG_PEDIDO_FINALIZADO);
		}

		boolean transicionValida = (ESTADO_CREADO.equalsIgnoreCase(estadoActual) && ESTADO_LISTO.equals(estadoNuevo))
				|| estadoActual.equalsIgnoreCase(estadoNuevo);

		if (!transicionValida) {
			throw new ReglaDeNegocioException(String.format(MSG_TRANSICION_INVALIDA, estadoActual, estadoNuevo));
		}
	}

	/* ===== Métodos Privados - Lógica de Negocio ===== */

	/**
	 * Agrega un item al pedido sin validar el estado del pedido. Calcula precios, descuentos y procesa extras.
	 */
	private Long agregarItemInterno(Long pedidoId, PedidoItem item)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {

		validarCamposObligatoriosItem(item);
		Plato plato = validarYObtenerPlato(item.getPlatoId());

		calcularPreciosYDescuentos(item, plato);

		item.setPedidoId(pedidoId);
		Long itemId = pedidoItemRepository.agregar(item);

		procesarExtrasDelItem(pedidoId, itemId, item);

		return itemId;
	}

	/**
	 * Calcula los precios y descuentos de un item basándose en el plato.
	 */
	private void calcularPreciosYDescuentos(PedidoItem item, Plato plato) {
		BigDecimal precioBase = defaultSiNulo(plato.getPrecioBase());
		BigDecimal descuentoPorcentaje = obtenerDescuentoPorcentajeValido(plato);

		BigDecimal factorDescuento = calcularFactorDescuento(descuentoPorcentaje);
		BigDecimal precioUnitario = escalarPrecio(precioBase.multiply(factorDescuento));
		BigDecimal descuentoMonto = escalarPrecio(precioBase.subtract(precioUnitario));
		BigDecimal subtotal = escalarPrecio(precioUnitario.multiply(item.getCantidad()));

		item.setPrecioUnitario(precioUnitario);
		item.setDescuentoPct(descuentoPorcentaje);
		item.setDescuentoMonto(descuentoMonto);
		item.setSubtotal(subtotal);
	}

	/**
	 * Obtiene el porcentaje de descuento válido del plato.
	 */
	private BigDecimal obtenerDescuentoPorcentajeValido(Plato plato) {
		if (!esIndicadorAfirmativo(plato.getEnPromocion()) || plato.getDescuentoPct() == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal descuento = defaultSiNulo(plato.getDescuentoPct());

		if (descuento.compareTo(BigDecimal.ZERO) < 0 || descuento.compareTo(DESCUENTO_PORCENTAJE_MAX) > 0) {
			return BigDecimal.ZERO;
		}

		return descuento;
	}

	/**
	 * Calcula el factor de descuento (1 - desc/100).
	 */
	private BigDecimal calcularFactorDescuento(BigDecimal descuentoPorcentaje) {
		BigDecimal cien = new BigDecimal("100");
		BigDecimal descuentoFraccion = descuentoPorcentaje.divide(cien, ESCALA_DIVISION, RoundingMode.HALF_UP);
		return BigDecimal.ONE.subtract(descuentoFraccion);
	}

	/**
	 * Procesa los extras del item si los tiene.
	 */
	private void procesarExtrasDelItem(Long pedidoId, Long itemId, PedidoItem item)
			throws ReglaDeNegocioException, EntidadNoEncontradaException {
		if (item.getExtras() != null && !item.getExtras().isEmpty()) {
			for (var extra : item.getExtras()) {
				pedidoItemExtraService.agregarExtra(pedidoId, itemId, extra);
			}
		}
	}

	/**
	 * Actualiza los totales del pedido recalculando desde los items.
	 */
	private void actualizarTotales(Long pedidoId) {
		List<PedidoItem> items = pedidoItemRepository.listarPorPedido(pedidoId);

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

		BigDecimal totalExtras = pedidoItemExtraRepository.totalExtrasDePedido(pedidoId);
		BigDecimal totalNeto = escalarPrecio(totalBrutoSinDescuento.subtract(totalDescuentos).add(totalExtras));

		pedidoRepository.actualizarTotales(pedidoId, escalarPrecio(totalBrutoSinDescuento),
				escalarPrecio(totalDescuentos), escalarPrecio(totalExtras), escalarPrecio(totalNeto));
	}
}
