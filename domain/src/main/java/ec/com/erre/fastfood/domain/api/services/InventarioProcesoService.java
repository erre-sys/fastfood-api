package ec.com.erre.fastfood.domain.api.services;

import java.math.BigDecimal;

public interface InventarioProcesoService {
	void ajustar(Long ingredienteId, BigDecimal cantidad, String referencia, String usuarioSub, boolean permitirNeg);
}