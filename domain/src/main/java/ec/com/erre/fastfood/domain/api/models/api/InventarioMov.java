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
	private String tipo; // COMPRA / CONSUMO / AJUSTE
	private BigDecimal cantidad; // escala 3
	private BigDecimal descuentoPct; // escala 2
	private String referencia;
	private Long compraItemId;
	private Long pedidoId;
}
