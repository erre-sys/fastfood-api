package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.domain.api.repositories.PlatoRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.PlatoEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QPlatoEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.PlatoMapper;
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

	private final PlatoMapper mapper;

	public PlatoRepositoryImpl(EntityManager em, PlatoMapper mapper) {
		super(PlatoEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public boolean existePorCodigo(String codigo) {
		PlatoEntity e = getQueryFactory().selectFrom(platoEntity).where(platoEntity.codigo.equalsIgnoreCase(codigo))
				.fetchFirst();
		return e != null;
	}

	@Override
	public void crear(Plato p) {
		save(mapper.domainToEntity(p));
	}

	@Override
	public void actualizar(Plato p) {
		save(mapper.domainToEntity(p));
	}

	@Override
	public void eliminar(Plato p) {
		delete(mapper.domainToEntity(p));
	}

	@Override
	@Transactional(readOnly = true)
	public Plato buscarPorId(Long id) throws EntidadNoEncontradaException {
		PlatoEntity e = getQueryFactory().selectFrom(platoEntity).where(platoEntity.id.eq(id)).fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Plato no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Plato> activos() {
		List<PlatoEntity> list = getQueryFactory().selectFrom(platoEntity).where(platoEntity.estado.eq("A"))
				.orderBy(platoEntity.nombre.asc()).fetch();
		return list.stream().map(mapper::entityToDomain).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Plato> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		Predicate where = buildQuery(filters);

		JPQLQuery<Plato> q = getQueryFactory()
				.select(Projections.bean(Plato.class, platoEntity.id.as("id"), platoEntity.codigo.as("codigo"),
						platoEntity.nombre.as("nombre"), platoEntity.grupoPlatoId.as("grupoPlatoId"),
						platoEntity.precioBase.as("precioBase"), platoEntity.estado.as("estado"),
						platoEntity.enPromocion.as("enPromocion"), platoEntity.descuentoPct.as("descuentoPct")))
				.from(platoEntity).where(where);

		if (pager.datosOrdenamientoCompleto())
			q.orderBy(buildOrder(pager));
		else
			q.orderBy(platoEntity.nombre.asc());

		Page<Plato> pageData = this.findPageData(q, pageable);

		return Pagina.<Plato> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	/* ==== helpers QueryDSL ==== */
	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QPlatoEntity> pb = new PathBuilder<>(QPlatoEntity.class, "platoEntity");
		criterios.forEach(
				c -> builder.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, PlatoEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QPlatoEntity> pb = new PathBuilder<>(QPlatoEntity.class, "platoEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()), PlatoEntity.class);
	}
}
