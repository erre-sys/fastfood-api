package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_mov")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioMovEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inventario_mov_id")
	private Long id;

	@Column(name = "ingrediente_id", nullable = false)
	private Long ingredienteId;

	@Column(name = "fecha", nullable = false)
	private LocalDateTime fecha;

	@Column(name = "tipo", nullable = false, length = 12) // COMPRA/CONSUMO/AJUSTE
	private String tipo;

	@Column(name = "cantidad", precision = 14, scale = 3, nullable = false)
	private BigDecimal cantidad; // +entrada / -salida

	@Column(name = "descuento_pct", precision = 5, scale = 2, nullable = false)
	private BigDecimal descuentoPct;

	@Column(name = "referencia", length = 80)
	private String referencia;

	@Column(name = "compra_item_id")
	private Long compraItemId;

	@Column(name = "pedido_id")
	private Long pedidoId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingrediente_id", insertable = false, updatable = false)
	private IngredienteEntity ingrediente;
}