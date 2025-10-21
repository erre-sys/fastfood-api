package ec.com.erre.fastfood.infrastructure.api.out.db;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.repositories.GrupoIngredienteRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.GrupoIngredienteEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.QGrupoIngredienteEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.GrupoIngredienteMapper;
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

import static ec.com.erre.fastfood.infrastructure.api.entities.QGrupoIngredienteEntity.grupoIngredienteEntity;

/**
 * <b>Implementacion de repositorio para GrupoIngredienteRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class GrupoIngredienteRepositoryImpl extends JPABaseRepository<GrupoIngredienteEntity, Long>
		implements GrupoIngredienteRepository {

	private final GrupoIngredienteMapper mapper;

	public GrupoIngredienteRepositoryImpl(EntityManager em, GrupoIngredienteMapper mapper) {
		super(GrupoIngredienteEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public boolean existePorNombre(String nombre) {
		GrupoIngredienteEntity e = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.nombre.equalsIgnoreCase(nombre)).fetchFirst();
		return e != null;
	}

	@Override
	public void crear(GrupoIngrediente create) {
		save(mapper.domainToEntity(create));
	}

	@Override
	public void actualizar(GrupoIngrediente update) {
		save(mapper.domainToEntity(update));
	}

	@Override
	public void eliminar(GrupoIngrediente delete) {
		delete(mapper.domainToEntity(delete));
	}

	@Override
	@Transactional(readOnly = true)
	public GrupoIngrediente buscarPorId(Long id) throws EntidadNoEncontradaException {
		GrupoIngredienteEntity e = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.id.eq(id)).fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("No existe el grupo con el id " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<GrupoIngrediente> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters) {

		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		Predicate where = buildQuery(filters);

		JPQLQuery<GrupoIngrediente> q = getQueryFactory()
				.select(Projections.bean(GrupoIngrediente.class, grupoIngredienteEntity.id.as("id"),
						grupoIngredienteEntity.nombre.as("nombre"), grupoIngredienteEntity.estado.as("estado"),
						grupoIngredienteEntity.aplicaComida.as("aplicaComida")))
				.from(grupoIngredienteEntity).where(where);

		if (pager.datosOrdenamientoCompleto())
			q.orderBy(buildOrder(pager));
		else
			q.orderBy(grupoIngredienteEntity.nombre.asc());

		Page<GrupoIngrediente> pageData = this.findPageData(q, pageable);

		return Pagina.<GrupoIngrediente> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.getContent()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<GrupoIngrediente> buscarActivos() {
		List<GrupoIngredienteEntity> list = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.estado.eq("A")).orderBy(grupoIngredienteEntity.nombre.asc()).fetch();
		return mapper.entitiesToDomains(list);
	}

	/* ===== helpers QueryDSL ===== */
	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QGrupoIngredienteEntity> pb = new PathBuilder<>(QGrupoIngredienteEntity.class,
				"grupoIngredienteEntity");
		criterios.forEach(c -> builder
				.and(getPredicate(c.getLlave(), c.getOperacion(), c.getValor(), pb, GrupoIngredienteEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QGrupoIngredienteEntity> pb = new PathBuilder<>(QGrupoIngredienteEntity.class,
				"grupoIngredienteEntity");
		return getOrderSpecifier(pb, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				GrupoIngredienteEntity.class);
	}
}
