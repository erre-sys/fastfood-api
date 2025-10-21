package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.domain.api.repositories.PromoProgramadaRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.*;
import ec.com.erre.fastfood.infrastructure.api.mappers.PromoProgramadaMapper;
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

import static ec.com.erre.fastfood.infrastructure.api.entities.QPromoProgramadaEntity.promoProgramadaEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class PromoProgramadaRepositoryImpl extends JPABaseRepository<PromoProgramadaEntity, Long>
		implements PromoProgramadaRepository {

	private final PromoProgramadaMapper mapper;

	public PromoProgramadaRepositoryImpl(EntityManager em, PromoProgramadaMapper mapper) {
		super(PromoProgramadaEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public Long crear(PromoProgramada p) {
		PromoProgramadaEntity e = save(mapper.domainToEntity(p));
		return e.getId();
	}

	@Override
	public void actualizar(PromoProgramada p) {
		save(mapper.domainToEntity(p));
	}

	@Override
	public void eliminar(Long id) {
		getQueryFactory().delete(promoProgramadaEntity).where(promoProgramadaEntity.id.eq(id)).execute();
	}

	@Override
	@Transactional(readOnly = true)
	public PromoProgramada buscarPorId(Long id) throws EntidadNoEncontradaException {
		PromoProgramadaEntity e = getQueryFactory().selectFrom(promoProgramadaEntity)
				.where(promoProgramadaEntity.id.eq(id)).fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Promo no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PromoProgramada> listarPorPlato(Long platoId) {
		List<PromoProgramadaEntity> list = getQueryFactory().selectFrom(promoProgramadaEntity)
				.where(promoProgramadaEntity.platoId.eq(platoId)).orderBy(promoProgramadaEntity.fechaInicio.desc())
				.fetch();
		return list.stream().map(mapper::entityToDomain).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<PromoProgramada> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		Predicate where = buildQuery(filters);

		JPQLQuery<PromoProgramada> q = getQueryFactory().select(Projections.bean(PromoProgramada.class,
				promoProgramadaEntity.id.as("id"), promoProgramadaEntity.platoId.as("platoId"),
				promoProgramadaEntity.fechaInicio.as("fechaInicio"), promoProgramadaEntity.fechaFin.as("fechaFin"),
				promoProgramadaEntity.descuentoPct.as("descuentoPct"), promoProgramadaEntity.estado.as("estado"),
				promoProgramadaEntity.creadoPorSub.as("creadoPorSub"))).from(promoProgramadaEntity).where(where);

		if (pager.datosOrdenamientoCompleto())
			q.orderBy(buildOrder(pager));
		else
			q.orderBy(promoProgramadaEntity.fechaInicio.desc());

		Page<PromoProgramada> pageData = this.findPageData(q, pageable);

		return Pagina.<PromoProgramada> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	/* ==== helpers QueryDSL ==== */
	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QPromoProgramadaEntity> pb = new PathBuilder<>(QPromoProgramadaEntity.class,
				"promoProgramadaEntity");
		criterios.forEach(c -> builder
				.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, PromoProgramadaEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QPromoProgramadaEntity> pb = new PathBuilder<>(QPromoProgramadaEntity.class,
				"promoProgramadaEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				PromoProgramadaEntity.class);
	}
}
