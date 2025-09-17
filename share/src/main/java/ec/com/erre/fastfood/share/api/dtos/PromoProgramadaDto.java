package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoProgramadaDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long platoId;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 3, fraction = 2)
	private BigDecimal descuentoPct;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private LocalDateTime fechaInicio;

	@NotNull(groups = { Crear.class, Actualizar.class })
	private LocalDateTime fechaFin;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 1)
	private String estado; // A/I

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 64)
	private String creadoPorSub;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}