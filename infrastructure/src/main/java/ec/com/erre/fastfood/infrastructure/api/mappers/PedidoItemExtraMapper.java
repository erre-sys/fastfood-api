package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.PedidoItemExtra;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoItemExtraEntity;
import ec.com.erre.fastfood.share.api.dtos.PedidoItemExtraDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoItemExtraMapper {
	PedidoItemExtraDto domainToDto(PedidoItemExtra domain);

	PedidoItemExtra dtoToDomain(PedidoItemExtraDto dto);

	List<PedidoItemExtraDto> domainsToDtos(List<PedidoItemExtra> domain);

	PedidoItemExtraEntity domainToEntity(PedidoItemExtra domain);

	PedidoItemExtra entityToDomain(PedidoItemExtraEntity entity);

}