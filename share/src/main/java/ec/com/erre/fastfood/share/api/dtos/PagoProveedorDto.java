package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
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
public class PagoProveedorDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long proveedorId;

	@NotNull(groups = Crear.class)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal montoTotal;

	@NotBlank(groups = Crear.class)
	@Size(max = 16)
	private String metodo;

	@Size(max = 80)
	private String referencia;
	@Size(max = 500)
	private String observaciones;

	@NotBlank(groups = Crear.class)
	@Size(max = 64)
	private String creadoPorSub;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}