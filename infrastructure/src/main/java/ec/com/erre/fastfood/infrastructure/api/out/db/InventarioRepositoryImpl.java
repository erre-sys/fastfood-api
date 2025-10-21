package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.Inventario;
import ec.com.erre.fastfood.domain.api.models.api.InventarioMov;
import ec.com.erre.fastfood.domain.api.repositories.InventarioRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.*;
import ec.com.erre.fastfood.infrastructure.commons.repositories.*;
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

import static ec.com.erre.fastfood.infrastructure.api.entities.QInventarioEntity.inventarioEntity;
import static ec.com.erre.fastfood.infrastructure.api.entities.QIngredienteEntity.ingredienteEntity;
import static ec.com.erre.fastfood.infrastructure.api.entities.QInventarioMovEntity.inventarioMovEntity;

/**
 * <b>Implementacion de repositorio para nventarioRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class InventarioRepositoryImpl extends JPABaseRepository<InventarioEntity, Long>
		implements InventarioRepository {

	public InventarioRepositoryImpl(EntityManager entityManager) {
		super(InventarioEntity.class, entityManager);
	}

	@Override
	public Pagina<Inventario> listarInventario(PagerAndSortDto pager, String q, boolean soloBajoMinimo) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());

		// Alias de Q-types
		QInventarioEntity inv = inventarioEntity;
		QIngredienteEntity ing = ingredienteEntity;

		// Filtros
		BooleanBuilder where = new BooleanBuilder();
		if (q != null && !q.isBlank()) {
			where.and(ing.nombre.containsIgnoreCase(q).or(ing.codigo.containsIgnoreCase(q)));
		}
		if (soloBajoMinimo) {
			where.and(inv.stockActual.lt(ing.stockMinimo));
		}

		// Proyección al Domain (Inventario con campos útiles para UI)
		JPQLQuery<Inventario> query = getQueryFactory()
				.select(Projections.bean(Inventario.class, inv.ingredienteId.as("ingredienteId"),
						ing.codigo.as("codigo"), ing.nombre.as("nombre"), inv.stockActual.as("stockActual"),
						ing.stockMinimo.as("stockMinimo"), inv.actualizadoEn.as("actualizadoEn")))
				.from(inv).join(ing).on(ing.id.eq(inv.ingredienteId)).where(where);

		// Orden por defecto si no envían, para resultados estables
		if (pager.datosOrdenamientoCompleto()) {
			query.orderBy(buildOrder(pager));
		} else {
			query.orderBy(ing.nombre.asc());
		}

		Page<Inventario> pageData = this.findPageData(query, pageable);

		return Pagina.<Inventario> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	@Override
	public Pagina<InventarioMov> listarKardex(Long ingredienteId, LocalDateTime desde, LocalDateTime hasta, String tipo,
			PagerAndSortDto pager) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());

		QInventarioMovEntity mov = inventarioMovEntity;

		BooleanBuilder where = new BooleanBuilder().and(mov.ingrediente.id.eq(ingredienteId));
		if (desde != null)
			where.and(mov.fecha.goe(desde));
		if (hasta != null)
			where.and(mov.fecha.loe(hasta));
		if (tipo != null && !tipo.isBlank())
			where.and(mov.tipo.eq(tipo));

		JPQLQuery<InventarioMov> query = getQueryFactory().select(Projections.bean(InventarioMov.class, mov.id.as("id"),
				mov.ingrediente.id.as("ingredienteId"), mov.fecha.as("fecha"), mov.tipo.as("tipo"),
				mov.cantidad.as("cantidad"), mov.descuentoPct.as("descuentoPct"), mov.referencia.as("referencia"),
				mov.compraItemId.as("compraItemId"), mov.pedidoId.as("pedidoId"))).from(mov).where(where);

		// Orden por defecto: más reciente primero
		if (pager.datosOrdenamientoCompleto()) {
			query.orderBy(buildOrderKardex(pager));
		} else {
			query.orderBy(mov.fecha.desc());
		}

		Page<InventarioMov> pageData = this.findPageData(query, pageable);

		return Pagina.<InventarioMov> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public Inventario obtenerPorIngrediente(Long ingredienteId) throws EntidadNoEncontradaException {
		QInventarioEntity inv = inventarioEntity;
		QIngredienteEntity ing = ingredienteEntity;

		Inventario snap = getQueryFactory()
				.select(Projections.bean(Inventario.class, inv.ingredienteId.as("ingredienteId"),
						ing.codigo.as("codigo"), ing.nombre.as("nombre"), inv.stockActual.as("stockActual"),
						ing.stockMinimo.as("stockMinimo"), inv.actualizadoEn.as("actualizadoEn")))
				.from(inv).join(ing).on(ing.id.eq(inv.ingredienteId)).where(inv.ingredienteId.eq(ingredienteId))
				.fetchFirst();

		if (snap == null)
			throw new EntidadNoEncontradaException("Sin snapshot de inventario para ingrediente: " + ingredienteId);
		return snap;
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obtenerStockActual(Long ingredienteId) {
		BigDecimal v = getQueryFactory().select(inventarioEntity.stockActual).from(inventarioEntity)
				.where(inventarioEntity.ingredienteId.eq(ingredienteId)).fetchFirst();
		return v == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : v.setScale(3, RoundingMode.HALF_UP);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean tieneMovimientos(Long ingredienteId) {
		Integer one = getQueryFactory().selectOne().from(inventarioMovEntity)
				.where(inventarioMovEntity.ingredienteId.eq(ingredienteId)).limit(1).fetchFirst();
		return one != null;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QInventarioEntity> pb = new PathBuilder<>(QInventarioEntity.class, "inventarioEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				InventarioEntity.class);
	}

	private OrderSpecifier<?> buildOrderKardex(PagerAndSortDto paging) {
		PathBuilder<QInventarioMovEntity> pb = new PathBuilder<>(QInventarioMovEntity.class, "inventarioMovEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				InventarioMovEntity.class);
	}
}