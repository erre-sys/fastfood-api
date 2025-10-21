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
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;
	@NotNull
	private Long platoId;
	@NotNull
	private Long ingredienteId;
	@NotNull
	@DecimalMin("0.0001")
	private BigDecimal cantidad;

	public interface Crear {
	}

	public interface Actualizar {
	}
}