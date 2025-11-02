package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.GrupoPlato;
import ec.com.erre.fastfood.domain.api.repositories.GrupoPlatoRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.GrupoPlatoEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QGrupoPlatoEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.GrupoPlatoMapper;
import ec.com.erre.fastfood.infrastructure.commons.repositories.*;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QGrupoPlatoEntity.grupoPlatoEntity;

/**
 * <b>Implementacion de repositorio para GrupoPlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class GrupoPlatoRepositoryImpl extends JPABaseRepository<GrupoPlatoEntity, Long>
		implements GrupoPlatoRepository {
	private final GrupoPlatoMapper grupoPlatoMapper;

	public GrupoPlatoRepositoryImpl(EntityManager entityManager, GrupoPlatoMapper grupoPlatoMapper) {
		super(GrupoPlatoEntity.class, entityManager);
		this.grupoPlatoMapper = grupoPlatoMapper;
	}

	@Override
	public boolean existePorNombre(String nombre) {
		GrupoPlatoEntity entity = getQueryFactory().selectFrom(grupoPlatoEntity)
				.where(grupoPlatoEntity.nombre.equalsIgnoreCase(nombre)).fetchFirst();
		return null != entity;
	}

	@Override
	public GrupoPlato buscarPorId(Long id) throws EntidadNoEncontradaException {
		GrupoPlatoEntity entity = getQueryFactory().selectFrom(grupoPlatoEntity).where(grupoPlatoEntity.id.eq(id))
				.fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException(String.format("No existe el grupo con el id %s", id));

		return grupoPlatoMapper.entityToDomain(entity);
	}

	@Override
	public List<GrupoPlato> buscarActivos() {
		List<GrupoPlatoEntity> entities = getQueryFactory().selectFrom(grupoPlatoEntity)
				.orderBy(grupoPlatoEntity.nombre.desc()).fetch();
		return grupoPlatoMapper.entitiesToDomains(entities);

	}

	@Override
	public void crear(GrupoPlato create) {
		this.save(grupoPlatoMapper.domainToEntity(create));
	}

	@Override
	public void actualizar(GrupoPlato update) {
		this.save(grupoPlatoMapper.domainToEntity(update));
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<GrupoPlato> obtenerGrupoPlatoPaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters) {

		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		Predicate where = buildQuery(filters);

		JPQLQuery<GrupoPlato> q = getQueryFactory()
				.select(Projections.bean(GrupoPlato.class, grupoPlatoEntity.id.as("id"),
						grupoPlatoEntity.nombre.as("nombre"), grupoPlatoEntity.estado.as("estado")))
				.from(grupoPlatoEntity).where(where);

		if (pager.datosOrdenamientoCompleto())
			q.orderBy(buildOrder(pager));
		else
			q.orderBy(grupoPlatoEntity.nombre.asc());

		Page<GrupoPlato> pageData = this.findPageData(q, pageable);

		return Pagina.<GrupoPlato> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	/* ===== helpers QueryDSL ===== */
	/**
	 * Builder query
	 *
	 * @param criterios Query
	 * @return Builder boolean query
	 */
	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QGrupoPlatoEntity> pb = new PathBuilder<>(QGrupoPlatoEntity.class, "grupoPlatoEntity");
		criterios.forEach(c -> builder
				.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, GrupoPlatoEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QGrupoPlatoEntity> pb = new PathBuilder<>(QGrupoPlatoEntity.class, "grupoPlatoEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				GrupoPlatoEntity.class);
	}

}