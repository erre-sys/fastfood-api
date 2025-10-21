package ec.com.erre.fastfood.infrastructure.api.out.db;

import ec.com.erre.fastfood.domain.api.models.api.PedidoItem;
import ec.com.erre.fastfood.domain.api.repositories.PedidoItemRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoItemEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoItemMapper;
import ec.com.erre.fastfood.infrastructure.commons.repositories.JPABaseRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QPedidoItemEntity.pedidoItemEntity;

/**
 * <b>Implementacion de repositorio para PlatoRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class PedidoItemRepositoryImpl extends JPABaseRepository<PedidoItemEntity, Long>
		implements PedidoItemRepository {

	private final PedidoItemMapper mapper;

	public PedidoItemRepositoryImpl(EntityManager em, PedidoItemMapper mapper) {
		super(PedidoItemEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public Long agregar(PedidoItem item) {
		PedidoItemEntity e = save(mapper.domainToEntity(item));
		return e.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PedidoItem> listarPorPedido(Long pedidoId) {
		List<PedidoItemEntity> list = getQueryFactory().selectFrom(pedidoItemEntity)
				.where(pedidoItemEntity.pedido.id.eq(pedidoId)).orderBy(pedidoItemEntity.id.asc()).fetch();
		return list.stream().map(mapper::entityToDomain).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PedidoItem buscarPorId(Long id) throws EntidadNoEncontradaException {
		PedidoItemEntity e = getQueryFactory().selectFrom(pedidoItemEntity).where(pedidoItemEntity.id.eq(id))
				.fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("PedidoItem no existe: " + id);
		return mapper.entityToDomain(e);
	}
}
