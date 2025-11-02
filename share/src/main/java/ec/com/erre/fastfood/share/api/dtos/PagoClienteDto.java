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
public class PagoClienteDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotNull
	private Long pedidoId;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime fecha;

	@NotNull
	@DecimalMin("0.00")
	private BigDecimal montoTotal;

	@NotBlank
	@Size(max = 20)
	private String metodo;

	@Size(max = 40)
	private String referencia;

	@Size(max = 1)
	@Pattern(regexp = "[SPF]", message = "Estado debe ser S (SOLICITADO), P (PAGADO) o F (FIADO)")
	private String estado;

	private String creadoPorSub;

	public interface Crear {
	}

	public interface Actualizar {
	}
}