package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Compra;
import ec.com.erre.fastfood.domain.api.models.api.CompraItem;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <b>Servicio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface CompraService {
	Long crear(Compra cabecera, List<CompraItem> items) throws EntidadNoEncontradaException, ReglaDeNegocioException;

	Compra buscarPorId(Long compraId) throws EntidadNoEncontradaException;

	Pagina<Compra> buscarPaginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
