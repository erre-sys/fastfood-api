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
public class IngredienteDto {
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long grupoIngredienteId;

	@NotBlank(groups = { Crear.class })
	@Size(max = 40)
	private String codigo;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 160)
	private String nombre;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 16)
	private String unidad; // kg, g, ml, un

	private String esExtra;

	@Digits(integer = 10, fraction = 2)
	private BigDecimal precioExtra;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 11, fraction = 3)
	private BigDecimal stockMinimo;

	private String estado;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}