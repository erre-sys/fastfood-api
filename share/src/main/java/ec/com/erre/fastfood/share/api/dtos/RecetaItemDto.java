package ec.com.erre.fastfood.share.api.dtos;

import ec.com.erre.fastfood.share.commons.ValidationGroups.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RecetaItemDto {
    @NotNull(groups = {Crear.class, Actualizar.class})
    private Long ingredienteId;
    @NotNull(groups = {Crear.class, Actualizar.class})
    @Digits(integer = 12, fraction = 3)
    private BigDecimal cantidad; // por 1 plato
}