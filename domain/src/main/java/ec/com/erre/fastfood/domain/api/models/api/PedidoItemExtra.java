package ec.com.erre.fastfood.domain.api.models.api;

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
public class PedidoItemExtra {
	private Long id;
	private Long pedidoItemId;
	private Long ingredienteId;
	private BigDecimal cantidad; // escala 3
	private BigDecimal precioExtra; // TOTAL línea, escala 2
}