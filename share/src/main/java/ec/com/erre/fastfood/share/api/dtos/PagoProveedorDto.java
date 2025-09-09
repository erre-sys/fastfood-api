package ec.com.erre.fastfood.share.api.dtos;
import ec.com.erre.fastfood.share.commons.ValidationGroups.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PagoProveedorDto {
    @Null(groups = Crear.class) @NotNull(groups = Actualizar.class)
    private Long id;
    @NotNull(groups = {Crear.class, Actualizar.class})
    private Long proveedorId;
    @NotNull(groups = {Crear.class, Actualizar.class})
    @Digits(integer = 10, fraction = 2)
    private BigDecimal montoTotal;
    @NotBlank(groups = {Crear.class, Actualizar.class})
    private String metodo; // EFECTIVO, TARJETA, TRANSFERENCIA, OTRO
    private String referencia;
    private String observaciones;
    private String creadoPorSub;
    private LocalDateTime fecha;
}