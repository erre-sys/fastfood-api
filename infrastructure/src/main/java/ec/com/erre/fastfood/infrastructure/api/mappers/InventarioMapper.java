package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.Inventario;
import ec.com.erre.fastfood.domain.api.models.api.InventarioMov;
import ec.com.erre.fastfood.infrastructure.api.entities.InventarioEntity;
import ec.com.erre.fastfood.infrastructure.api.entities.InventarioMovEntity;
import ec.com.erre.fastfood.share.api.dtos.InventarioDto;
import ec.com.erre.fastfood.share.api.dtos.InventarioMovDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

	Inventario entityToDomain(InventarioEntity entity);

	InventarioDto domaintoDto(Inventario domain);

	List<Inventario> entitiesToDomains(List<InventarioEntity> entity);

}