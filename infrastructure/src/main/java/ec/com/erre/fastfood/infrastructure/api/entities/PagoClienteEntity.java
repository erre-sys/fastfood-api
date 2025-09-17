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
	private Long pedidoId;

	@Column(name = "fecha", nullable = false)
	private LocalDateTime fecha;

	@Column(name = "monto_total", precision = 12, scale = 2, nullable = false)
	private BigDecimal montoTotal;

	@Column(name = "metodo", nullable = false, length = 16)
	private String metodo;

	@Column(name = "referencia", length = 80)
	private String referencia;

	@Column(name = "creado_por_sub", nullable = false, length = 64)
	private String creadoPorSub;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", insertable = false, updatable = false)
	private PedidoEntity pedido;
}