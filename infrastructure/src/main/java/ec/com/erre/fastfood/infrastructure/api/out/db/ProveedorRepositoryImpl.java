package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.Proveedor;
import ec.com.erre.fastfood.domain.api.repositories.ProveedorRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.ProveedorEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QProveedorEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.ProveedorMapper;
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

import static ec.com.erre.fastfood.infrastructure.api.entities.QProveedorEntity.proveedorEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class ProveedorRepositoryImpl extends JPABaseRepository<ProveedorEntity, Long> implements ProveedorRepository {

	private final ProveedorMapper mapper;

	public ProveedorRepositoryImpl(EntityManager entityManager, ProveedorMapper mapper) {
		super(ProveedorEntity.class, entityManager);
		this.mapper = mapper;
	}

	@Override
	public boolean existePorNombre(String nombre) {
		ProveedorEntity e = getQueryFactory().selectFrom(proveedorEntity)
				.where(proveedorEntity.nombre.equalsIgnoreCase(nombre)).fetchFirst();
		return e != null;
	}

	@Override
	public boolean existePorRuc(String ruc) {
		if (ruc == null || ruc.isBlank())
			return false;
		ProveedorEntity e = getQueryFactory().selectFrom(proveedorEntity)
				.where(proveedorEntity.ruc.equalsIgnoreCase(ruc)).fetchFirst();
		return e != null;
	}

	@Override
	public void crear(Proveedor create) {
		save(mapper.domainToEntity(create));
	}

	@Override
	public void actualizar(Proveedor update) {
		save(mapper.domainToEntity(update));
	}

	@Override
	public void eliminar(Proveedor delete) {
		delete(mapper.domainToEntity(delete));
	}

	@Override
	@Transactional(readOnly = true)
	public Proveedor buscarPorId(Long id) throws EntidadNoEncontradaException {
		ProveedorEntity e = getQueryFactory().selectFrom(proveedorEntity).where(proveedorEntity.id.eq(id)).fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Proveedor no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Proveedor> listarTodos() {
		List<ProveedorEntity> list = getQueryFactory().selectFrom(proveedorEntity).orderBy(proveedorEntity.nombre.asc())
				.fetch();
		return mapper.entitiesToDomains(list);
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Proveedor> paginadoPorFiltros(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());

		Predicate where = buildQuery(filters);

		JPQLQuery<Proveedor> q = getQueryFactory().select(
				Projections.bean(Proveedor.class, proveedorEntity.id.as("id"), proveedorEntity.nombre.as("nombre"),
						proveedorEntity.ruc.as("ruc"), proveedorEntity.telefono.as("telefono"),
						proveedorEntity.email.as("email"), proveedorEntity.estado.as("estado")))
				.from(proveedorEntity).where(where);

		if (pager.datosOrdenamientoCompleto()) {
			q.orderBy(buildOrder(pager));
		} else {
			q.orderBy(proveedorEntity.nombre.asc());
		}

		Page<Proveedor> pageData = this.findPageData(q, pageable);

		return Pagina.<Proveedor> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	/* ==== helpers QueryDSL ==== */

	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QProveedorEntity> pb = new PathBuilder<>(QProveedorEntity.class, "proveedorEntity");
		criterios.forEach(c -> builder
				.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, ProveedorEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QProveedorEntity> pb = new PathBuilder<>(QProveedorEntity.class, "proveedorEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				ProveedorEntity.class);
	}
}
