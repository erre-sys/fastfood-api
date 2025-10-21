package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.Compra;
import ec.com.erre.fastfood.domain.api.models.api.CompraItem;
import ec.com.erre.fastfood.domain.api.repositories.CompraRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.*;
import ec.com.erre.fastfood.infrastructure.api.mappers.CompraItemMapper;
import ec.com.erre.fastfood.infrastructure.api.mappers.CompraMapper;
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

import static ec.com.erre.fastfood.infrastructure.api.entities.QCompraEntity.compraEntity;
import static ec.com.erre.fastfood.infrastructure.api.entities.QCompraItemEntity.compraItemEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class CompraRepositoryImpl extends JPABaseRepository<CompraEntity, Long> implements CompraRepository {

	private final CompraMapper compraMapper;
	private final CompraItemMapper itemMapper;
	private final EntityManager entityManager;

	public CompraRepositoryImpl(EntityManager entityManager, CompraMapper compraMapper, CompraItemMapper itemMapper) {
		super(CompraEntity.class, entityManager);
		this.compraMapper = compraMapper;
		this.itemMapper = itemMapper;
		this.entityManager = entityManager;
	}

	@Override
	public Long crearCompraConItems(Compra cabecera, List<CompraItem> items) {
		// guardar cabecera
		CompraEntity eCab = save(compraMapper.domainToEntity(cabecera));
		Long compraId = eCab.getId();

		// guardar ítems (el trigger hará inventario/kardex)
		for (CompraItem it : items) {
			CompraItemEntity eIt = itemMapper.domainToEntity(it);
			eIt.setCompraId(compraId);
			entityManager.persist(eIt);
		}
		return compraId;
	}

	@Override
	@Transactional(readOnly = true)
	public Compra buscarPorId(Long id) throws EntidadNoEncontradaException {
		CompraEntity e = getQueryFactory().selectFrom(compraEntity).where(compraEntity.id.eq(id)).fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Compra no existe: " + id);
		return compraMapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CompraItem> listarItems(Long compraId) {
		List<CompraItemEntity> list = getQueryFactory().selectFrom(compraItemEntity)
				.where(compraItemEntity.compraId.eq(compraId)).orderBy(compraItemEntity.id.asc()).fetch();
		return list.stream().map(itemMapper::entityToDomain).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Compra> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());

		Predicate where = buildQuery(filters);

		JPQLQuery<Compra> q = getQueryFactory().select(
				Projections.bean(Compra.class, compraEntity.id.as("id"), compraEntity.proveedor.id.as("proveedorId"),
						compraEntity.fecha.as("fecha"), compraEntity.referencia.as("referencia"),
						compraEntity.observaciones.as("observaciones"), compraEntity.creadoPorSub.as("creadoPorSub")))
				.from(compraEntity).where(where);

		if (pager.datosOrdenamientoCompleto()) {
			q.orderBy(buildOrder(pager));
		} else {
			q.orderBy(compraEntity.fecha.desc());
		}

		Page<Compra> pageData = this.findPageData(q, pageable);

		return Pagina.<Compra> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	/* ===== helpers QueryDSL ===== */

	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QCompraEntity> pb = new PathBuilder<>(QCompraEntity.class, "compraEntity");
		criterios.forEach(
				c -> builder.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, CompraEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QCompraEntity> pb = new PathBuilder<>(QCompraEntity.class, "compraEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()), CompraEntity.class);
	}
}
