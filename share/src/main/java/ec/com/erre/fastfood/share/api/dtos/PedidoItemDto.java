package ec.com.erre.fastfood.share.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "DTO para item de pedido con sus extras opcionales")
public class PedidoItemDto {

	@Schema(description = "ID del item (solo lectura)", accessMode = Schema.AccessMode.READ_ONLY)
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@Schema(description = "ID del pedido al que pertenece", accessMode = Schema.AccessMode.READ_ONLY)
	private Long pedidoId;

	@Schema(description = "ID del plato a agregar", example = "1", required = true)
	@NotNull(groups = { Crear.class })
	private Long platoId;

	@Schema(description = "Cantidad del plato", example = "2.0", required = true)
	@NotNull(groups = { Crear.class })
	@Digits(integer = 14, fraction = 3)
	@DecimalMin(value = "0.001", inclusive = true)
	private BigDecimal cantidad;

	@Schema(description = "Precio unitario calculado por el backend (con descuentos si aplica)", accessMode = Schema.AccessMode.READ_ONLY)
	private BigDecimal precioUnitario;

	@Schema(description = "Porcentaje de descuento aplicado", accessMode = Schema.AccessMode.READ_ONLY)
	private BigDecimal descuentoPct;

	@Schema(description = "Monto de descuento aplicado", accessMode = Schema.AccessMode.READ_ONLY)
	private BigDecimal descuentoMonto;

	@Schema(description = "Subtotal del item (cantidad * precio unitario)", accessMode = Schema.AccessMode.READ_ONLY)
	private BigDecimal subtotal;

	@Schema(description = "Lista de extras para este item (opcional)")
	@Valid
	@Builder.Default
	private List<PedidoItemExtraDto> extras = new ArrayList<>();

	public interface Crear {
	}

	public interface Actualizar {
	}
}
