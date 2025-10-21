package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;

/**
 * <b>Servicio de pago proveedor </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface PromosProcesoService {
	void aplicar() throws ServiceException;
}
