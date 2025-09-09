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
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CompraDto {
    @Null(groups = Crear.class) @NotNull(groups = Actualizar.class)
    private Long id;
    @NotNull(groups = {Crear.class, Actualizar.class})
    private Long proveedorId;
    private String referencia;
    @Size(max = 500)
    private String observaciones;
    private String creadoPorSub;
    private LocalDateTime fecha;
    private List<CompraItemDto> items; // opcional
}