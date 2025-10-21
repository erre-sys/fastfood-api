package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.PagoProveedor;
import ec.com.erre.fastfood.domain.api.models.api.Proveedor;
import ec.com.erre.fastfood.domain.api.repositories.PagoProveedorRepository;
import ec.com.erre.fastfood.domain.api.repositories.ProveedorRepository;
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
import java.util.Set;

@Service
public class PagoProveedorServiceImpl implements PagoProveedorService {

	private static final Set<String> METODOS_PERMITIDOS = new java.util.HashSet<>(
			java.util.Arrays.asList("EFECTIVO", "TRANSFERENCIA", "TARJETA", "CHEQUE", "OTRO"));

	private final PagoProveedorRepository repo;
	private final ProveedorRepository proveedorRepo;

	public PagoProveedorServiceImpl(PagoProveedorRepository repo, ProveedorRepository proveedorRepo) {
		this.repo = repo;
		this.proveedorRepo = proveedorRepo;
	}

	@Override
	@Transactional
	public Long crear(PagoProveedor pago, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		// proveedor
		if (pago.getProveedorId() == null)
			throw new ReglaDeNegocioException("El proveedor es obligatorio");
		Proveedor prov = proveedorRepo.buscarPorId(pago.getProveedorId());
		if (!"A".equalsIgnoreCase(prov.getEstado()))
			throw new ReglaDeNegocioException("El proveedor no está activo");

		// monto
		if (pago.getMontoTotal() == null || pago.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaDeNegocioException("El montoTotal debe ser > 0");
		}
		pago.setMontoTotal(scale2(pago.getMontoTotal()));

		// método
		if (pago.getMetodo() == null || pago.getMetodo().isBlank()) {
			throw new ReglaDeNegocioException("El método de pago es obligatorio");
		}
		String metodo = pago.getMetodo().trim().toUpperCase();
		if (!METODOS_PERMITIDOS.contains(metodo)) {
			throw new ReglaDeNegocioException("Método inválido. Permitidos: " + METODOS_PERMITIDOS);
		}
		pago.setMetodo(metodo);

		// normalizar textos
		if (pago.getReferencia() != null)
			pago.setReferencia(pago.getReferencia().trim());
		if (pago.getObservaciones() != null)
			pago.setObservaciones(pago.getObservaciones().trim());
		pago.setCreadoPorSub(usuarioSub);
		pago.setFecha(LocalDateTime.now());

		return repo.crear(pago);
	}

	@Override
	@Transactional(readOnly = true)
	public PagoProveedor buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repo.buscarPorId(id);
	}

	@Override
	public Pagina<PagoProveedor> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return repo.paginadoPorFiltros(pager, filters);
	}

	/* helpers */
	private BigDecimal scale2(BigDecimal v) {
		return v.setScale(2, RoundingMode.HALF_UP);
	}
}
