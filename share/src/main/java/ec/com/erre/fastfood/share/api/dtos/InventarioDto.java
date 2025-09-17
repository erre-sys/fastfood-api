package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioDto {

	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long ingredienteId;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 12, fraction = 3)
	private BigDecimal stockActual;

	private LocalDateTime actualizadoEn;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}