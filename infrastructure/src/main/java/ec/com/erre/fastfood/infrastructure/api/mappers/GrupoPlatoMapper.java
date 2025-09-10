package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.GrupoPlato;
import ec.com.erre.fastfood.infrastructure.api.entities.GrupoPlatoEntity;
import ec.com.erre.fastfood.share.api.dtos.GrupoPlatoDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrupoPlatoMapper {
	GrupoPlatoDto domainToDto(GrupoPlato domain);

	GrupoPlato dtoToDomain(GrupoPlatoDto dto);

	List<GrupoPlatoDto> domainsToDtos(List<GrupoPlato> domain);

	GrupoPlatoEntity domainToEntity(GrupoPlato domain);

	GrupoPlato entityToDomain(GrupoPlatoEntity entity);

}