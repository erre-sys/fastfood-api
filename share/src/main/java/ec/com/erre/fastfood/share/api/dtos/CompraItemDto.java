package ec.com.erre.fastfood.share.api.dtos;

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
public class CompraItemDto {
	@Null(groups = CompraDto.Crear.class)
	@NotNull(groups = CompraDto.Actualizar.class)
	private Long id;

	@Null(groups = CompraDto.Crear.class)
	private Long compraId;

	@NotNull(groups = { CompraDto.Crear.class })
	private Long ingredienteId;

	@NotNull(groups = { CompraDto.Crear.class })
	@Digits(integer = 14, fraction = 3)
	@DecimalMin(value = "0.001", inclusive = true)
	private BigDecimal cantidad;

	@NotNull(groups = { CompraDto.Crear.class })
	@Digits(integer = 14, fraction = 2)
	@DecimalMin(value = "0.00", inclusive = true)
	private BigDecimal costoUnitario;
}