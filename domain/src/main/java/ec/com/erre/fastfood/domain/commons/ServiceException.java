package ec.com.erre.fastfood.domain.commons;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 2896352477488023477L;

	public ServiceException(String message) {
		super(message);
	}

}