package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Proveedor;
import ec.com.erre.fastfood.domain.api.repositories.ProveedorRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl implements ProveedorService {

	private final ProveedorRepository repo;

	public ProveedorServiceImpl(ProveedorRepository repo) {
		this.repo = repo;
	}

	@Override
	public void crear(Proveedor create) throws RegistroDuplicadoException, ReglaDeNegocioException {
		normalizar(create);
		validarEstado(create.getEstado());

		if (repo.existePorNombre(create.getNombre())) {
			throw new RegistroDuplicadoException("El nombre de proveedor ya existe");
		}
		if (create.getRuc() != null && !create.getRuc().isBlank() && repo.existePorRuc(create.getRuc())) {
			throw new RegistroDuplicadoException("El RUC ya existe");
		}
		repo.crear(create);
	}

	@Override
	public void actualizar(Proveedor update)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException {
		if (update.getId() == null)
			throw new ReglaDeNegocioException("El id es obligatorio para actualizar");
		Proveedor actual = repo.buscarPorId(update.getId());

		if (notBlank(update.getNombre()) && !update.getNombre().equalsIgnoreCase(actual.getNombre())
				&& repo.existePorNombre(update.getNombre())) {
			throw new RegistroDuplicadoException("El nombre de proveedor ya existe");
		}
		if (notBlank(update.getRuc()) && (actual.getRuc() == null || !update.getRuc().equalsIgnoreCase(actual.getRuc()))
				&& repo.existePorRuc(update.getRuc())) {
			throw new RegistroDuplicadoException("La identificación ya existe");
		}
		if (notBlank(update.getEstado())) {
			validarEstado(update.getEstado());
		}

		// aplicar cambios
		actual.setNombre(coalesce(update.getNombre(), actual.getNombre()));
		actual.setRuc(coalesce(update.getRuc(), actual.getRuc()));
		actual.setTelefono(coalesce(update.getTelefono(), actual.getTelefono()));
		actual.setEmail(coalesce(update.getEmail(), actual.getEmail()));
		actual.setRuc(coalesce(update.getRuc(), actual.getRuc()));
		actual.setEstado(coalesce(upper1(update.getEstado()), actual.getEstado()));

		normalizar(actual);
		repo.actualizar(actual);
	}

	@Override
	public void eliminarPorId(Long id) throws EntidadNoEncontradaException {
		Proveedor found = repo.buscarPorId(id);
		repo.eliminar(found);
	}

	@Override
	public Proveedor buscarPorId(Long id) throws EntidadNoEncontradaException {
		return repo.buscarPorId(id);
	}

	@Override
	public List<Proveedor> listarTodos() {
		return repo.listarTodos();
	}

	@Override
	public Pagina<Proveedor> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return repo.paginadoPorFiltros(pager, filters);
	}

	/* ===== helpers ===== */
	private void normalizar(Proveedor p) {
		if (p.getNombre() != null)
			p.setNombre(p.getNombre().trim());
		if (p.getRuc() != null)
			p.setRuc(p.getRuc().trim());
		if (p.getTelefono() != null)
			p.setTelefono(p.getTelefono().trim());
		if (p.getEmail() != null)
			p.setEmail(p.getEmail().trim());
		if (p.getRuc() != null)
			p.setRuc(p.getRuc().trim());
		if (p.getEstado() != null)
			p.setEstado(upper1(p.getEstado()));
	}

	private void validarEstado(String e) throws ReglaDeNegocioException {
		String v = upper1(e);
		if (!"A".equals(v) && !"I".equals(v))
			throw new ReglaDeNegocioException("Estado inválido. Valores permitidos: A/I");
	}

	private boolean notBlank(String s) {
		return s != null && !s.trim().isEmpty();
	}

	private String coalesce(String a, String b) {
		return notBlank(a) ? a : b;
	}

	private String upper1(String s) {
		return s == null ? null : s.trim().toUpperCase();
	}
}
