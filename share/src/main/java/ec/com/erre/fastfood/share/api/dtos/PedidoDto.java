package ec.com.erre.fastfood.share.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para representar un pedido en el sistema POS")
public class PedidoDto {

	@Schema(description = "ID único del pedido", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@Schema(description = "Estado del pedido: C=CREADO, L=LISTO, E=ENTREGADO, A=ANULADO", example = "C", maxLength = 1)
	@Size(max = 1)
	private String estado;

	@Schema(description = "Total bruto del pedido (suma de items sin extras)", example = "25.50", accessMode = Schema.AccessMode.READ_ONLY)
	@Digits(integer = 14, fraction = 2)
	private BigDecimal totalBruto;

	@Schema(description = "Total de extras agregados al pedido", example = "3.00", accessMode = Schema.AccessMode.READ_ONLY)
	@Digits(integer = 14, fraction = 2)
	private BigDecimal totalExtras;

	@Schema(description = "Total neto del pedido (bruto + extras - descuentos)", example = "28.50", accessMode = Schema.AccessMode.READ_ONLY)
	@Digits(integer = 14, fraction = 2)
	private BigDecimal totalNeto;

	@Schema(description = "Observaciones o comentarios del pedido", example = "Sin cebolla en la hamburguesa", maxLength = 255)
	@Size(max = 255)
	private String observaciones;

	private String entregadoPorSub;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime creadoEn;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime actualizadoEn;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime entregadoEn;

	@Builder.Default
	private List<PedidoItemDto> items = new ArrayList<>();

	@Builder.Default
	private List<PedidoItemExtraDto> itemsExtras = new ArrayList<>();

	public interface Crear {
	}

	public interface Actualizar {
	}
}