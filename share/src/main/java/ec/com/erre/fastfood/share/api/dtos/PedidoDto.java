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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@Size(max = 1)
	private String estado;

	@Digits(integer = 14, fraction = 2)
	private BigDecimal totalBruto;

	@Digits(integer = 14, fraction = 2)
	private BigDecimal totalExtras;

	@Digits(integer = 14, fraction = 2)
	private BigDecimal totalNeto;

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

	public interface Crear {
	}

	public interface Actualizar {
	}
}