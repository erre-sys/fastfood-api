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
public class RecetaItemDto {
	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long ingredienteId;
	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 12, fraction = 3)
	private BigDecimal cantidad; // por 1 plato

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}