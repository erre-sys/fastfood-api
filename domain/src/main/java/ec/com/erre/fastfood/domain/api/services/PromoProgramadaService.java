package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * <b>Servicio de pago proveedor </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PromoProgramadaService {
	Long crear(PromoProgramada p, String usuarioSub) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	void actualizar(PromoProgramada p) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	PromoProgramada buscarPorId(Long id) throws EntidadNoEncontradaException;

	List<PromoProgramada> listarPorPlato(Long platoId) throws EntidadNoEncontradaException;

	Pagina<PromoProgramada> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
