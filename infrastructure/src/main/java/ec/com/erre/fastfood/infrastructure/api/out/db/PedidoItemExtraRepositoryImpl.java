package ec.com.erre.fastfood.infrastructure.api.out.db;

import ec.com.erre.fastfood.domain.api.models.api.PedidoItemExtra;
import ec.com.erre.fastfood.domain.api.repositories.PedidoItemExtraRepository;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoItemExtraEntity;
import ec.com.erre.fastfood.infrastructure.api.mappers.PedidoItemExtraMapper;
import ec.com.erre.fastfood.infrastructure.commons.repositories.*;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static ec.com.erre.fastfood.infrastructure.api.entities.QPedidoItemEntity.pedidoItemEntity;
import static ec.com.erre.fastfood.infrastructure.api.entities.QPedidoItemExtraEntity.pedidoItemExtraEntity;

/**
 * <b>Implementacion de repositorio para GrupoIngredienteRepositoryImpl </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
@Repository
@Transactional
public class PedidoItemExtraRepositoryImpl extends JPABaseRepository<PedidoItemExtraEntity, Long>
		implements PedidoItemExtraRepository {

	private final PedidoItemExtraMapper mapper;

	public PedidoItemExtraRepositoryImpl(EntityManager em, PedidoItemExtraMapper mapper) {
		super(PedidoItemExtraEntity.class, em);
		this.mapper = mapper;
	}

	@Override
	public Long agregar(PedidoItemExtra e) {
		PedidoItemExtraEntity pe = save(mapper.domainToEntity(e));
		return pe.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public PedidoItemExtra buscarPorId(Long id) throws EntidadNoEncontradaException {
		PedidoItemExtraEntity e = getQueryFactory().selectFrom(pedidoItemExtraEntity)
				.where(pedidoItemExtraEntity.id.eq(id)).fetchFirst();
		if (e == null)
			throw new EntidadNoEncontradaException("Extra no existe: " + id);
		return mapper.entityToDomain(e);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PedidoItemExtra> listarPorItem(Long pedidoItemId) {
		List<PedidoItemExtraEntity> list = getQueryFactory().selectFrom(pedidoItemExtraEntity)
				.where(pedidoItemExtraEntity.pedidoItemId.eq(pedidoItemId)).orderBy(pedidoItemExtraEntity.id.asc())
				.fetch();
		return list.stream().map(mapper::entityToDomain).toList();
	}

	@Override
	public void eliminar(Long extraId) {
		getQueryFactory().delete(pedidoItemExtraEntity).where(pedidoItemExtraEntity.id.eq(extraId)).execute();
	}

	@Override
	public void actualizarCantidadYTotal(Long extraId, BigDecimal nuevaCantidad, BigDecimal nuevoTotal) {
		getQueryFactory().update(pedidoItemExtraEntity).set(pedidoItemExtraEntity.cantidad, nuevaCantidad)
				.set(pedidoItemExtraEntity.precioExtra, nuevoTotal).where(pedidoItemExtraEntity.id.eq(extraId))
				.execute();
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal totalExtrasDePedido(Long pedidoId) {
		// join extras -> items para filtrar por pedido
		BigDecimal sum = getQueryFactory().select(pedidoItemExtraEntity.precioExtra.sum()).from(pedidoItemExtraEntity)
				.join(pedidoItemEntity).on(pedidoItemEntity.id.eq(pedidoItemExtraEntity.pedidoItemId))
				.where(pedidoItemEntity.pedidoId.eq(pedidoId)).fetchFirst();
		return sum == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : sum.setScale(2, RoundingMode.HALF_UP);
	}
}
