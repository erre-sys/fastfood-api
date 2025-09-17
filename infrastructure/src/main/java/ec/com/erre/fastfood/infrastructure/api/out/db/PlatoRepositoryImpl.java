package ec.com.erre.fastfood.infrastructure.api.out.db;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.PlatoEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.PlatoMapper;
import ec.com.erre.fastfood.infrastructure.commons.repositories.JPABaseRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QPlatoEntity.platoEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class PlatoRepositoryImpl extends JPABaseRepository<PlatoEntity, Long> implements PlatoRepository {
	private final PlatoMapper platoMapper;

	public PlatoRepositoryImpl(EntityManager entityManager, PlatoMapper platoMapper) {
		super(PlatoEntity.class, entityManager);
		this.platoMapper = platoMapper;
	}

	@Override
	public List<Plato> buscarTodos() {
		List<PlatoEntity> entities = getQueryFactory().selectFrom(platoEntity).orderBy(platoEntity.id.asc()).fetch();
		return platoMapper.entitiesToDomains(entities);
	}

	@Override
	public Plato buscarPorId(Long id) throws EntidadNoEncontradaException {
		PlatoEntity entity = getQueryFactory().selectFrom(platoEntity).where(platoEntity.id.eq(id)).fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException(String.format("No existe el grupo con el id %s", id));

		return platoMapper.entityToDomain(entity);
	}

	@Override
	public List<Plato> buscarPorGrupoId(Long grupoId) {
		List<PlatoEntity> entities = getQueryFactory().selectFrom(platoEntity)
				.where(platoEntity.grupoPlato.id.eq(grupoId)).fetch();

		return platoMapper.entitiesToDomains(entities);
	}

	@Override
	public Plato buscarPorNombre(String nombre) {
		PlatoEntity entity = getQueryFactory().selectFrom(platoEntity).where(platoEntity.nombre.eq(nombre))
				.fetchFirst();
		return platoMapper.entityToDomain(entity);
	}

	@Override
	public void crear(Plato plato) {
		this.save(platoMapper.domainToEntity(plato));
	}

	@Override
	public void actualizar(Plato plato) {
		this.save(platoMapper.domainToEntity(plato));
	}
}
