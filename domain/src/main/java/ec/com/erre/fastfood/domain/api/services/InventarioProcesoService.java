package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;

import java.math.BigDecimal;

public interface InventarioProcesoService {
	void ajustar(Long ingredienteId, BigDecimal cantidad, String referencia, String usuarioSub, boolean permitirNeg)
			throws ReglaDeNegocioException;
}