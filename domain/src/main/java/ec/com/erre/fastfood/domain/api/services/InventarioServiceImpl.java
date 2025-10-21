package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Inventario;
import ec.com.erre.fastfood.domain.api.models.api.InventarioMov;
import ec.com.erre.fastfood.domain.api.repositories.InventarioRepository;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <b>Servicio de kardex de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Service
@Transactional(readOnly = true)
public class InventarioServiceImpl implements InventarioService {

	private final InventarioRepository inventarioRepository;

	public InventarioServiceImpl(InventarioRepository inventarioRepository) {
		this.inventarioRepository = inventarioRepository;
	}

	@Override
	public Pagina<Inventario> listarInventario(PagerAndSortDto pager, String q, boolean soloBajoMinimo) {
		return inventarioRepository.listarInventario(pager, q, soloBajoMinimo);
	}

	@Override
	public Pagina<InventarioMov> listarKardex(Long ingredienteId, LocalDateTime desde, LocalDateTime hasta, String tipo,
			PagerAndSortDto pager) {
		return inventarioRepository.listarKardex(ingredienteId, desde, hasta, tipo, pager);
	}
}