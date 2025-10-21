package ec.com.erre.fastfood.infrastructure.api.out.db;

import ec.com.erre.fastfood.domain.api.models.api.RecetaItem;
import ec.com.erre.fastfood.domain.api.repositories.RecetaRepository;
import ec.com.erre.fastfood.infrastructure.api.entities.RecetaItemEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.RecetaItemPK;
import ec.com.erre.fastfood.infrastructure.api.mappers.RecetaItemMapper;
import ec.com.erre.fastfood.infrastructure.commons.repositories.*;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QRecetaItemEntity.recetaItemEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class RecetaRepositoryImpl extends JPABaseRepository<RecetaItemEntity, RecetaItemPK>
		implements RecetaRepository {

	private final RecetaItemMapper mapper;
	private final EntityManager em;

	public RecetaRepositoryImpl(EntityManager em, RecetaItemMapper mapper) {
		super(RecetaItemEntity.class, em);
		this.mapper = mapper;
		this.em = em;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RecetaItem> obtenerPorPlato(Long platoId) {
		List<RecetaItemEntity> list = getQueryFactory().selectFrom(recetaItemEntity)
				.where(recetaItemEntity.platoId.eq(platoId)).orderBy(recetaItemEntity.ingredienteId.asc()).fetch();
		return list.stream().map(mapper::entityToDomain).toList();
	}

	@Override
	public void reemplazarReceta(Long platoId, List<RecetaItem> items) {
		getQueryFactory().delete(recetaItemEntity).where(recetaItemEntity.platoId.eq(platoId)).execute();

		for (RecetaItem it : items) {
			RecetaItemEntity e = mapper.domainToEntity(it);
			e.setPlatoId(platoId);
			em.persist(e);
		}
	}
}
