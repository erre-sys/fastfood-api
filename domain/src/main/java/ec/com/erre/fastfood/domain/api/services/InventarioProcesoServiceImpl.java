package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.share.api.dtos.AjusteInventarioDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Servicio para procesos de inventario (ajustes manuales)
 *
 * @author eduardo.romero
 * @version 1.0
 */
@Service
@Log4j2
public class InventarioProcesoServiceImpl implements InventarioProcesoService {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public void ajustar(AjusteInventarioDto dto) throws ReglaDeNegocioException {

		if (dto.getIngredienteId() == null) {
			throw new ReglaDeNegocioException("ingredienteId es obligatorio");
		}
		if (dto.getCantidad() == null || dto.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
			throw new ReglaDeNegocioException("cantidad no puede ser cero");
		}

		try {
			Query q = em.createNativeQuery(
					"CALL palaspapas_db.sp_inventario_ajustar(:p_ingrediente, :p_cantidad, :p_ref, :p_sub, :p_neg)");
			q.setParameter("p_ingrediente", dto.getIngredienteId());
			q.setParameter("p_cantidad", dto.getCantidad());
			q.setParameter("p_ref", dto.getReferencia() != null ? dto.getReferencia() : "Ajuste manual");
			q.setParameter("p_sub", dto.getUsuario() != null ? dto.getUsuario() : "SISTEMA");
			q.setParameter("p_neg", "N");
			q.executeUpdate();

			log.info("Ajuste de inventario realizado: ingredienteId={}, cantidad={}, ref={}", dto.getIngredienteId(),
					dto.getCantidad(), dto.getReferencia());

		} catch (RuntimeException ex) {
			String msg = deepestMessage(ex);

			if (msg.contains("Stock insuficiente")) {
				throw new ReglaDeNegocioException(
						"Stock insuficiente para realizar el ajuste. Use permitirNegativo=true si desea forzar.");
			}
			if (msg.contains("Ingrediente sin fila de inventario")) {
				throw new ReglaDeNegocioException("El ingrediente no existe o no tiene inventario inicializado");
			}

			throw new ReglaDeNegocioException("Error al ajustar inventario: " + msg);
		}
	}

	/**
	 * Obtiene el mensaje más profundo de la cadena de excepciones
	 */
	private String deepestMessage(Throwable t) {
		String last = null;
		Throwable cur = t;
		while (cur != null) {
			if (cur.getMessage() != null)
				last = cur.getMessage();
			cur = cur.getCause();
		}
		return last;
	}
}
