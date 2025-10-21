package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Servicio de platos </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PlatoService {
	void crear(Plato p) throws RegistroDuplicadoException, ReglaDeNegocioException, EntidadNoEncontradaException;

	void actualizar(Plato p) throws EntidadNoEncontradaException, RegistroDuplicadoException, ReglaDeNegocioException;

	void eliminarPorId(Long id) throws EntidadNoEncontradaException;

	Plato buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<Plato> activos();

	Pagina<Plato> obtenerPaginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
