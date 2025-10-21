package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Proveedor;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Servicio de proveedor </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface ProveedorService {
	void crear(Proveedor create) throws RegistroDuplicadoException, ReglaDeNegocioException;

	void actualizar(Proveedor update)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException;

	void eliminarPorId(Long id) throws EntidadNoEncontradaException;

	Proveedor buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<Proveedor> listarTodos();

	Pagina<Proveedor> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
