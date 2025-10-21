package ec.com.erre.fastfood.share.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
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

	@NotNull(groups = { Crear.class })
	private Long proveedorId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime fecha;

	@NotNull(groups = { Crear.class })
	@Digits(integer = 14, fraction = 2)
	@DecimalMin(value = "0.01", inclusive = true) // > 0
	private BigDecimal montoTotal;

	@NotBlank(groups = { Crear.class })
	@Size(max = 20) // EFECTIVO/TRANSFERENCIA/TARJETA/CHEQUE/OTRO
	private String metodo;

	@Size(max = 80)
	private String referencia;

	@Size(max = 255)
	private String observaciones;

	public interface Crear {
	}

	public interface Actualizar {
	}
}