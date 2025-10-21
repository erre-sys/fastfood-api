package ec.com.erre.fastfood.domain.api.repositories;

import ec.com.erre.fastfood.domain.api.models.api.RecetaItem;

import java.util.List;

/**
 * <b>Repositorio de platos </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface RecetaRepository {

	List<RecetaItem> obtenerPorPlato(Long platoId);

	void reemplazarReceta(Long platoId, List<RecetaItem> items);
}