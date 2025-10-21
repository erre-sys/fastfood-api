package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.api.repositories.PromoProgramadaRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PromoProgramadaServiceImpl implements PromoProgramadaService {

	private final PromoProgramadaRepository repo;
	private final PlatoRepository platoRepo;

	public PromoProgramadaServiceImpl(PromoProgramadaRepository repo, PlatoRepository platoRepo) {
		this.repo = repo;
		this.platoRepo = platoRepo;
	}

	@Override
	@Transactional
	public Long crear(PromoProgramada p, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		validarYNormalizar(p, true);
		Plato plato = platoRepo.buscarPorId(p.getPlatoId());
		if (!"A".equalsIgnoreCase(plato.getEstado())) {
			throw new ReglaDeNegocioException("El plato no está activo");
		}
		p.setCreadoPorSub(usuarioSub);
		return repo.crear(p);
	}

	@Override
	@Transactional
	public void actualizar(PromoProgramada p) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		if (p.getId() == null)
			throw new ReglaDeNegocioException("El id es obligatorio para actualizar");
		validarYNormalizar(p, false);
		// verificar existencia
		repo.buscarPorId(p.getId());
		// si cambian platoId, validar que exista y esté activo
		if (p.getPlatoId() != null) {
			Plato plato = platoRepo.buscarPorId(p.getPlatoId());
			if (!"A".equalsIgnoreCase(plato.getEstado())) {
				throw new ReglaDeNegocioException("El plato no está activo");
			}
		}
		repo.actualizar(p);
	}

	@Override
	@Transactional(readOnly = true)
	public PromoProgramada buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repo.buscarPorId(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PromoProgramada> listarPorPlato(Long platoId) throws EntidadNoEncontradaException {
		// coherencia: valida plato existente
		platoRepo.buscarPorId(platoId);
		return repo.listarPorPlato(platoId);
	}

	@Override
	public Pagina<PromoProgramada> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return repo.paginadoPorFiltros(pager, filters);
	}

	/* ===== helpers ===== */
	private void validarYNormalizar(PromoProgramada p, boolean crear) throws ReglaDeNegocioException {
		if (crear) {
			if (p.getPlatoId() == null)
				throw new ReglaDeNegocioException("platoId es obligatorio");
			if (p.getFechaInicio() == null)
				throw new ReglaDeNegocioException("fechaInicio es obligatoria");
			if (p.getFechaFin() == null)
				throw new ReglaDeNegocioException("fechaFin es obligatoria");
			if (p.getDescuentoPct() == null)
				throw new ReglaDeNegocioException("descuentoPct es obligatorio");
			if (p.getEstado() == null)
				throw new ReglaDeNegocioException("estado es obligatorio");
		}

		if (p.getFechaInicio() != null && p.getFechaFin() != null) {
			if (!p.getFechaFin().isAfter(p.getFechaInicio())) {
				throw new ReglaDeNegocioException("fechaFin debe ser mayor a fechaInicio");
			}
		}
		if (p.getDescuentoPct() != null) {
			if (p.getDescuentoPct().compareTo(BigDecimal.ZERO) <= 0
					|| p.getDescuentoPct().compareTo(new BigDecimal("100.00")) > 0) {
				throw new ReglaDeNegocioException("descuentoPct debe estar en (0, 100]");
			}
			p.setDescuentoPct(p.getDescuentoPct().setScale(2, RoundingMode.HALF_UP));
		}
		if (p.getEstado() != null) {
			String e = p.getEstado().trim().toUpperCase();
			if (!"A".equals(e) && !"I".equals(e))
				throw new ReglaDeNegocioException("Estado inválido. Permitidos: A/I");
			p.setEstado(e);
		}
	}
}
