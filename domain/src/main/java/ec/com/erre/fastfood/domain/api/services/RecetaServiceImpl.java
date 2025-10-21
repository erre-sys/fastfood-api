package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.models.api.RecetaItem;
import ec.com.erre.fastfood.domain.api.repositories.IngredienteRepository;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.api.repositories.RecetaRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecetaServiceImpl implements RecetaService {

	private final RecetaRepository recetaRepository;
	private final PlatoRepository platoRepository;
	private final IngredienteRepository ingredienteRepository;

	public RecetaServiceImpl(RecetaRepository recetaRepository, PlatoRepository platoRepository,
			IngredienteRepository ingredienteRepository) {
		this.recetaRepository = recetaRepository;
		this.platoRepository = platoRepository;
		this.ingredienteRepository = ingredienteRepository;
	}

	@Override
	public List<RecetaItem> listarPorPlato(Long platoId) throws EntidadNoEncontradaException {
		platoRepository.buscarPorId(platoId);
		return recetaRepository.obtenerPorPlato(platoId);
	}

	@Override
	@Transactional
	public void reemplazarReceta(Long platoId, List<RecetaItem> items)
			throws EntidadNoEncontradaException, ReglaDeNegocioException {

		// 1) Plato debe existir y estar activo
		Plato plato = platoRepository.buscarPorId(platoId);
		if (!"A".equalsIgnoreCase(plato.getEstado())) {
			throw new ReglaDeNegocioException("El plato no está activo");
		}

		// 2) Validaciones por item
		if (items == null || items.isEmpty()) {
			throw new ReglaDeNegocioException("La receta no puede ser vacía");
		}

		Set<Long> vistos = new HashSet<>();
		for (RecetaItem it : items) {
			if (it.getIngredienteId() == null) {
				throw new ReglaDeNegocioException("Cada item debe tener ingredienteId");
			}
			if (!vistos.add(it.getIngredienteId())) {
				throw new ReglaDeNegocioException("Ingrediente duplicado en la receta: " + it.getIngredienteId());
			}
			if (it.getCantidad() == null || it.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
				throw new ReglaDeNegocioException("La cantidad debe ser > 0 para ingrediente " + it.getIngredienteId());
			}

			Ingrediente ing = ingredienteRepository.buscarPorId(it.getIngredienteId());
			if (!"A".equalsIgnoreCase(ing.getEstado())) {
				throw new ReglaDeNegocioException("Ingrediente inactivo: " + it.getIngredienteId());
			}
			if (!"S".equalsIgnoreCase(ing.getAplicaComida())) {
				throw new ReglaDeNegocioException("Ingrediente no aplica para comida: " + it.getIngredienteId());
			}

			// normaliza datos
			it.setPlatoId(platoId);
			it.setCantidad(scale3(it.getCantidad()));
		}

		// 3) Reemplazo atómico
		recetaRepository.reemplazarReceta(platoId, items);
	}

	/* helpers */
	private BigDecimal scale3(BigDecimal val) {
		return val.setScale(3, RoundingMode.HALF_UP);
	}
}
