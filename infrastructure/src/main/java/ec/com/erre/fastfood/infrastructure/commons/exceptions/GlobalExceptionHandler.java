package ec.com.erre.fastfood.infrastructure.commons.exceptions;

import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

	@ExceptionHandler(EntidadNoEncontradaException.class)
	public ResponseEntity<ErrorResponse> handleEntidadNoEncontrada(EntidadNoEncontradaException e) {
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		log.error(e.getMessage(), e);
		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Error en el servidor, por favor contacte al administrador: ".concat(e.getMessage()));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(error);
	}

}
