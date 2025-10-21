package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.api.repositories.IngredienteRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.IngredienteEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QIngredienteEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.IngredienteMapper;
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

import static ec.com.erre.fastfood.infrastructure.api.entities.QIngredienteEntity.ingredienteEntity;

/**
 * <b>Implementacion de repositorio para IngredienteRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class IngredienteRepositoryImpl extends JPABaseRepository<IngredienteEntity, Long>
		implements IngredienteRepository {

	private final IngredienteMapper mapper;

	public IngredienteRepositoryImpl(EntityManager em, IngredienteMapper mapper) {
		super(IngredienteEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public boolean existePorCodigo(String codigo) {
		IngredienteEntity e = getQueryFactory().selectFrom(ingredienteEntity)
				.where(ingredienteEntity.codigo.equalsIgnoreCase(codigo)).fetchFirst();
		return e != null;
	}

	@Override
	public void crear(Ingrediente i) {
		save(mapper.domainToEntity(i));
	}

	@Override
	public void actualizar(Ingrediente i) {
		save(mapper.domainToEntity(i));
	}

	@Override
	public void eliminar(Ingrediente i) {
		delete(mapper.domainToEntity(i));
	}

	@Override
	@Transactional(readOnly = true)
	public Ingrediente buscarPorId(Long id) throws EntidadNoEncontradaException {
		IngredienteEntity e = getQueryFactory().selectFrom(ingredienteEntity).where(ingredienteEntity.id.eq(id))
				.fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Ingrediente no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Ingrediente> activos() {
		List<IngredienteEntity> list = getQueryFactory().selectFrom(ingredienteEntity)
				.where(ingredienteEntity.estado.eq("A")).orderBy(ingredienteEntity.nombre.asc()).fetch();
		return list.stream().map(mapper::entityToDomain).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Ingrediente> paginado(PagerAndSortDto pager, List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		Predicate where = buildQuery(filters);

		JPQLQuery<Ingrediente> q = getQueryFactory().select(Projections.bean(Ingrediente.class,
				ingredienteEntity.id.as("id"), ingredienteEntity.grupoIngredienteId.as("grupoIngredienteId"),
				ingredienteEntity.codigo.as("codigo"), ingredienteEntity.nombre.as("nombre"),
				ingredienteEntity.unidad.as("unidad"), ingredienteEntity.esExtra.as("esExtra"),
				ingredienteEntity.precioExtra.as("precioExtra"), ingredienteEntity.stockMinimo.as("stockMinimo"),
				ingredienteEntity.aplicaComida.as("aplicaComida"), ingredienteEntity.estado.as("estado")))
				.from(ingredienteEntity).where(where);

		if (pager.datosOrdenamientoCompleto())
			q.orderBy(buildOrder(pager));
		else
			q.orderBy(ingredienteEntity.nombre.asc());

		Page<Ingrediente> pageData = this.findPageData(q, pageable);

		return Pagina.<Ingrediente> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QIngredienteEntity> pb = new PathBuilder<>(QIngredienteEntity.class, "ingredienteEntity");
		criterios.forEach(c -> builder
				.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, IngredienteEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QIngredienteEntity> pb = new PathBuilder<>(QIngredienteEntity.class, "ingredienteEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				IngredienteEntity.class);
	}
}
