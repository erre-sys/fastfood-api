package ec.com.erre.fastfood.infrastructure.api.mappers;

import ec.com.erre.fastfood.domain.api.models.api.InventarioMov;
import ec.com.erre.fastfood.infrastructure.api.entities.InventarioMovEntity;
import ec.com.erre.fastfood.share.api.dtos.InventarioMovDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KardexMapper {
	InventarioMovDto entityToDomain(InventarioMovEntity entity);

	InventarioMovDto domaintoDto(InventarioMov domain);

	List<InventarioMov> entitiesToDomains(List<InventarioMovEntity> entity);
}