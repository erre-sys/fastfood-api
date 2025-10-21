package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.CompraItem;
import ec.com.erre.fastfood.infrastructure.api.entities.CompraItemEntity;

import ec.com.erre.fastfood.share.api.dtos.CompraItemDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompraItemMapper {

	CompraItemDto domainToDto(CompraItem d);

	CompraItem dtoToDomain(CompraItemDto dto);

	List<CompraItemDto> domainsToDtos(List<CompraItem> d);

	List<CompraItem> dtosToDomains(List<CompraItemDto> dto);

	CompraItem entityToDomain(CompraItemEntity e);

	CompraItemEntity domainToEntity(CompraItem d);
}
