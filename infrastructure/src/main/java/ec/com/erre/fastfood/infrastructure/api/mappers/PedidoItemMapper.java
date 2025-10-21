package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.PedidoItem;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoItemEntity;
import ec.com.erre.fastfood.share.api.dtos.PedidoItemDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoItemMapper {

	PedidoItemDto domainToDto(PedidoItem d);

	PedidoItem dtoToDomain(PedidoItemDto dto);

	List<PedidoItemDto> domainsToDtos(List<PedidoItem> d);

	List<PedidoItem> dtosToDomains(List<PedidoItemDto> dto);

	PedidoItem entityToDomain(PedidoItemEntity e);

	PedidoItemEntity domainToEntity(PedidoItem d);
}
