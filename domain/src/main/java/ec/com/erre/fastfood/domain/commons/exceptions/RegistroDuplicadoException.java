package ec.com.erre.fastfood.domain.commons.exceptions;

public class RegistroDuplicadoException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 2061287620328825383L;

	public RegistroDuplicadoException(String mensaje) {
		super(mensaje);
	}
}
