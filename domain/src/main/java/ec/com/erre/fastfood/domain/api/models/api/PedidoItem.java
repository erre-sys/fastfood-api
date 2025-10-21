package ec.com.erre.fastfood.domain.api.models.api;

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
public class PedidoItem {
	private Long id;
	private Long pedidoId;
	private Long platoId;
	private Integer cantidad;
	private BigDecimal precioUnitario;
	private BigDecimal descuentoPct;
	private BigDecimal descuentoMonto;
	private BigDecimal subtotal;
}