package ec.com.erre.fastfood.share.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
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

	@NotNull(groups = { Crear.class })
	private Long platoId;

	@NotNull(groups = { Crear.class })
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime fechaInicio;

	@NotNull(groups = { Crear.class })
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime fechaFin;

	@NotNull(groups = { Crear.class })
	@Digits(integer = 3, fraction = 2)
	@DecimalMin(value = "0.01", inclusive = true) // > 0
	@DecimalMax(value = "100.00", inclusive = true) // ≤ 100
	private BigDecimal descuentoPct;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 1) // 'A' o 'I'
	private String estado;

	private String creadoPorSub;

	public interface Crear {
	}

	public interface Actualizar {
	}
}
