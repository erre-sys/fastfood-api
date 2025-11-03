package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.PagoCliente;
import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.repositories.PagoClienteRepository;
import ec.com.erre.fastfood.domain.api.repositories.PedidoRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de pagos de clientes. Flujo de estados: - SOLICITADO (S): Estado inicial al registrar un pago -
 * PAGADO (P): Pago completado - FIADO (F): Pedido queda a crédito Transiciones permitidas: - SOLICITADO → PAGADO -
 * SOLICITADO → FIADO - FIADO → PAGADO - PAGADO → (estado final, no permite cambios)
 */
@Service
public class PagoClienteServiceImpl implements PagoClienteService {

	// Estados de pago
	private static final String ESTADO_SOLICITADO = "S";
	private static final String ESTADO_PAGADO = "P";
	private static final String ESTADO_FIADO = "F";

	private static final java.util.Set<String> METODOS = new java.util.HashSet<>(
			java.util.Arrays.asList("EFECTIVO", "TARJETA", "TRANSFERENCIA", "DEPOSITO"));

	private static final java.util.Set<String> ESTADOS = new java.util.HashSet<>(
			java.util.Arrays.asList(ESTADO_SOLICITADO, ESTADO_PAGADO, ESTADO_FIADO));

	private final PagoClienteRepository repo;
	private final PedidoRepository pedidoRepo;

	public PagoClienteServiceImpl(PagoClienteRepository repo, PedidoRepository pedidoRepo) {
		this.repo = repo;
		this.pedidoRepo = pedidoRepo;
	}

	@Override
	public Long registrarPago(PagoCliente pago) throws EntidadNoEncontradaException, ReglaDeNegocioException {

		normalizar(pago);
		validar(pago);

		Pedido ped = pedidoRepo.buscarPorId(pago.getPedidoId());
		if (!"E".equalsIgnoreCase(ped.getEstado())) {
			throw new ReglaDeNegocioException("El pedido debe estar ENTREGADO antes de registrar el pago");
		}

		BigDecimal yaPagado = repo.totalPagadoPorPedido(pago.getPedidoId());
		BigDecimal totalPedido = ped.getTotalNeto() == null ? BigDecimal.ZERO : ped.getTotalNeto();
		if (yaPagado.add(pago.getMontoTotal()).compareTo(totalPedido) > 0) {
			throw new ReglaDeNegocioException("El pago excede el total del pedido");
		}

		pago.setFecha(pago.getFecha() == null ? LocalDateTime.now() : pago.getFecha());
		pago.setEstado(ESTADO_SOLICITADO);

		return repo.crear(pago);
	}

	@Override
	public PagoCliente buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repo.buscarPorId(id);
	}

	@Override
	public List<PagoCliente> listarPorPedido(Long pedidoId) {
		return repo.listarPorPedido(pedidoId);
	}

	@Override
	public Pagina<PagoCliente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return repo.paginado(pager, filters);
	}

	@Override
	public void cambiarEstado(Long pagoId, String nuevoEstado)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		// Normalizar y validar nuevo estado
		if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
			throw new ReglaDeNegocioException("El nuevo estado es obligatorio");
		}
		nuevoEstado = nuevoEstado.trim().toUpperCase();

		if (!ESTADOS.contains(nuevoEstado)) {
			throw new ReglaDeNegocioException("Estado inválido. Permitidos: S (SOLICITADO), P (PAGADO), F (FIADO)");
		}

		// Buscar el pago
		PagoCliente pago = repo.buscarPorId(pagoId);

		// Validar transiciones permitidas
		validarTransicionEstado(pago.getEstado(), nuevoEstado);

		// Si cambia a PAGADO, actualizar la fecha al momento del pago
		LocalDateTime fechaActualizacion = nuevoEstado.equals(ESTADO_PAGADO) ? LocalDateTime.now() : null;

		// Actualizar estado (y fecha si corresponde)
		boolean actualizado = repo.actualizarEstado(pagoId, nuevoEstado, fechaActualizacion);
		if (!actualizado) {
			throw new ReglaDeNegocioException("No se pudo actualizar el estado del pago");
		}
	}

	/**
	 * Valida que la transición de estados sea permitida según las reglas de negocio: - SOLICITADO → PAGADO ✓ -
	 * SOLICITADO → FIADO ✓ - FIADO → PAGADO ✓ - PAGADO → (ninguno, estado final) ✗
	 */
	private void validarTransicionEstado(String estadoActual, String estadoNuevo) throws ReglaDeNegocioException {
		// Verificar que no sea el mismo estado
		if (estadoActual != null && estadoActual.equals(estadoNuevo)) {
			throw new ReglaDeNegocioException("El pago ya está en estado " + getNombreEstado(estadoNuevo));
		}

		if (estadoActual.equals(ESTADO_SOLICITADO)) {
			if (!estadoNuevo.equals(ESTADO_PAGADO) && !estadoNuevo.equals(ESTADO_FIADO)) {
				throw new ReglaDeNegocioException("Desde SOLICITADO solo se puede cambiar a PAGADO o FIADO");
			}
		} else if (estadoActual.equals(ESTADO_FIADO)) {
			if (!estadoNuevo.equals(ESTADO_PAGADO)) {
				throw new ReglaDeNegocioException("Desde FIADO solo se puede cambiar a PAGADO");
			}
		} else if (estadoActual.equals(ESTADO_PAGADO)) {
			throw new ReglaDeNegocioException(
					"No se puede cambiar el estado de un pago que ya está PAGADO (estado final)");
		} else {
			throw new ReglaDeNegocioException("Estado actual inválido: " + estadoActual);
		}
	}

	/**
	 * Obtiene el nombre legible del estado
	 */
	private String getNombreEstado(String estado) {
		return switch (estado) {
		case ESTADO_SOLICITADO -> "SOLICITADO";
		case ESTADO_PAGADO -> "PAGADO";
		case ESTADO_FIADO -> "FIADO";
		default -> estado;
		};
	}

	/* ==== helpers ==== */
	private void normalizar(PagoCliente p) {
		if (p.getMetodo() != null)
			p.setMetodo(p.getMetodo().trim().toUpperCase());
		if (p.getReferencia() != null)
			p.setReferencia(p.getReferencia().trim());
		if (p.getMontoTotal() != null)
			p.setMontoTotal(p.getMontoTotal().setScale(2, RoundingMode.HALF_UP));
	}

	private void validar(PagoCliente p) throws ReglaDeNegocioException {
		if (p.getPedidoId() == null)
			throw new ReglaDeNegocioException("pedidoId es obligatorio");
		if (p.getMontoTotal() == null || p.getMontoTotal().compareTo(new BigDecimal("0.00")) <= 0)
			throw new ReglaDeNegocioException("montoTotal debe ser > 0");
		if (p.getMetodo() == null || !METODOS.contains(p.getMetodo()))
			throw new ReglaDeNegocioException("metodo inválido. Permitidos: " + METODOS);
	}
}
