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
class PedidoDto {
    @Null(groups = Crear.class) @NotNull(groups = Actualizar.class)
    private Long id;
    @NotBlank(groups = {Crear.class, Actualizar.class})
    private String estado; // CREADO, EN_COCINA, LISTO, ENTREGADO, CANCELADO
    @NotNull(groups = {Crear.class, Actualizar.class})
    @Digits(integer = 10, fraction = 2)
    private BigDecimal totalBruto;
    @NotNull(groups = {Crear.class, Actualizar.class})
    @Digits(integer = 10, fraction = 2)
    private BigDecimal totalExtras;
    @NotNull(groups = {Crear.class, Actualizar.class})
    @Digits(integer = 10, fraction = 2)
    private BigDecimal totalNeto;
    private String creadoPorSub;
    private String entregadoPorSub;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private List<PedidoItemDto> items;
}

