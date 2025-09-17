package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoClienteDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = Crear.class)
	private Long pedidoId;

	@NotNull(groups = Crear.class)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal montoTotal;

	@NotBlank(groups = Crear.class)
	@Size(max = 16)
	private String metodo;

	@Size(max = 80)
	private String referencia;

	@NotBlank(groups = Crear.class)
	@Size(max = 64)
	private String creadoPorSub;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}