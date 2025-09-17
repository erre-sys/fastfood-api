package ec.com.erre.fastfood.share.api.dtos;

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

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 12)
	private String estado;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 10, fraction = 2)
	private BigDecimal totalBruto;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 10, fraction = 2)
	private BigDecimal totalExtras;

	@NotNull(groups = { Crear.class, Actualizar.class })
	@Digits(integer = 10, fraction = 2)
	private BigDecimal totalNeto;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 64)
	private String creadoPorSub;

	@Size(max = 64)
	private String entregadoPorSub;
	private LocalDateTime entregadoEn;

	@Builder.Default
	private List<PedidoItemDto> items = new ArrayList<>();

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}
