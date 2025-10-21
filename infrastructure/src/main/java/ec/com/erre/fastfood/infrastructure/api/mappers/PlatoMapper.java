package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Plato;
import ec.com.erre.fastfood.infrastructure.api.entities.PlatoEntity;
import ec.com.erre.fastfood.share.api.dtos.PlatoDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlatoMapper {
	PlatoDto domainToDto(Plato domain);

	Plato dtoToDomain(PlatoDto dto);

	List<PlatoDto> domainsToDtos(List<Plato> domain);

	Plato entityToDomain(PlatoEntity entity);

	PlatoEntity domainToEntity(Plato domain);
}
