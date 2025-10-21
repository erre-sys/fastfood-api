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
public class PedidoItemDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	private Long pedidoId;

	@NotNull(groups = { Crear.class })
	private Long platoId;

	@NotNull(groups = { Crear.class })
	@Digits(integer = 14, fraction = 3)
	@DecimalMin(value = "0.001", inclusive = true)
	private BigDecimal cantidad;

	// Solo lectura: el backend calcula con promo vigente
	private BigDecimal precioUnitario;

	private BigDecimal descuentoPct; // solo lectura si decides calcular del lado backend
	private BigDecimal descuentoMonto; // idem

	private BigDecimal subtotal; // calculado por backend

	public interface Crear {
	}

	public interface Actualizar {
	}
}
