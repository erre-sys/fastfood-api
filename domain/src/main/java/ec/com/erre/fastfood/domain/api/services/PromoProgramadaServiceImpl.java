package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.api.repositories.PromoProgramadaRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implementación del servicio de gestión de promociones programadas. Maneja la creación, actualización y consulta de
 * promociones de platos.
 */
@Service
public class PromoProgramadaServiceImpl implements PromoProgramadaService {

	// ===== Constantes =====
	private static final String ESTADO_ACTIVO = "A";
	private static final String ESTADO_INACTIVO = "I";
	private static final BigDecimal DESCUENTO_MINIMO = BigDecimal.ZERO;
	private static final BigDecimal DESCUENTO_MAXIMO = new BigDecimal("100.00");
	private static final int ESCALA_DESCUENTO = 2;

	// ===== Mensajes de Error =====
	private static final String MSG_PLATO_INACTIVO = "El plato no está activo";
	private static final String MSG_ID_OBLIGATORIO = "El id es obligatorio para actualizar";
	private static final String MSG_PLATO_ID_OBLIGATORIO = "platoId es obligatorio";
	private static final String MSG_FECHA_INICIO_OBLIGATORIA = "fechaInicio es obligatoria";
	private static final String MSG_FECHA_FIN_OBLIGATORIA = "fechaFin es obligatoria";
	private static final String MSG_DESCUENTO_OBLIGATORIO = "descuentoPct es obligatorio";
	private static final String MSG_ESTADO_OBLIGATORIO = "estado es obligatorio";
	private static final String MSG_FECHA_FIN_INVALIDA = "fechaFin debe ser mayor a fechaInicio";
	private static final String MSG_DESCUENTO_FUERA_RANGO = "descuentoPct debe estar en (0, 100]";
	private static final String MSG_ESTADO_INVALIDO = "Estado inválido. Permitidos: A/I";

	private final PromoProgramadaRepository promoRepository;
	private final PlatoRepository platoRepository;

	public PromoProgramadaServiceImpl(PromoProgramadaRepository promoRepository, PlatoRepository platoRepository) {
		this.promoRepository = promoRepository;
		this.platoRepository = platoRepository;
	}

	/**
	 * Crea una nueva promoción programada.
	 *
	 * @param promocion la promoción a crear
	 * @param usuarioSub identificador del usuario creador
	 * @return el ID de la promoción creada
	 * @throws EntidadNoEncontradaException si el plato no existe
	 * @throws ReglaDeNegocioException si las validaciones de negocio fallan
	 */
	@Override
	@Transactional
	public Long crear(PromoProgramada promocion, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		validarCamposObligatorios(promocion);
		validarReglasDeNegocio(promocion);
		normalizarDatos(promocion);
		validarPlatoActivo(promocion.getPlatoId());

		promocion.setCreadoPorSub(usuarioSub);
		return promoRepository.crear(promocion);
	}

	/**
	 * Actualiza una promoción existente.
	 *
	 * @param promocion la promoción con los datos actualizados
	 * @param usuarioSub identificador del usuario que actualiza
	 * @throws EntidadNoEncontradaException si la promoción o el plato no existen
	 * @throws ReglaDeNegocioException si las validaciones de negocio fallan
	 */
	@Override
	@Transactional
	public void actualizar(PromoProgramada promocion, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {
		if (promocion.getId() == null) {
			throw new ReglaDeNegocioException(MSG_ID_OBLIGATORIO);
		}

		promoRepository.buscarPorId(promocion.getId());
		validarReglasDeNegocio(promocion);
		normalizarDatos(promocion);

		if (promocion.getPlatoId() != null) {
			validarPlatoActivo(promocion.getPlatoId());
		}

		promocion.setCreadoPorSub(usuarioSub);
		promoRepository.actualizar(promocion);
	}

	/**
	 * Busca una promoción por su ID.
	 *
	 * @param id el ID de la promoción
	 * @return la promoción encontrada
	 * @throws EntidadNoEncontradaException si no existe
	 */
	@Override
	@Transactional(readOnly = true)
	public PromoProgramada buscarPorId(Long id) throws EntidadNoEncontradaException {
		return promoRepository.buscarPorId(id);
	}

	/**
	 * Lista todas las promociones de un plato específico.
	 *
	 * @param platoId el ID del plato
	 * @return lista de promociones del plato
	 * @throws EntidadNoEncontradaException si el plato no existe
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PromoProgramada> listarPorPlato(Long platoId) throws EntidadNoEncontradaException {
		platoRepository.buscarPorId(platoId);
		return promoRepository.listarPorPlato(platoId);
	}

	/**
	 * Obtiene un listado paginado de promociones con filtros.
	 *
	 * @param pager información de paginación y ordenamiento
	 * @param filters criterios de búsqueda
	 * @return página de promociones
	 */
	@Override
	@Transactional(readOnly = true)
	public Pagina<PromoProgramada> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		return promoRepository.paginadoPorFiltros(pager, filters);
	}

