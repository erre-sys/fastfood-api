package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.Inventario;
import ec.com.erre.fastfood.domain.api.models.api.InventarioMov;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <b>Repositorio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface InventarioRepository {

	Pagina<Inventario> listarInventario(PagerAndSortDto pager, String q, boolean soloBajoMinimo);

	Pagina<InventarioMov> listarKardex(Long ingredienteId, LocalDateTime desde, LocalDateTime hasta, String tipo,
			PagerAndSortDto pager);

	Inventario obtenerPorIngrediente(Long ingredienteId) throws EntidadNoEncontradaException;

	BigDecimal obtenerStockActual(Long ingredienteId);

	boolean tieneMovimientos(Long ingredienteId);

}