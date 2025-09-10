package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
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
	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long platoId;
	@NotNull(groups = { Crear.class, Actualizar.class })
	@Min(1)
	private Integer cantidad;
	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 10, fraction = 2)
	private BigDecimal precioUnitario;
	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 10, fraction = 2)
	private BigDecimal subtotal;
	private List<PedidoItemExtraDto> extras;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}