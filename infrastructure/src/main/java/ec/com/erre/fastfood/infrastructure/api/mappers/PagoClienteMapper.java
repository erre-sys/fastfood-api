package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.PagoCliente;
import ec.com.erre.fastfood.infrastructure.api.entities.PagoClienteEntity;
import ec.com.erre.fastfood.share.api.dtos.PagoClienteDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoClienteMapper {
	PagoClienteDto domainToDto(PagoCliente d);

	PagoCliente dtoToDomain(PagoClienteDto dto);

	List<PagoClienteDto> domainsToDtos(List<PagoCliente> d);

	PagoCliente entityToDomain(PagoClienteEntity e);

	PagoClienteEntity domainToEntity(PagoCliente d);
}
