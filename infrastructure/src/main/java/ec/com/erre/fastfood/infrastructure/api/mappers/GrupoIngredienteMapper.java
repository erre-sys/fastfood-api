package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.infrastructure.api.entities.GrupoIngredienteEntity;
import ec.com.erre.fastfood.share.api.dtos.GrupoIngredienteDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrupoIngredienteMapper {
	GrupoIngredienteDto domainToDto(GrupoIngrediente d);

	GrupoIngrediente dtoToDomain(GrupoIngredienteDto dto);

	List<GrupoIngredienteDto> domainsToDtos(List<GrupoIngrediente> d);

	GrupoIngrediente entityToDomain(GrupoIngredienteEntity e);

	GrupoIngredienteEntity domainToEntity(GrupoIngrediente d);

	List<GrupoIngrediente> entitiesToDomains(List<GrupoIngredienteEntity> e);
}
