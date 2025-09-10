package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_item_extra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItemExtraEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pedido_item_extra_id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_item_id", nullable = false)
	private PedidoItemEntity pedidoItem;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingrediente_id", nullable = false)
	private IngredienteEntity ingrediente;
	@Column(precision = 14, scale = 3, nullable = false)
	private BigDecimal cantidad;
	@Column(name = "precio_extra", precision = 12, scale = 2, nullable = false)
	private BigDecimal precioExtra;
}