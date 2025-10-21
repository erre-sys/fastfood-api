package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Proveedor;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Repositorio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface ProveedorRepository {
	boolean existePorNombre(String nombre);

	boolean existePorRuc(String ruc);

	void crear(Proveedor create);

	void actualizar(Proveedor update);

	void eliminar(Proveedor delete);

	Proveedor buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<Proveedor> listarTodos();

	Pagina<Proveedor> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);

}
