package ec.com.erre.fastfood.domain.api.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ec.com.erre.fastfood.domain.api.services.PedidoConstants.*;

/**
 * Utilidades compartidas para servicios de pedidos. Proporciona métodos helper comunes para cálculos y validaciones.
 */
public final class PedidoUtils {

	private PedidoUtils() {
		// Clase de utilidad, no debe ser instanciada
		throw new AssertionError("No se debe instanciar PedidoUtils");
	}

	/**
	 * Retorna el valor por defecto (ZERO) si el BigDecimal es nulo.
	 *
	 * @param valor el valor a verificar
	 * @return el valor o BigDecimal.ZERO si es nulo
	 */
	public static BigDecimal defaultSiNulo(BigDecimal valor) {
		return valor == null ? BigDecimal.ZERO : valor;
	}

	/**
	 * Escala un BigDecimal a 2 decimales (para precios).
	 *
	 * @param valor el valor a escalar
	 * @return el valor escalado a 2 decimales
	 */
	public static BigDecimal escalarPrecio(BigDecimal valor) {
		return valor.setScale(ESCALA_PRECIOS, RoundingMode.HALF_UP);
	}

	/**
	 * Escala un BigDecimal a 3 decimales (para cantidades).
	 *
	 * @param valor el valor a escalar
	 * @return el valor escalado a 3 decimales
	 */
	public static BigDecimal escalarCantidad(BigDecimal valor) {
		return valor.setScale(ESCALA_CANTIDADES, RoundingMode.HALF_UP);
	}

	/**
	 * Normaliza un texto a mayúsculas y sin espacios.
	 *
	 * @param texto el texto a normalizar
	 * @return el texto normalizado o null si es nulo
	 */
	public static String normalizarTexto(String texto) {
		return texto == null ? null : texto.trim().toUpperCase();
	}

	/**
	 * Compara dos BigDecimals con escala de 2 decimales.
	 *
	 * @param a primer valor
	 * @param b segundo valor
	 * @return true si son iguales con 2 decimales de precisión
	 */
	public static boolean igualesConPrecision(BigDecimal a, BigDecimal b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return escalarPrecio(a).compareTo(escalarPrecio(b)) == 0;
	}

	/**
	 * Verifica si un pedido está en estado finalizado (ENTREGADO o ANULADO).
	 *
	 * @param estado el estado del pedido
	 * @return true si el pedido está finalizado
	 */
	public static boolean esPedidoFinalizado(String estado) {
		return ESTADO_ENTREGADO.equalsIgnoreCase(estado) || ESTADO_ANULADO.equalsIgnoreCase(estado);
	}

	/**
	 * Verifica si una entidad está activa.
	 *
	 * @param estado el estado de la entidad
	 * @return true si está activa
	 */
	public static boolean esActivo(String estado) {
		return ESTADO_ACTIVO.equalsIgnoreCase(estado);
	}

	/**
	 * Verifica si un indicador es afirmativo (S).
	 *
	 * @param indicador el indicador
	 * @return true si es 'S'
	 */
	public static boolean esIndicadorAfirmativo(String indicador) {
		return INDICADOR_SI.equalsIgnoreCase(indicador);
	}

	/**
	 * Extrae el mensaje más profundo de una cadena de excepciones.
	 *
	 * @param throwable la excepción raíz
	 * @return el mensaje más profundo o null si no hay
	 */
	public static String extraerMensajeMasProfundo(Throwable throwable) {
		String mensajeUltimo = null;
		Throwable actual = throwable;
		while (actual != null) {
			if (actual.getMessage() != null) {
				mensajeUltimo = actual.getMessage();
			}
			actual = actual.getCause();
		}
		return mensajeUltimo;
	}
}
