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
public class IngredienteDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long grupoIngredienteId;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 40)
	private String codigo;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 120)
	private String nombre;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 12)
	private String unidad; // validada en service: KG,G,LT,ML,UND,...

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 1)
	private String esExtra; // S/N

	@Digits(integer = 14, fraction = 2)
	@DecimalMin(value = "0.00", inclusive = true)
	private BigDecimal precioExtra; // requerido si esExtra='S'

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 14, fraction = 3)
	@DecimalMin(value = "0.000", inclusive = true)
	private BigDecimal stockMinimo;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 1)
	private String aplicaComida; // S/N

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 1)
	private String estado; // A/I

	public interface Crear {
	}

	public interface Actualizar {
	}
}
