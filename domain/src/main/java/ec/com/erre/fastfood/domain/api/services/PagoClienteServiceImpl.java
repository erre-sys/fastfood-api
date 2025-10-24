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

@Service
public class PagoClienteServiceImpl implements PagoClienteService {

	private static final java.util.Set<String> METODOS = new java.util.HashSet<>(
			java.util.Arrays.asList("EFECTIVO", "TARJETA", "TRANSFERENCIA", "DEPOSITO"));

	private static final java.util.Set<String> ESTADOS = new java.util.HashSet<>(
			java.util.Arrays.asList("S", "P", "F")); // SOLICITADO, PAGADO, FIADO

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
		pago.setEstado("S"); // Estado inicial: SOLICITADO

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

		// Validar transiciones permitidas (aplicando principio de responsabilidad única)
		validarTransicionEstado(pago.getEstado(), nuevoEstado);

		// Actualizar estado
		boolean actualizado = repo.actualizarEstado(pagoId, nuevoEstado);
		if (!actualizado) {
			throw new ReglaDeNegocioException("No se pudo actualizar el estado del pago");
		}
	}

	/* ==== helpers ==== */

	/**
	 * Valida que la transición de estados sea permitida (Single Responsibility Principle)
	 */
	private void validarTransicionEstado(String estadoActual, String estadoNuevo) throws ReglaDeNegocioException {

		// Permitir cambiar de cualquier estado a cualquier otro
		// Si necesitas reglas más estrictas, agrégalas aquí
		// Ejemplo de reglas estrictas:
		// - No permitir cambiar de P a S
		// - No permitir cambiar de F a S

		if (estadoActual != null && estadoActual.equals(estadoNuevo)) {
			throw new ReglaDeNegocioException("El pago ya está en estado " + getNombreEstado(estadoNuevo));
		}

		// Reglas de negocio opcionales (comentadas por ahora)
		// if ("P".equals(estadoActual)) {
		// throw new ReglaDeNegocioException("No se puede cambiar el estado de un pago
		// ya PAGADO");
		// }
	}

	/**
	 * Obtiene el nombre legible del estado (Open/Closed Principle)
	 */
	private String getNombreEstado(String estado) {
		switch (estado) {
		case "S":
			return "SOLICITADO";
		case "P":
			return "PAGADO";
		case "F":
			return "FIADO";
		default:
			return estado;
		}
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
