package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.RecetaItem;
import ec.com.erre.fastfood.infrastructure.api.entities.RecetaItemEntity;
import ec.com.erre.fastfood.share.api.dtos.RecetaItemDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecetaItemMapper {

	// DTO ⇄ Domain
	RecetaItemDto domainToDto(RecetaItem d);

	RecetaItem dtoToDomain(RecetaItemDto dto);

	List<RecetaItemDto> domainsToDtos(List<RecetaItem> d);

	List<RecetaItem> dtosToDomains(List<RecetaItemDto> dto);

	// Entity ⇄ Domain
	RecetaItem entityToDomain(RecetaItemEntity e);

	RecetaItemEntity domainToEntity(RecetaItem d);
}
