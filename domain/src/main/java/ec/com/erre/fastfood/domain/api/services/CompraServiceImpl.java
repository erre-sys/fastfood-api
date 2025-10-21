package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.*;
import ec.com.erre.fastfood.domain.api.repositories.CompraRepository;
import ec.com.erre.fastfood.domain.api.repositories.IngredienteRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompraServiceImpl implements CompraService {

	private final CompraRepository compraRepo;
	private final ProveedorRepository proveedorRepo;
	private final IngredienteRepository ingredienteRepo;

	public CompraServiceImpl(CompraRepository compraRepo, ProveedorRepository proveedorRepo,
			IngredienteRepository ingredienteRepo) {
		this.compraRepo = compraRepo;
		this.proveedorRepo = proveedorRepo;
		this.ingredienteRepo = ingredienteRepo;
	}

	@Override
	@Transactional
	public Long crear(Compra cabecera, List<CompraItem> items)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		if (cabecera.getProveedorId() == null) {
			throw new ReglaDeNegocioException("El proveedor es obligatorio");
		}
		Proveedor prov = proveedorRepo.buscarPorId(cabecera.getProveedorId());
		if (!"A".equalsIgnoreCase(prov.getEstado())) {
			throw new ReglaDeNegocioException("El proveedor no está activo");
		}

		if (items == null || items.isEmpty()) {
			throw new ReglaDeNegocioException("La compra debe tener al menos un ítem");
		}

		Set<Long> vistos = new HashSet<>();
		for (CompraItem it : items) {
			if (it.getIngredienteId() == null) {
				throw new ReglaDeNegocioException("Cada ítem debe tener ingredienteId");
			}
			if (!vistos.add(it.getIngredienteId())) {
				throw new ReglaDeNegocioException("Ingrediente duplicado en la compra: " + it.getIngredienteId());
			}

			Ingrediente ing = ingredienteRepo.buscarPorId(it.getIngredienteId());
			if (!"A".equalsIgnoreCase(ing.getEstado())) {
				throw new ReglaDeNegocioException("Ingrediente inactivo: " + it.getIngredienteId());
			}

			if (it.getCantidad() == null || it.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
				throw new ReglaDeNegocioException("Cantidad debe ser > 0 (ingrediente " + it.getIngredienteId() + ")");
			}
			if (it.getCostoUnitario() == null || it.getCostoUnitario().compareTo(BigDecimal.ZERO) < 0) {
				throw new ReglaDeNegocioException(
						"Costo unitario inválido (ingrediente " + it.getIngredienteId() + ")");
			}

			it.setCantidad(scale3(it.getCantidad()));
			it.setCostoUnitario(scale2(it.getCostoUnitario()));
		}

		// normalizar cabecera
		if (cabecera.getReferencia() != null)
			cabecera.setReferencia(cabecera.getReferencia().trim());
		if (cabecera.getObservaciones() != null)
			cabecera.setObservaciones(cabecera.getObservaciones().trim());
		cabecera.setCreadoPorSub("USUARIO");
		cabecera.setFecha(LocalDateTime.now());

		// persistir (trigger actualizará inventario y kardex)
		return compraRepo.crearCompraConItems(cabecera, items);
	}

	@Override
	@Transactional(readOnly = true)
	public Compra buscarPorId(Long compraId) throws EntidadNoEncontradaException {
		Compra cab = compraRepo.buscarPorId(compraId);
		List<CompraItem> items = compraRepo.listarItems(compraId);
		cab.setItems(items);
		cab.setTotal(calcularTotal(items));
		return cab;
	}

	@Override
	public Pagina<Compra> buscarPaginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pagina<Compra> page = compraRepo.paginadoPorFiltros(pager, filters);

		// Cargar items y calcular total para cada compra
		for (Compra compra : page.getContenido()) {
			List<CompraItem> items = compraRepo.listarItems(compra.getId());
			compra.setItems(items);
			compra.setTotal(calcularTotal(items));
		}

		return page;
	}

	/**
	 * Calcula el total de una compra sumando cantidad * costoUnitario de cada item
	 */
	private BigDecimal calcularTotal(List<CompraItem> items) {
		if (items == null || items.isEmpty()) {
			return BigDecimal.ZERO;
		}

		return items.stream().map(item -> {
			BigDecimal cantidad = item.getCantidad() != null ? item.getCantidad() : BigDecimal.ZERO;
			BigDecimal costo = item.getCostoUnitario() != null ? item.getCostoUnitario() : BigDecimal.ZERO;
			return cantidad.multiply(costo);
		}).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
	}

	/* helpers */
	private BigDecimal scale2(BigDecimal v) {
		return v.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal scale3(BigDecimal v) {
		return v.setScale(3, RoundingMode.HALF_UP);
	}
}