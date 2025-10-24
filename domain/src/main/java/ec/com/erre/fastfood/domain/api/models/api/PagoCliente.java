package ec.com.erre.fastfood.domain.api.models.api;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoCliente {
	private Long id;
	private Long pedidoId;
	private LocalDateTime fecha;
	private BigDecimal montoTotal;
	private String metodo;
	private String referencia;
	private String estado; // S=SOLICITADO, P=PAGADO, F=FIADO
	private String creadoPorSub;
}