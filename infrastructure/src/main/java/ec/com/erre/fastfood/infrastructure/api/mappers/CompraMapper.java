package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Compra;
import ec.com.erre.fastfood.infrastructure.api.entities.CompraEntity;
import ec.com.erre.fastfood.share.api.dtos.CompraDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompraMapper {
	CompraDto domainToDto(Compra domain);

	Compra dtoToDomain(CompraDto dto);

	List<CompraDto> domainsToDtos(List<Compra> domain);

	CompraEntity domainToEntity(Compra domain);

	Compra entityToDomain(CompraEntity entity);

}