package ec.com.erre.fastfood.share.api.dtos;

import ec.com.erre.fastfood.share.commons.ValidationGroups;
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
class IngredienteDto {
    @Null(groups = ValidationGroups.Crear.class) @NotNull(groups = ValidationGroups.Actualizar.class)
    private Long id;
    @NotBlank(groups = {ValidationGroups.Crear.class}) @Size(max = 40)
    private String codigo;
    @NotBlank(groups = {ValidationGroups.Crear.class, ValidationGroups.Actualizar.class}) @Size(max = 160)
    private String nombre;
    @NotNull(groups = {ValidationGroups.Crear.class, ValidationGroups.Actualizar.class})
    private Long grupoIngredienteId;
    @NotBlank(groups = {ValidationGroups.Crear.class, ValidationGroups.Actualizar.class}) @Size(max = 16)
    private String unidad; // kg, g, ml, un
    private Boolean esExtra; // default false
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precioExtra; // >0 si esExtra
    @NotNull(groups = {ValidationGroups.Crear.class, ValidationGroups.Actualizar.class})
    @Digits(integer = 11, fraction = 3)
    private BigDecimal stockMinimo;
    private Boolean activo; // default true
}