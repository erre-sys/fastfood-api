package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pago_cliente")
public class PagoClienteEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pago_cliente_id")
	private Long id;
	@Column(name = "pedido_id", nullable = false)
	private Long pedidoId; // FK simple
	private LocalDateTime fecha;
	@Column(name = "monto_total", precision = 14, scale = 2, nullable = false)
	private BigDecimal montoTotal;
	@Column(length = 20)
	private String metodo;
	@Column(length = 40)
	private String referencia;
	@Column(length = 1, nullable = false)
	private String estado; // S=SOLICITADO, P=PAGADO, F=FIADO
	@Column(name = "creado_por_sub", length = 64)
	private String creadoPorSub;
}