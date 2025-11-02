package ec.com.erre.fastfood.domain.api.models.api;

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
public class Pedido {
	private Long id;
	private String estado;
	private BigDecimal totalBruto;
	private BigDecimal totalExtras;
	private BigDecimal totalNeto;
	private String observaciones;
	private String creadoPorSub;
	private String entregadoPorSub;
	private LocalDateTime creadoEn;
	private LocalDateTime actualizadoEn;
	private LocalDateTime entregadoEn;

	@Builder.Default
	private List<PedidoItem> items = new ArrayList<>();
	@Builder.Default
	private List<PedidoItemExtra> itemsExtras = new ArrayList<>();
}