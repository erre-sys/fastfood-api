package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.PagoProveedor;
import ec.com.erre.fastfood.infrastructure.api.entities.PagoProveedorEntity;
import ec.com.erre.fastfood.share.api.dtos.PagoProveedorDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoProveedorMapper {

	PagoProveedorDto domainToDto(PagoProveedor d);

	PagoProveedor dtoToDomain(PagoProveedorDto dto);

	List<PagoProveedorDto> domainsToDtos(List<PagoProveedor> d);

	PagoProveedor entityToDomain(PagoProveedorEntity e);

	PagoProveedorEntity domainToEntity(PagoProveedor d);
}
