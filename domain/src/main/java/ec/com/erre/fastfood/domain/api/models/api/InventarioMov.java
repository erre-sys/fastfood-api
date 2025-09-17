package ec.com.erre.fastfood.domain.api.models.api;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioMov {

	private Long id;
	private Long ingredienteId;
	private LocalDateTime fecha;
	private String tipo;
	private BigDecimal cantidad;
	private BigDecimal descuentoPct;
	private String referencia;
	private Long compraItemId;
	private Long pedidoId;

}