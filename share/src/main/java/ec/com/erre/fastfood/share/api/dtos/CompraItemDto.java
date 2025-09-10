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
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;
	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long ingredienteId;
	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 12, fraction = 3)
	private BigDecimal cantidad;
	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 10, fraction = 4)
	private BigDecimal costoUnitario;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}