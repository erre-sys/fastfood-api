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
import ec.com.erre.fastfood.infrastructure.commons.repositories.CriterioBusqueda;
import ec.com.erre.fastfood.infrastructure.commons.repositories.PagerAndSortDto;
import ec.com.erre.fastfood.infrastructure.commons.repositories.Pagina;
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
	private final GrupoIngredienteMapper grupoIngredienteMapper;

	public GrupoIngredienteRepositoryImpl(EntityManager entityManager, GrupoIngredienteMapper grupoIngredienteMapper) {
		super(GrupoIngredienteEntity.class, entityManager);
		this.grupoIngredienteMapper = grupoIngredienteMapper;
	}

	@Override
	public boolean existePorNombre(String nombre) {
		GrupoIngredienteEntity entity = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.nombre.equalsIgnoreCase(nombre)).fetchFirst();
		return entity == null;
	}

	@Override
	public GrupoIngrediente buscarPorId(Long id) throws EntidadNoEncontradaException {
		GrupoIngredienteEntity entity = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.where(grupoIngredienteEntity.id.eq(id)).fetchFirst();
		if (null == entity)
			throw new EntidadNoEncontradaException(String.format("No existe el grupo con el id %s", id));

		return grupoIngredienteMapper.entityToDomain(entity);
	}

	@Override
	public Pagina<GrupoIngrediente> obtenerGrupoIngredientePaginadoPorFiltros(PagerAndSortDto pager,
			List<CriterioBusqueda> filters) {
		Pageable pageable = PageRequest.of(pager.getPage(), pager.getSize());
		JPQLQuery<GrupoIngrediente> jpqlQuery = getQueryFactory().selectFrom(grupoIngredienteEntity).select(
				Projections.bean(GrupoIngrediente.class, grupoIngredienteEntity.nombre, grupoIngredienteEntity.estado))
				.where(buildQuery(filters));

		if (pager.datosOrdenamientoCompleto()) {
			jpqlQuery.orderBy(buildOrder(pager));
		}

		Page<GrupoIngrediente> pageData = this.findPageData(jpqlQuery, pageable);

		return Pagina.<GrupoIngrediente> builder().paginaActual(pager.getPage()).totalpaginas(pageData.getTotalPages())
				.totalRegistros(pageData.getTotalElements()).contenido(pageData.stream().toList()).build();

	}

	@Override
	public List<GrupoIngrediente> buscarActivos() {
		List<GrupoIngredienteEntity> entities = getQueryFactory().selectFrom(grupoIngredienteEntity)
				.orderBy(grupoIngredienteEntity.nombre.desc()).fetch();
		return grupoIngredienteMapper.entitiesToDomains(entities);

	}

	@Override
	public void crear(GrupoIngrediente create) {
		this.save(grupoIngredienteMapper.domainToEntity(create));
	}

	@Override
	public void actualizar(GrupoIngrediente update) {
		this.save(grupoIngredienteMapper.domainToEntity(update));
	}

	@Override
	public void eliminar(GrupoIngrediente delete) {
		this.delete(grupoIngredienteMapper.domainToEntity(delete));
	}

	/**
	 * Builder query
	 *
	 * @param criterios Query
	 * @return Builder boolean query
	 */
	private Predicate buildQuery(List<CriterioBusqueda> criterios) {
		BooleanBuilder builder = new BooleanBuilder();
		PathBuilder<QGrupoIngredienteEntity> pathBuilder = new PathBuilder<>(QGrupoIngredienteEntity.class,
				"grupoIngredienteEntity");
		criterios.forEach(criterio -> builder.and(getPredicate(criterio.getLlave(), criterio.getOperacion(),
				criterio.getValor(), pathBuilder, GrupoIngredienteEntity.class)));
		return builder;
	}

	private OrderSpecifier<?> buildOrder(PagerAndSortDto paging) {
		PathBuilder<QGrupoIngredienteEntity> pathBuilder = new PathBuilder<>(QGrupoIngredienteEntity.class,
				"grupoIngredienteEntity");
		return getOrderSpecifier(pathBuilder, new CriterioOrden(paging.getOrderBy(), paging.getDirection()),
				GrupoIngredienteEntity.class);
	}

}