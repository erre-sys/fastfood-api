package ec.com.erre.fastfood.domain.api.models.api;

import ec.com.erre.fastfood.domain.api.models.enums.PedidoEstado;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
	private Long id;
	private PedidoEstado estado;
	private BigDecimal totalBruto;
	private BigDecimal totalExtras;
	private BigDecimal totalNeto;
	private String creadoPorSub;
	private String entregadoPorSub;
	private LocalDateTime creadoEn;
	private LocalDateTime actualizadoEn;
	private List<PedidoItem> items;
}