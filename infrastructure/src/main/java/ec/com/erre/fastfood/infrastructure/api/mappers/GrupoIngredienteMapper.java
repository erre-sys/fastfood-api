package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.infrastructure.api.entities.GrupoIngredienteEntity;
import ec.com.erre.fastfood.share.api.dtos.GrupoIngredienteDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrupoIngredienteMapper {
	GrupoIngredienteDto domainToDto(GrupoIngrediente domain);

	GrupoIngrediente dtoToDomain(GrupoIngredienteDto dto);

	List<GrupoIngredienteDto> domainsToDtos(List<GrupoIngrediente> domain);

	GrupoIngredienteEntity domainToEntity(GrupoIngrediente domain);

	GrupoIngrediente entityToDomain(GrupoIngredienteEntity entity);

	List<GrupoIngrediente> entitiesToDomains(List<GrupoIngredienteEntity> entity);
}