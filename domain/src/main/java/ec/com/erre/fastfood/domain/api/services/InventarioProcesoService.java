package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.api.dtos.AjusteInventarioDto;

public interface InventarioProcesoService {
	void ajustar(AjusteInventarioDto dto) throws ReglaDeNegocioException;
}