	/* ===== Métodos de Validación ===== */

	/**
	 * Valida que todos los campos obligatorios estén presentes al crear.
	 */
	private void validarCamposObligatorios(PromoProgramada promocion) throws ReglaDeNegocioException {
		if (promocion.getPlatoId() == null) {
			throw new ReglaDeNegocioException(MSG_PLATO_ID_OBLIGATORIO);
		}
		if (promocion.getFechaInicio() == null) {
			throw new ReglaDeNegocioException(MSG_FECHA_INICIO_OBLIGATORIA);
		}
		if (promocion.getFechaFin() == null) {
			throw new ReglaDeNegocioException(MSG_FECHA_FIN_OBLIGATORIA);
		}
		if (promocion.getDescuentoPct() == null) {
			throw new ReglaDeNegocioException(MSG_DESCUENTO_OBLIGATORIO);
		}
		if (promocion.getEstado() == null) {
			throw new ReglaDeNegocioException(MSG_ESTADO_OBLIGATORIO);
		}
	}

	/**
	 * Valida las reglas de negocio de la promoción.
	 */
	private void validarReglasDeNegocio(PromoProgramada promocion) throws ReglaDeNegocioException {
		validarRangoFechas(promocion);
		validarRangoDescuento(promocion);
		validarEstado(promocion);
	}

	/**
	 * Valida que la fecha fin sea posterior a la fecha inicio.
	 */
	private void validarRangoFechas(PromoProgramada promocion) throws ReglaDeNegocioException {
		if (promocion.getFechaInicio() != null && promocion.getFechaFin() != null) {
			if (!promocion.getFechaFin().isAfter(promocion.getFechaInicio())) {
				throw new ReglaDeNegocioException(MSG_FECHA_FIN_INVALIDA);
			}
		}
	}

	/**
	 * Valida que el porcentaje de descuento esté en el rango (0, 100].
	 */
	private void validarRangoDescuento(PromoProgramada promocion) throws ReglaDeNegocioException {
		if (promocion.getDescuentoPct() != null) {
			BigDecimal descuento = promocion.getDescuentoPct();
			if (descuento.compareTo(DESCUENTO_MINIMO) <= 0 || descuento.compareTo(DESCUENTO_MAXIMO) > 0) {
				throw new ReglaDeNegocioException(MSG_DESCUENTO_FUERA_RANGO);
			}
		}
	}

	/**
	 * Valida que el estado sea A (Activo) o I (Inactivo).
	 */
	private void validarEstado(PromoProgramada promocion) throws ReglaDeNegocioException {
		if (promocion.getEstado() != null) {
			String estado = promocion.getEstado().trim().toUpperCase();
			if (!ESTADO_ACTIVO.equals(estado) && !ESTADO_INACTIVO.equals(estado)) {
				throw new ReglaDeNegocioException(MSG_ESTADO_INVALIDO);
			}
		}
	}

	/**
	 * Valida que el plato exista y esté activo.
	 */
	private void validarPlatoActivo(Long platoId) throws EntidadNoEncontradaException, ReglaDeNegocioException {
		Plato plato = platoRepository.buscarPorId(platoId);
		if (!ESTADO_ACTIVO.equalsIgnoreCase(plato.getEstado())) {
			throw new ReglaDeNegocioException(MSG_PLATO_INACTIVO);
		}
	}

	/* ===== Métodos de Normalización ===== */

	/**
	 * Normaliza los datos de la promoción (escala de decimales, mayúsculas, etc.).
	 */
	private void normalizarDatos(PromoProgramada promocion) {
		normalizarDescuento(promocion);
		normalizarEstado(promocion);
	}

	/**
	 * Normaliza el porcentaje de descuento a 2 decimales.
	 */
	private void normalizarDescuento(PromoProgramada promocion) {
		if (promocion.getDescuentoPct() != null) {
			BigDecimal descuentoNormalizado = promocion.getDescuentoPct().setScale(ESCALA_DESCUENTO,
					RoundingMode.HALF_UP);
			promocion.setDescuentoPct(descuentoNormalizado);
		}
	}

	/**
	 * Normaliza el estado a mayúsculas.
	 */
	private void normalizarEstado(PromoProgramada promocion) {
		if (promocion.getEstado() != null) {
			String estadoNormalizado = promocion.getEstado().trim().toUpperCase();
			promocion.setEstado(estadoNormalizado);
		}
	}
}
