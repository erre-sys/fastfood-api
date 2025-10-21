package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Servicio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface IngredienteService {
	void crear(Ingrediente i) throws RegistroDuplicadoException, ReglaDeNegocioException, EntidadNoEncontradaException;

	void actualizar(Ingrediente i)
			throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException;

	void eliminarPorId(Long id) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	Ingrediente buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<Ingrediente> activos();

	Pagina<Ingrediente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
