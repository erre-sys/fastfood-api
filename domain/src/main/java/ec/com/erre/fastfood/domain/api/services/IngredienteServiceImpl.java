package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.api.repositories.GrupoIngredienteRepository;
import ec.com.erre.fastfood.domain.api.repositories.IngredienteRepository;
import ec.com.erre.fastfood.domain.api.repositories.InventarioRepository;
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
public class IngredienteServiceImpl implements IngredienteService {

	private static final java.util.Set<String> UNIDADES_PERMITIDAS = new java.util.HashSet<>(
			java.util.Arrays.asList("KG", "G", "LT", "ML", "UND", "PACK", "PORC"));

	private final IngredienteRepository repo;
	private final GrupoIngredienteRepository grupoRepo;
	private final InventarioRepository inventarioRepo; // expuesto en tu Sprint de Inventario

	public IngredienteServiceImpl(IngredienteRepository repo, GrupoIngredienteRepository grupoRepo,
			InventarioRepository inventarioRepo) {
		this.repo = repo;
		this.grupoRepo = grupoRepo;
		this.inventarioRepo = inventarioRepo;
	}

	@Override
	public void crear(Ingrediente i)
			throws RegistroDuplicadoException, ReglaDeNegocioException, EntidadNoEncontradaException {
		normalizar(i);
		validar(i, true);

		if (repo.existePorCodigo(i.getCodigo())) {
			throw new RegistroDuplicadoException("El código de ingrediente ya existe");
		}

		GrupoIngrediente g = grupoRepo.buscarPorId(i.getGrupoIngredienteId());
		if (!"A".equalsIgnoreCase(g.getEstado())) {
			throw new ReglaDeNegocioException("El grupo de ingrediente no está activo");
		}

		if ("N".equalsIgnoreCase(getAplicaComidaGrupo(g)) && "S".equalsIgnoreCase(i.getAplicaComida())) {
			throw new ReglaDeNegocioException(
					"El grupo no aplica a comida; el ingrediente no puede marcarse como comida");
		}

		repo.crear(i);
	}

	@Override
	public void actualizar(Ingrediente i)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException {
		if (i.getId() == null)
			throw new ReglaDeNegocioException("El id es obligatorio para actualizar");
		Ingrediente actual = repo.buscarPorId(i.getId());

		// si cambia código, validar duplicado
		if (notBlank(i.getCodigo()) && !i.getCodigo().equalsIgnoreCase(actual.getCodigo())
				&& repo.existePorCodigo(i.getCodigo())) {
			throw new RegistroDuplicadoException("El código de ingrediente ya existe");
		}

		// si cambia grupo, validar
		if (i.getGrupoIngredienteId() != null && !i.getGrupoIngredienteId().equals(actual.getGrupoIngredienteId())) {
			GrupoIngrediente g = grupoRepo.buscarPorId(i.getGrupoIngredienteId());
			if (!"A".equalsIgnoreCase(g.getEstado())) {
				throw new ReglaDeNegocioException("El grupo de ingrediente no está activo");
			}
			if ("N".equalsIgnoreCase(getAplicaComidaGrupo(g))
					&& "S".equalsIgnoreCase(nvl(i.getAplicaComida(), actual.getAplicaComida()))) {
				throw new ReglaDeNegocioException(
						"El grupo no aplica a comida; el ingrediente no puede marcarse como comida");
			}
		}

		// aplicar cambios permitidos
		actual.setGrupoIngredienteId(
				i.getGrupoIngredienteId() != null ? i.getGrupoIngredienteId() : actual.getGrupoIngredienteId());
		actual.setCodigo(coalesce(i.getCodigo(), actual.getCodigo()));
		actual.setNombre(coalesce(i.getNombre(), actual.getNombre()));
		actual.setUnidad(coalesce(i.getUnidad(), actual.getUnidad()));
		actual.setEsExtra(coalesce(i.getEsExtra(), actual.getEsExtra()));
		actual.setPrecioExtra(i.getPrecioExtra() != null ? scale2(i.getPrecioExtra()) : actual.getPrecioExtra());
		actual.setStockMinimo(i.getStockMinimo() != null ? scale3(i.getStockMinimo()) : actual.getStockMinimo());
		actual.setAplicaComida(coalesce(i.getAplicaComida(), actual.getAplicaComida()));
		actual.setEstado(coalesce(i.getEstado(), actual.getEstado()));

		normalizar(actual);
		validar(actual, false);

		repo.actualizar(actual);
	}

	@Override
	public void eliminarPorId(Long id) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Ingrediente ing = repo.buscarPorId(id);

		// Verificación de inventario
		BigDecimal stock = inventarioRepo.obtenerStockActual(id); // retorna 0 si no hay fila
		boolean tieneMovs = inventarioRepo.tieneMovimientos(id);

