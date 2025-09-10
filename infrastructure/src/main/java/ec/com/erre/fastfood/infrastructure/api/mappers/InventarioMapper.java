package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Inventario;
import ec.com.erre.fastfood.infrastructure.api.entities.InventarioEntity;
import ec.com.erre.fastfood.share.api.dtos.InventarioDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {
	InventarioDto domainToDto(Inventario domain);

	Inventario dtoToDomain(InventarioDto dto);

	List<InventarioDto> domainsToDtos(List<Inventario> domain);

	InventarioEntity domainToEntity(Inventario domain);

	Inventario entityToDomain(InventarioEntity entity);

}