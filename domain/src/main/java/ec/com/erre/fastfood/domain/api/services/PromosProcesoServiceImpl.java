package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.commons.exceptions.ServiceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromosProcesoServiceImpl implements PromosProcesoService {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public void aplicar() throws ServiceException {
		try {
			jakarta.persistence.Query q = em.createNativeQuery("CALL fastfood.sp_promos_aplicar(NOW())");
			q.executeUpdate();
		} catch (RuntimeException ex) {
			throw new ServiceException("Error al aplicar promociones: " + deepestMessage(ex));
		}
	}

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
