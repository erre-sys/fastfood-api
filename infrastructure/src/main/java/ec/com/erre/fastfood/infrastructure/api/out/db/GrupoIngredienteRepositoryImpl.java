package ec.com.erre.fastfood.infrastructure.api.out.db;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.repositories.GrupoIngredienteRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;
import ec.com.erre.fastfood.infrastructure.api.entities.GrupoIngredienteEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.GrupoIngredienteMapper;
import ec.com.erre.fastfood.infrastructure.commons.repositories.JPABaseRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QGrupoIngredienteEntity.grupoIngredienteEntity;

/**
 * <b>Implementacion de repositorio para GrupoIngredienteRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class GrupoIngredienteRepositoryImpl extends JPABaseRepository<GrupoIngredienteEntity, Long>
		implements GrupoIngredienteRepository {
	private final GrupoIngredienteMapper grupoIngredienteMapper;

	public GrupoIngredienteRepositoryImpl(EntityManager entityManager, GrupoIngredienteMapper grupoIngredienteMapper) {
		super(GrupoIngredienteEntity.class, entityManager);
		this.grupoIngredienteMapper = grupoIngredienteMapper;
	}

	@Override
	public boolean existePorNombre(String nombre) {
		GrupoIngredienteEntity entity = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.nombre.equalsIgnoreCase(nombre)).fetchFirst();
		return null != entity;
	}

	@Override
	public GrupoIngrediente buscarPorId(Long id) throws EntidadNoEncontradaException {
		GrupoIngredienteEntity entity = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.id.eq(id)).fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException(String.format("No existe el grupo con el id %s", id));

		return grupoIngredienteMapper.entityToDomain(entity);
	}

	@Override
	public GrupoIngrediente buscarPorNombre(String nombre) throws EntidadNoEncontradaException {
		GrupoIngredienteEntity entity = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.nombre.equalsIgnoreCase(nombre)).fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException("No existe el grupo de ingredientes");
		return grupoIngredienteMapper.entityToDomain(entity);
	}

	@Override
	public GrupoIngrediente buscarPorEstado(String estado) throws EntidadNoEncontradaException {
		GrupoIngredienteEntity entity = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.estado.equalsIgnoreCase(estado)).fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException("No existe el grupo de ingredientes");
		return grupoIngredienteMapper.entityToDomain(entity);
	}

	@Override
	public GrupoIngrediente buscarPorNombreyEstado(String nombre, String estado) throws EntidadNoEncontradaException {
		GrupoIngredienteEntity entity = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.nombre.equalsIgnoreCase(nombre)
						.and(grupoIngredienteEntity.estado.equalsIgnoreCase(estado)))
				.fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException(
					String.format("No existe el grupo con el nombre %s y estado %s", nombre, estado));
		return grupoIngredienteMapper.entityToDomain(entity);
	}

	@Override
	public List<GrupoIngrediente> buscarActivos(String estado) {
		List<GrupoIngredienteEntity> entities = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.estado.eq(estado)).orderBy(grupoIngredienteEntity.nombre.desc()).fetch();
		return grupoIngredienteMapper.entitiesToDomains(entities);

	}

	@Override
	public void crear(GrupoIngrediente create) {
		this.save(grupoIngredienteMapper.domainToEntity(create));
	}

	@Override
	public void actualizar(GrupoIngrediente update) throws EntidadNoEncontradaException {
		this.save(grupoIngredienteMapper.domainToEntity(update));
	}
}
