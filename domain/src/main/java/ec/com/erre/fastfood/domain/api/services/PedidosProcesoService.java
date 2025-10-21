package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;

public interface PedidosProcesoService {
	void entregar(Long pedidoId, String usuarioSub)
			throws EntidadNoEncontradaException, ReglaDeNegocioException, ServiceException;

}