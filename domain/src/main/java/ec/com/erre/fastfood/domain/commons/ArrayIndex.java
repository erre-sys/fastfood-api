package ec.com.erre.fastfood.domain.commons;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <b> Descripcion de la clase, interface o enumeracion. </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ArrayIndex {
	int value();

}
