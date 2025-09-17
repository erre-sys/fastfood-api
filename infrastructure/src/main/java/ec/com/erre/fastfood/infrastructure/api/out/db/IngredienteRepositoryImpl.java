package ec.com.erre.fastfood.infrastructure.api.out.db;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.api.repositories.IngredienteRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.IngredienteEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.IngredienteMapper;
import ec.com.erre.fastfood.infrastructure.commons.repositories.JPABaseRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QIngredienteEntity.ingredienteEntity;

/**
 * <b>Implementacion de repositorio para IngredienteRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class IngredienteRepositoryImpl extends JPABaseRepository<IngredienteEntity, Long>
		implements IngredienteRepository {
	private final IngredienteMapper ingredienteMapper;

	public IngredienteRepositoryImpl(EntityManager entityManager, IngredienteMapper ingredienteMapper) {
		super(IngredienteEntity.class, entityManager);
		this.ingredienteMapper = ingredienteMapper;
	}

	@Override
	public List<Ingrediente> buscarTodos() {
		List<IngredienteEntity> entities = getQueryFactory().selectFrom(ingredienteEntity)
				.orderBy(ingredienteEntity.id.asc()).fetch();
		return ingredienteMapper.entitiesToDomains(entities);
	}

	@Override
	public Ingrediente buscarPorId(Long id) throws EntidadNoEncontradaException {
		IngredienteEntity entity = getQueryFactory().selectFrom(ingredienteEntity).where(ingredienteEntity.id.eq(id))
				.fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException(String.format("No existe el grupo con el id %s", id));

		return ingredienteMapper.entityToDomain(entity);
	}

	@Override
	public List<Ingrediente> buscarPorGrupoId(Long grupoId) {
		List<IngredienteEntity> entities = getQueryFactory().selectFrom(ingredienteEntity)
				.where(ingredienteEntity.grupoIngrediente.id.eq(grupoId)).fetch();

		return ingredienteMapper.entitiesToDomains(entities);
	}

	@Override
	public Ingrediente buscarPorNombre(String nombre) {
		IngredienteEntity entity = getQueryFactory().selectFrom(ingredienteEntity)
				.where(ingredienteEntity.nombre.eq(nombre)).fetchFirst();
		return ingredienteMapper.entityToDomain(entity);
	}

	@Override
	public void crear(Ingrediente ingrediente) {
		this.save(ingredienteMapper.domainToEntity(ingrediente));
	}

	@Override
	public void actualizar(Ingrediente ingrediente) {
		this.save(ingredienteMapper.domainToEntity(ingrediente));
	}
}
