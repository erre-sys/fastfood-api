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
	@NotNull(groups = Crear.class)
	private Long ingredienteId;

	@NotNull(groups = Crear.class)
	@Digits(integer = 11, fraction = 3)
	private BigDecimal cantidad;

	@NotNull(groups = Crear.class)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal precioExtra;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}