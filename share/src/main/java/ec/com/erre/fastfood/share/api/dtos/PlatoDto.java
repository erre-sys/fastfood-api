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
public class PlatoDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 40)
	private String codigo;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 120)
	private String nombre;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long grupoPlatoId;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 14, fraction = 2)
	@DecimalMin(value = "0.00", inclusive = true)
	private BigDecimal precioBase;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 1) // 'A'/'I'
	private String estado;

	// Solo lectura (el sistema los define vía SP de promociones)
	private String enPromocion;
	private BigDecimal descuentoPct;

	public interface Crear {
	}

	public interface Actualizar {
	}
}
