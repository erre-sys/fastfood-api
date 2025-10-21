package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.RecetaItem;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;

import java.util.List;

/**
 * <b>Servicio de recetas </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface RecetaService {
	List<RecetaItem> listarPorPlato(Long platoId) throws EntidadNoEncontradaException;

	void reemplazarReceta(Long platoId, List<RecetaItem> items)
			throws EntidadNoEncontradaException, ReglaDeNegocioException;
}