		if (stock != null && stock.compareTo(BigDecimal.ZERO) != 0) {
			// no borramos; inactivamos
			ing.setEstado("I");
			repo.actualizar(ing);
			throw new ReglaDeNegocioException(
					"No se puede eliminar: hay stock distinto de 0. Se inactivó el ingrediente.");
		}
		if (tieneMovs) {
			ing.setEstado("I");
			repo.actualizar(ing);
			throw new ReglaDeNegocioException(
					"No se puede eliminar: existen movimientos de inventario. Se inactivó el ingrediente.");
		}

		repo.eliminar(ing);
	}

	@Override
	public Ingrediente buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repo.buscarPorId(id);
	}

	@Override
	public List<Ingrediente> activos() {
		return repo.activos();
	}

	@Override
	public Pagina<Ingrediente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return repo.paginado(pager, filters);
	}

	/* ===== helpers ===== */
	private void normalizar(Ingrediente i) {
		if (i.getCodigo() != null)
			i.setCodigo(i.getCodigo().trim());
		if (i.getNombre() != null)
			i.setNombre(i.getNombre().trim());
		if (i.getUnidad() != null)
			i.setUnidad(i.getUnidad().trim().toUpperCase());
		if (i.getEsExtra() != null)
			i.setEsExtra(upper1(i.getEsExtra()));
		if (i.getAplicaComida() != null)
			i.setAplicaComida(upper1(i.getAplicaComida()));
		if (i.getEstado() != null)
			i.setEstado(upper1(i.getEstado()));
		if (i.getPrecioExtra() != null)
			i.setPrecioExtra(scale2(i.getPrecioExtra()));
		if (i.getStockMinimo() != null)
			i.setStockMinimo(scale3(i.getStockMinimo()));
	}

	private void validar(Ingrediente i, boolean crear) throws ReglaDeNegocioException {
		if (crear) {
			if (i.getGrupoIngredienteId() == null)
				throw new ReglaDeNegocioException("grupoIngredienteId es obligatorio");
			if (isBlank(i.getCodigo()))
				throw new ReglaDeNegocioException("codigo es obligatorio");
			if (isBlank(i.getNombre()))
				throw new ReglaDeNegocioException("nombre es obligatorio");
			if (isBlank(i.getUnidad()))
				throw new ReglaDeNegocioException("unidad es obligatoria");
			if (isBlank(i.getEsExtra()))
				throw new ReglaDeNegocioException("esExtra es obligatorio");
			if (i.getStockMinimo() == null || i.getStockMinimo().compareTo(BigDecimal.ZERO) < 0)
				throw new ReglaDeNegocioException("stockMinimo inválido");
			if (isBlank(i.getAplicaComida()))
				throw new ReglaDeNegocioException("aplicaComida es obligatorio");
			if (isBlank(i.getEstado()))
				throw new ReglaDeNegocioException("estado es obligatorio");
		}

		if (!UNIDADES_PERMITIDAS.contains(i.getUnidad()))
			throw new ReglaDeNegocioException("Unidad inválida. Permitidas: " + UNIDADES_PERMITIDAS);

		if (!inSet(i.getEsExtra(), "S", "N"))
			throw new ReglaDeNegocioException("esExtra inválido (S/N)");

		if (!inSet(i.getAplicaComida(), "S", "N"))
			throw new ReglaDeNegocioException("aplicaComida inválido (S/N)");

		if (!inSet(i.getEstado(), "A", "I"))
			throw new ReglaDeNegocioException("estado inválido (A/I)");

		// precioExtra según esExtra
		if ("S".equalsIgnoreCase(i.getEsExtra())) {
			if (i.getPrecioExtra() == null || i.getPrecioExtra().compareTo(BigDecimal.ZERO) < 0)
				throw new ReglaDeNegocioException("precioExtra debe ser >= 0 cuando esExtra='S'");
		} else {
			i.setPrecioExtra(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
		}
	}

	private boolean inSet(String v, String... opts) {
		if (v == null)
			return false;
		for (String o : opts)
			if (o.equalsIgnoreCase(v))
				return true;
		return false;
	}

	private String getAplicaComidaGrupo(GrupoIngrediente g) {
		try {
			var m = g.getClass().getMethod("getAplicaComida");
			Object r = m.invoke(g);
			return r == null ? "S" : r.toString();
		} catch (Exception ignore) {
			return "S";
		}
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

	private BigDecimal scale3(BigDecimal v) {
		return v.setScale(3, RoundingMode.HALF_UP);
	}

	private String nvl(String v, String def) {
		return v == null ? def : v;
	}

	private String nvl(String v) {
		return nvl(v, "");
	}

	private String nvl(String v, java.util.function.Supplier<String> sup) {
		return v == null ? sup.get() : v;
	}

	private String coalesce(String a, String b) {
		return notBlank(a) ? a : b;
	}
}
