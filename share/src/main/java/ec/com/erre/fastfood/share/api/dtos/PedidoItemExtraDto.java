package ec.com.erre.fastfood.share.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO para extra de un item de pedido")
public class PedidoItemExtraDto {

	@Schema(description = "ID del extra (solo lectura)", accessMode = Schema.AccessMode.READ_ONLY)
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@Schema(description = "ID del item al que pertenece (se llena automáticamente)", accessMode = Schema.AccessMode.READ_ONLY)
	private Long pedidoItemId;

	@Schema(description = "ID del ingrediente a agregar como extra", example = "10", required = true)
	@NotNull(groups = { Crear.class })
	private Long ingredienteId;

	@Schema(description = "Cantidad del extra", example = "1.0", required = true)
	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 14, fraction = 3)
	private BigDecimal cantidad;

	@Schema(description = "Precio del extra (calculado por el backend)", accessMode = Schema.AccessMode.READ_ONLY)
	private BigDecimal precioExtra;

	public interface Crear {
	}

	public interface Actualizar {
	}
}