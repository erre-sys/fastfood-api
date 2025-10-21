package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.repositories.GrupoPlatoRepository;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PlatoServiceImpl implements PlatoService {

	private final PlatoRepository repo;
	private final GrupoPlatoRepository grupoRepo; // asumimos interfaz similar a tus otros repos

	public PlatoServiceImpl(PlatoRepository repo, GrupoPlatoRepository grupoRepo) {
		this.repo = repo;
		this.grupoRepo = grupoRepo;
	}

	@Override
	public void crear(Plato p)
			throws RegistroDuplicadoException, ReglaDeNegocioException, EntidadNoEncontradaException {
		normalizar(p);
		validarBasico(p);

		if (repo.existePorCodigo(p.getCodigo())) {
			throw new RegistroDuplicadoException("Código de plato ya existe");
		}

		// validar grupo activo
		var g = grupoRepo.buscarPorId(p.getGrupoPlatoId());
		if (!"A".equalsIgnoreCase(g.getEstado())) {
			throw new ReglaDeNegocioException("El grupo de plato no está activo");
		}

		// forzar flags controlados por SP
		p.setEnPromocion("N");
		p.setDescuentoPct(BigDecimal.ZERO);

		repo.crear(p);
	}

	@Override
	public void actualizar(Plato p)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException {
		if (p.getId() == null)
			throw new ReglaDeNegocioException("El id es obligatorio para actualizar");
		Plato actual = repo.buscarPorId(p.getId());

		// si cambia código, validar duplicado
		if (notBlank(p.getCodigo()) && !p.getCodigo().equalsIgnoreCase(actual.getCodigo())
				&& repo.existePorCodigo(p.getCodigo())) {
			throw new RegistroDuplicadoException("Código de plato ya existe");
		}

		// si cambia grupo, validar que exista y esté activo
		if (p.getGrupoPlatoId() != null && !p.getGrupoPlatoId().equals(actual.getGrupoPlatoId())) {
			var g = grupoRepo.buscarPorId(p.getGrupoPlatoId());
			if (!"A".equalsIgnoreCase(g.getEstado())) {
				throw new ReglaDeNegocioException("El grupo de plato no está activo");
			}
		}

		// aplicar cambios permitidos
		actual.setCodigo(coalesce(p.getCodigo(), actual.getCodigo()));
		actual.setNombre(coalesce(p.getNombre(), actual.getNombre()));
		actual.setGrupoPlatoId(p.getGrupoPlatoId() != null ? p.getGrupoPlatoId() : actual.getGrupoPlatoId());
		if (p.getPrecioBase() != null)
			actual.setPrecioBase(scale2(p.getPrecioBase()));
		if (notBlank(p.getEstado()))
			actual.setEstado(upper1(p.getEstado()));

		// IGNORAR cualquier intento de setear enPromocion/descuentoPct desde el DTO
		// conservar valores de DB
		// actual.setEnPromocion(actual.getEnPromocion());
		// actual.setDescuentoPct(actual.getDescuentoPct());

		normalizar(actual);
		validarBasico(actual);

		repo.actualizar(actual);
	}

	private String coalesce(String a, String b) {
		return notBlank(a) ? a : b;
	}

	@Override
	public void eliminarPorId(Long id) throws EntidadNoEncontradaException {
		Plato found = repo.buscarPorId(id);
		// Alternativa si no quieres delete físico:
		// found.setEstado("I"); repo.actualizar(found); return;
		repo.eliminar(found);
	}

	@Override
	public Plato buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repo.buscarPorId(id);
	}

	@Override
	public List<Plato> activos() {
		return repo.activos();
	}

	@Override
	public Pagina<Plato> obtenerPaginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return repo.paginado(pager, filters);
	}

	/* ===== helpers ===== */
	private void normalizar(Plato p) {
		if (p.getCodigo() != null)
			p.setCodigo(p.getCodigo().trim());
		if (p.getNombre() != null)
			p.setNombre(p.getNombre().trim());
		if (p.getEstado() != null)
			p.setEstado(upper1(p.getEstado()));
		if (p.getPrecioBase() != null)
			p.setPrecioBase(scale2(p.getPrecioBase()));
	}

	private void validarBasico(Plato p) throws ReglaDeNegocioException {
		if (p.getPrecioBase() == null || p.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
			throw new ReglaDeNegocioException("precioBase inválido");
		}
		if (!"A".equalsIgnoreCase(p.getEstado()) && !"I".equalsIgnoreCase(p.getEstado())) {
			throw new ReglaDeNegocioException("Estado inválido. Permitidos: A/I");
		}
		if (isBlank(p.getCodigo()))
			throw new ReglaDeNegocioException("codigo es obligatorio");
		if (isBlank(p.getNombre()))
			throw new ReglaDeNegocioException("nombre es obligatorio");
		if (p.getGrupoPlatoId() == null)
			throw new ReglaDeNegocioException("grupoPlatoId es obligatorio");
	}

	private boolean notBlank(String s) {
		return s != null && !s.trim().isEmpty();
	}

	private boolean isBlank(String s) {
		return !notBlank(s);
	}

	private String upper1(String s) {
		return s == null ? null : s.trim().toUpperCase();
	}

	private BigDecimal scale2(BigDecimal v) {
		return v.setScale(2, RoundingMode.HALF_UP);
	}
}
