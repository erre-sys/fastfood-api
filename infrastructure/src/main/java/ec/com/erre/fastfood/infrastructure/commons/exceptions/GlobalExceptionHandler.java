package ec.com.erre.fastfood.infrastructure.commons.exceptions;

import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

	@ExceptionHandler(EntidadNoEncontradaException.class)
	public ResponseEntity<ErrorResponse> handleEntidadNoEncontrada(EntidadNoEncontradaException e) {
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(ReglaDeNegocioException.class)
	public ResponseEntity<ErrorResponse> handleReglaDeNegocio(ReglaDeNegocioException e) {
		log.warn("Regla de negocio violada: {}", e.getMessage());
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
		String paramName = e.getName();
		String paramValue = e.getValue() != null ? e.getValue().toString() : "null";
		String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "desconocido";

		String message = String.format(
				"El parámetro '%s' tiene un formato inválido. Valor recibido: '%s'. "
						+ "Formato esperado para fechas: yyyy-MM-dd HH:mm:ss (ejemplo: 2025-10-24 23:59:59)",
				paramName, paramValue);

		log.warn("Error de conversión de parámetro: {} = {} (tipo esperado: {})", paramName, paramValue, requiredType);

		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(RemoteExecutionException.class)
	public ResponseEntity<ErrorResponse> handleRemoteExecution(RemoteExecutionException e) {
		ErrorResponse error = new ErrorResponse(e.getStatusCode(), e.getMessage());
		return ResponseEntity.status(e.getStatusCode()).body(error);
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
		log.error(e.getMessage(), e);
		ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(error);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
		log.error("Error de integridad de datos: {}", e.getMessage(), e);

		String message = extractMeaningfulMessage(e);

		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
		log.error("Error SQL: {}", e.getMessage(), e);

		String message = extractMeaningfulMessage(e);

		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		log.error("Error inesperado: {}", e.getMessage(), e);

		// Intentar extraer mensaje significativo de errores SQL embebidos
		String message = extractMeaningfulMessage(e);

		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	/**
	 * Extrae mensajes significativos de excepciones SQL anidadas
	 */
	private String extractMeaningfulMessage(Throwable e) {
		String message = e.getMessage();

		// Buscar mensajes específicos en la cadena de excepciones
		Throwable cause = e;
		while (cause != null) {
			String causeMessage = cause.getMessage();
			if (causeMessage != null) {
				// Errores de stored procedures
				if (causeMessage.contains("Stock insuficiente")) {
					// Intentar extraer el ingrediente del mensaje si viene en formato:
					// "Stock insuficiente para ingrediente: Queso" o similar
					return extractStockErrorMessage(causeMessage);
				}
				if (causeMessage.contains("Pedido no existe")) {
					return "El pedido no existe";
				}
				if (causeMessage.contains("Pedido ya finalizado")) {
					return "El pedido ya fue finalizado y no se puede modificar";
				}
				if (causeMessage.contains("Usa sp_pedido_cambiar_estado")) {
					return "No se puede cambiar el estado del pedido directamente. Use el endpoint correspondiente";
				}
				// Errores de constraints
				if (causeMessage.contains("Duplicate entry")) {
					return "El registro ya existe en el sistema";
				}
				if (causeMessage.contains("cannot be null")) {
					String field = extractFieldName(causeMessage);
					return "El campo '" + field + "' es obligatorio";
				}
				if (causeMessage.contains("foreign key constraint fails")) {
					return "No se puede completar la operación porque hay referencias a otros registros";
				}
			}
			cause = cause.getCause();
		}

		// Si no se encontró un mensaje específico, retornar mensaje genérico pero limpio
		if (message != null && message.length() > 200) {
			return "Error en el servidor. Por favor contacte al administrador";
		}

		return message != null ? message : "Error inesperado en el servidor";
	}

	/**
	 * Extrae el nombre del campo de un mensaje de error "cannot be null"
	 */
	private String extractFieldName(String message) {
		try {
			// Formato típico: "Column 'field_name' cannot be null"
			int start = message.indexOf("'") + 1;
			int end = message.indexOf("'", start);
			if (start > 0 && end > start) {
				return message.substring(start, end);
			}
		} catch (Exception ignored) {
		}
		return "desconocido";
	}

	/**
	 * Extrae un mensaje amigable de errores de stock insuficiente
	 */
	private String extractStockErrorMessage(String message) {
		try {
			// Si el mensaje del SP incluye detalles después de ":", extraerlos
			// Ej: "Stock insuficiente: No hay suficiente Queso disponible"
			int colonIndex = message.indexOf(":");
			if (colonIndex > 0 && colonIndex < message.length() - 1) {
				String detalle = message.substring(colonIndex + 1).trim();
				// Limpiar saltos de línea y caracteres extraños
				detalle = detalle.split("\n")[0].split("\r")[0].trim();
				if (!detalle.isEmpty() && detalle.length() < 200) {
					return detalle;
				}
			}
		} catch (Exception ignored) {
		}
		// Mensaje por defecto si no se pudo extraer información adicional
		return "No hay suficiente stock disponible para completar el pedido";
	}

}
