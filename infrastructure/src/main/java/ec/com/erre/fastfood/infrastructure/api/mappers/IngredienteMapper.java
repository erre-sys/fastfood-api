package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.infrastructure.api.entities.IngredienteEntity;
import ec.com.erre.fastfood.share.api.dtos.IngredienteDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {
	IngredienteDto domainToDto(Ingrediente domain);

	Ingrediente dtoToDomain(IngredienteDto dto);

	List<IngredienteDto> domainsToDtos(List<Ingrediente> domain);

	IngredienteEntity domainToEntity(Ingrediente domain);

	Ingrediente entityToDomain(IngredienteEntity entity);

}