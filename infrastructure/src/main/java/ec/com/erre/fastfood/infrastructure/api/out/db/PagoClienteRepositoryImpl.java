package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.PagoCliente;
import ec.com.erre.fastfood.domain.api.repositories.PagoClienteRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.PagoClienteEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QPagoClienteEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.PagoClienteMapper;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QPagoClienteEntity.pagoClienteEntity;

/**
 * <b>Implementacion de repositorio para PagoClienteRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class PagoClienteRepositoryImpl extends JPABaseRepository<PagoClienteEntity, Long>
		implements PagoClienteRepository {

	private final PagoClienteMapper mapper;

	public PagoClienteRepositoryImpl(EntityManager em, PagoClienteMapper mapper) {
		super(PagoClienteEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public Long crear(PagoCliente p) {
		PagoClienteEntity e = mapper.domainToEntity(p);
		PagoClienteEntity saved = save(e);
		return saved.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public PagoCliente buscarPorId(Long id) throws EntidadNoEncontradaException {
		PagoClienteEntity e = getQueryFactory().selectFrom(pagoClienteEntity).where(pagoClienteEntity.id.eq(id))
				.fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Pago de cliente no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PagoCliente> listarPorPedido(Long pedidoId) {
		List<PagoClienteEntity> list = getQueryFactory().selectFrom(pagoClienteEntity)
				.where(pagoClienteEntity.pedidoId.eq(pedidoId)).orderBy(pagoClienteEntity.fecha.desc()).fetch();
		return list.stream().map(mapper::entityToDomain).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<PagoCliente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		Predicate where = buildQuery(filters);

		JPQLQuery<PagoCliente> q = getQueryFactory()
				.select(Projections.bean(PagoCliente.class, pagoClienteEntity.id.as("id"),
						pagoClienteEntity.pedidoId.as("pedidoId"), pagoClienteEntity.montoTotal.as("montoTotal"),
						pagoClienteEntity.metodo.as("metodo"), pagoClienteEntity.referencia.as("referencia"),
						pagoClienteEntity.estado.as("estado"), pagoClienteEntity.fecha.as("fecha")))
				.from(pagoClienteEntity).where(where);

		if (pager.datosOrdenamientoCompleto())
			q.orderBy(buildOrder(pager));
		else
			q.orderBy(pagoClienteEntity.fecha.desc());

		Page<PagoCliente> page = this.findPageData(q, pageable);

		return Pagina.<PagoCliente> builder().paginaActual(pager.getPage()).totalpaginas(page.getTotalPages())
				.totalRegistros(page.getTotalElements()).contenido(page.getContent()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal totalPagadoPorPedido(Long pedidoId) {
		BigDecimal sum = getQueryFactory().select(pagoClienteEntity.montoTotal.sum()).from(pagoClienteEntity)
				.where(pagoClienteEntity.pedidoId.eq(pedidoId)).fetchFirst();
		return sum == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : sum.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public boolean actualizarEstado(Long pagoId, String nuevoEstado, LocalDateTime fecha) {
		var updateQuery = getQueryFactory().update(pagoClienteEntity).where(pagoClienteEntity.id.eq(pagoId))
				.set(pagoClienteEntity.estado, nuevoEstado);

		// Si se proporciona fecha, actualizarla también
		if (fecha != null) {
			updateQuery.set(pagoClienteEntity.fecha, fecha);
		}

		long updated = updateQuery.execute();
		return updated > 0;
	}

	/* ==== helpers QueryDSL ==== */
	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QPagoClienteEntity> pb = new PathBuilder<>(QPagoClienteEntity.class, "pagoClienteEntity");
		criterios.forEach(c -> builder
				.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, PagoClienteEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QPagoClienteEntity> pb = new PathBuilder<>(QPagoClienteEntity.class, "pagoClienteEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				PagoClienteEntity.class);
	}
}
