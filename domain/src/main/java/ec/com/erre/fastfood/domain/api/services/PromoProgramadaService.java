package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.List;

/**
 * Servicio de gestión de promociones programadas. Maneja la creación, actualización y consulta de promociones de
 * platos.
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PromoProgramadaService {
	/**
	 * Crea una nueva promoción programada.
	 *
	 * @param promocion la promoción a crear
	 * @param usuarioSub identificador del usuario creador
	 * @return el ID de la promoción creada
	 */
	Long crear(PromoProgramada promocion, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException;

	/**
	 * Actualiza una promoción existente.
	 *
	 * @param promocion la promoción con los datos actualizados
	 * @param usuarioSub identificador del usuario que actualiza
	 */
	void actualizar(PromoProgramada promocion, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException;

	/**
	 * Busca una promoción por su ID.
	 *
	 * @param id el ID de la promoción
	 * @return la promoción encontrada
	 */
	PromoProgramada buscarPorId(Long id) throws EntidadNoEncontradaException;

	/**
	 * Lista todas las promociones de un plato específico.
	 *
	 * @param platoId el ID del plato
	 * @return lista de promociones del plato
	 */
	List<PromoProgramada> listarPorPlato(Long platoId) throws EntidadNoEncontradaException;

	/**
	 * Obtiene un listado paginado de promociones con filtros.
	 *
	 * @param pager información de paginación y ordenamiento
	 * @param filters criterios de búsqueda
	 * @return página de promociones
	 */
	Pagina<PromoProgramada> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters);
}
