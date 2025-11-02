package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoEntity;
import ec.com.erre.fastfood.share.api.dtos.PedidoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

	/* ===== DTO <-> Domain (solo cabecera) ===== */
	PedidoDto domainToDto(Pedido domain);

	Pedido dtoToDomain(PedidoDto dto);

	List<PedidoDto> domainsToDtos(List<Pedido> domain);

	/* ===== Entity <-> Domain (solo cabecera, sin items) ===== */
	@Mapping(target = "items", ignore = true)
	@Mapping(target = "itemsExtras", ignore = true)
	Pedido entityToDomain(PedidoEntity entity);

	PedidoEntity domainToEntity(Pedido domain);
}