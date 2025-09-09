package ec.com.erre.fastfood.infrastructure.commons.security;

public class ConnectedUser {

	public static ThreadLocal<String> userName = new ThreadLocal<>();
	public static ThreadLocal<String> token = new ThreadLocal<>();

}
