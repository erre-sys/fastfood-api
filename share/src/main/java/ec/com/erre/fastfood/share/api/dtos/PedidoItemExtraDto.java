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
public class PedidoItemExtraDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = { Crear.class })
	private Long pedidoItemId;

	@NotNull(groups = { Crear.class })
	private Long ingredienteId;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 14, fraction = 3)
	@DecimalMin(value = "0.001", inclusive = true)
	private BigDecimal cantidad;

	private BigDecimal precioExtra;

	public interface Crear {
	}

	public interface Actualizar {
	}
}