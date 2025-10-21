package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.PagoProveedor;
import ec.com.erre.fastfood.domain.api.repositories.PagoProveedorRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.PagoProveedorEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QPagoProveedorEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.PagoProveedorMapper;
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

import static ec.com.erre.fastfood.infrastructure.api.entities.QPagoProveedorEntity.pagoProveedorEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class PagoProveedorRepositoryImpl extends JPABaseRepository<PagoProveedorEntity, Long>
		implements PagoProveedorRepository {

	private final PagoProveedorMapper mapper;

	public PagoProveedorRepositoryImpl(EntityManager em, PagoProveedorMapper mapper) {
		super(PagoProveedorEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public Long crear(PagoProveedor pago) {
		PagoProveedorEntity e = save(mapper.domainToEntity(pago));
		return e.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public PagoProveedor buscarPorId(Long id) throws EntidadNoEncontradaException {
		PagoProveedorEntity e = getQueryFactory().selectFrom(pagoProveedorEntity).where(pagoProveedorEntity.id.eq(id))
				.fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Pago a proveedor no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<PagoProveedor> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		Predicate where = buildQuery(filters);

		JPQLQuery<PagoProveedor> q = getQueryFactory().select(Projections.bean(PagoProveedor.class,
				pagoProveedorEntity.id.as("id"), pagoProveedorEntity.proveedorId.as("proveedorId"),
				pagoProveedorEntity.creadoPorSub.as("creadoPorSub"), pagoProveedorEntity.fecha.as("fecha"),
				pagoProveedorEntity.metodo.as("metodo"), pagoProveedorEntity.referencia.as("referencia"),
				pagoProveedorEntity.montoTotal.as("montoTotal"), pagoProveedorEntity.observaciones.as("observaciones")))
				.from(pagoProveedorEntity).where(where);

		if (pager.datosOrdenamientoCompleto())
			q.orderBy(buildOrder(pager));
		else
			q.orderBy(pagoProveedorEntity.proveedorId.desc());

		Page<PagoProveedor> pageData = this.findPageData(q, pageable);

		return Pagina.<PagoProveedor> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	/* ==== helpers QueryDSL ==== */
	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QPagoProveedorEntity> pb = new PathBuilder<>(QPagoProveedorEntity.class, "pagoProveedorEntity");
		criterios.forEach(c -> builder
				.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, PagoProveedorEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QPagoProveedorEntity> pb = new PathBuilder<>(QPagoProveedorEntity.class, "pagoProveedorEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				PagoProveedorEntity.class);
	}
}
