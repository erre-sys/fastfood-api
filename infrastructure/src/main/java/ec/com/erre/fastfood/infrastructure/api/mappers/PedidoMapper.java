package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Pedido;
import ec.com.erre.fastfood.infrastructure.api.entities.PedidoEntity;
import ec.com.erre.fastfood.share.api.dtos.PedidoDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {
	PedidoDto domainToDto(Pedido domain);

	Pedido dtoToDomain(PedidoDto dto);

	List<PedidoDto> domainsToDtos(List<Pedido> domain);

	PedidoEntity domainToEntity(Pedido domain);

	Pedido entityToDomain(PedidoEntity entity);

}