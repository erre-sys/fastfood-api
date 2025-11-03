package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.domain.api.repositories.PedidoRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QPedidoEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoMapper;
import ec.com.erre.fastfood.share.commons.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioOrden;
import ec.com.erre.fastfood.infrastructure.commons.repositories.JPABaseRepository;
import ec.com.erre.fastfood.share.commons.PagerAndSortDto;
import ec.com.erre.fastfood.share.commons.Pagina;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QPedidoEntity.pedidoEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class PedidoRepositoryImpl extends JPABaseRepository<PedidoEntity, Long> implements PedidoRepository {

	private final PedidoMapper mapper;

	public PedidoRepositoryImpl(EntityManager em, PedidoMapper mapper) {
		super(PedidoEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	@Transactional(readOnly = true)
	public Pedido buscarPorId(Long id) throws EntidadNoEncontradaException {
		PedidoEntity e = getQueryFactory().selectFrom(pedidoEntity).where(pedidoEntity.id.eq(id)).fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Pedido no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	public void actualizarTotales(Long pedidoId, BigDecimal totalBruto, BigDecimal totalDescuentos,
			BigDecimal totalExtras, BigDecimal totalNeto) {
		getQueryFactory().update(pedidoEntity).where(pedidoEntity.id.eq(pedidoId))
				.set(pedidoEntity.totalBruto, totalBruto).set(pedidoEntity.totalDescuentos, totalDescuentos)
				.set(pedidoEntity.totalExtras, totalExtras).set(pedidoEntity.totalNeto, totalNeto)
				.set(pedidoEntity.actualizadoEn, java.time.LocalDateTime.now()).execute();
	}

	@Override
	public Long crear(Pedido ped) {
		PedidoEntity e = save(mapper.domainToEntity(ped));
		return e.getId();
	}

	@Override
	public boolean cambiarEstadoSimple(Long pedidoId, String nuevoEstado) {
		long upd = getQueryFactory().update(pedidoEntity)
				.where(pedidoEntity.id.eq(pedidoId), pedidoEntity.estado.in("C", "L"))
				.set(pedidoEntity.estado, nuevoEstado).set(pedidoEntity.actualizadoEn, java.time.LocalDateTime.now())
				.execute();
		return upd > 0;
	}

	@Override
	public boolean anularSiProcede(Long pedidoId) {
		long upd = getQueryFactory().update(pedidoEntity)
				.where(pedidoEntity.id.eq(pedidoId), pedidoEntity.estado.notIn("E", "A")) // E=Entregado, A=Anulado
				.set(pedidoEntity.estado, "A").set(pedidoEntity.actualizadoEn, java.time.LocalDateTime.now()).execute();
		return upd > 0;
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Pedido> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());

		Predicate where = buildQuery(filters);

		JPQLQuery<Pedido> q = getQueryFactory()
				.select(Projections.bean(Pedido.class, pedidoEntity.id.as("id"), pedidoEntity.estado.as("estado"),
						pedidoEntity.totalBruto.as("totalBruto"), pedidoEntity.totalDescuentos.as("totalDescuentos"),
						pedidoEntity.totalExtras.as("totalExtras"), pedidoEntity.totalNeto.as("totalNeto"),
						pedidoEntity.observaciones.as("observaciones"),
						pedidoEntity.entregadoPorSub.as("entregadoPorSub"), pedidoEntity.creadoEn.as("creadoEn"),
						pedidoEntity.actualizadoEn.as("actualizadoEn"), pedidoEntity.entregadoEn.as("entregadoEn")))
				.from(pedidoEntity).where(where);

		if (pager.datosOrdenamientoCompleto()) {
			q.orderBy(buildOrder(pager));
		} else {
			q.orderBy(pedidoEntity.creadoEn.desc());
		}

		Page<Pedido> pageData = this.findPageData(q, pageable);

		return Pagina.<Pedido> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	/* ==== helpers QueryDSL ==== */

	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QPedidoEntity> pb = new PathBuilder<>(QPedidoEntity.class, "pedidoEntity");
		criterios.forEach(
				c -> builder.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, PedidoEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QPedidoEntity> pb = new PathBuilder<>(QPedidoEntity.class, "pedidoEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()), PedidoEntity.class);
	}
}
