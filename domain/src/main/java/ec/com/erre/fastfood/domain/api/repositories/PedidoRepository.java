package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <b>Repositorio de grupo de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PedidoRepository {
	Pedido buscarPorId(Long id) throws EntidadNoEncontradaException;

	void actualizarTotales(Long pedidoId, BigDecimal totalBruto, BigDecimal totalDescuentos, BigDecimal totalExtras,
			BigDecimal totalNeto);

	Long crear(Pedido ped);

	boolean cambiarEstadoSimple(Long pedidoId, String nuevoEstado);

	boolean anularSiProcede(Long pedidoId);

	Pagina<Pedido> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
