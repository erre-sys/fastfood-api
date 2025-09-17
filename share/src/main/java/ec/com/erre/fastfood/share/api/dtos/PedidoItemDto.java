package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItemDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = Crear.class)
	private Long platoId;
	@NotNull(groups = Crear.class)
	@Min(1)
	private Integer cantidad;

	@NotNull(groups = Crear.class)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal precioUnitario;

	@NotNull(groups = Crear.class)
	@Digits(integer = 3, fraction = 2)
	private BigDecimal descuentoPct;

	@NotNull(groups = Crear.class)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal descuentoMonto;

	@NotNull(groups = Crear.class)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal subtotal;

	@Builder.Default
	private List<PedidoItemExtraDto> extras = new ArrayList<>();

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}