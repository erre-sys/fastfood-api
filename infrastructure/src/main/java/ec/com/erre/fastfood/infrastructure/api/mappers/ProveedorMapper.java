package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Proveedor;
import ec.com.erre.fastfood.infrastructure.api.entities.ProveedorEntity;
import ec.com.erre.fastfood.share.api.dtos.ProveedorDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {
	ProveedorDto domainToDto(Proveedor domain);

	Proveedor dtoToDomain(ProveedorDto dto);

	List<ProveedorDto> domainsToDtos(List<Proveedor> domain);

	ProveedorEntity domainToEntity(Proveedor domain);

	Proveedor entityToDomain(ProveedorEntity entity);

}