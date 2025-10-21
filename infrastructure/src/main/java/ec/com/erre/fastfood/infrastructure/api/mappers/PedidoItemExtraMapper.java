package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.PedidoItemExtra;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoItemExtraEntity;
import ec.com.erre.fastfood.share.api.dtos.PedidoItemExtraDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoItemExtraMapper {
	PedidoItemExtraDto domainToDto(PedidoItemExtra d);

	PedidoItemExtra dtoToDomain(PedidoItemExtraDto dto);

	List<PedidoItemExtraDto> domainsToDtos(List<PedidoItemExtra> d);

	PedidoItemExtra entityToDomain(PedidoItemExtraEntity e);

	PedidoItemExtraEntity domainToEntity(PedidoItemExtra d);
}
