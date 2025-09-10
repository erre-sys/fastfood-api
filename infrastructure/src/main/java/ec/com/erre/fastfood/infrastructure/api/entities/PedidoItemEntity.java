package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItemEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pedido_item_id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false)
	private PedidoEntity pedido;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plato_id", nullable = false)
	private PlatoEntity plato;
	@Column(nullable = false)
	private Integer cantidad;
	@Column(name = "precio_unitario", precision = 12, scale = 2, nullable = false)
	private BigDecimal precioUnitario;
	@Column(precision = 12, scale = 2, nullable = false)
	private BigDecimal subtotal;
	@OneToMany(mappedBy = "pedidoItem", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PedidoItemExtraEntity> extras = new ArrayList<>();
}