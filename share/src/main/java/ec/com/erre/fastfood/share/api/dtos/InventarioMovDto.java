package ec.com.erre.fastfood.share.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class InventarioMovDto {

	private Long id;

	@NotNull(groups = Crear.class)
	private Long ingredienteId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime fecha;

	@NotBlank(groups = Crear.class)
	@Size(max = 12)
	private String tipo;

	@NotNull(groups = Crear.class)
	@Digits(integer = 14, fraction = 3)
	private BigDecimal cantidad;

	@Digits(integer = 5, fraction = 2)
	private BigDecimal descuentoPct;

	@Size(max = 80)
	private String referencia;

	private Long compraItemId;
	private Long pedidoId;

	public interface Crear {
	}

	public interface Actualizar {
	}
}
