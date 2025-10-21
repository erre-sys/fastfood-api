package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.PromoProgramada;
import ec.com.erre.fastfood.infrastructure.api.entities.PromoProgramadaEntity;
import ec.com.erre.fastfood.share.api.dtos.PromoProgramadaDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromoProgramadaMapper {
	PromoProgramadaDto domainToDto(PromoProgramada d);

	PromoProgramada dtoToDomain(PromoProgramadaDto dto);

	List<PromoProgramadaDto> domainsToDtos(List<PromoProgramada> d);

	PromoProgramada entityToDomain(PromoProgramadaEntity e);

	PromoProgramadaEntity domainToEntity(PromoProgramada d);
}
