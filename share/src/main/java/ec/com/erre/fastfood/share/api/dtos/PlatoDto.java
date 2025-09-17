package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoDto {
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long grupoPlatoId;

	@NotBlank(groups = { Crear.class })
	@Size(max = 40)
	private String codigo;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	private String nombre;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 10, fraction = 2)
	private BigDecimal precioBase;

	private String estado;

	private String enPromocion;

	private BigDecimal descuentoPct;

	private List<RecetaItemDto> recetas = new ArrayList<>();

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}