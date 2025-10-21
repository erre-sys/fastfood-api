package ec.com.erre.fastfood.infrastructure.api.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
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
public class PedidoItemEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pedido_item_id")
	private Long id;

	@Column(name = "pedido_id", nullable = false)
	private Long pedidoId;

	@Column(name = "plato_id", nullable = false)
	private Long platoId;

	@Column(name = "cantidad", precision = 14, scale = 3, nullable = false)
	private BigDecimal cantidad;

	@Column(name = "precio_unitario", precision = 14, scale = 2, nullable = false)
	private BigDecimal precioUnitario;

	@Column(name = "descuento_pct", precision = 5, scale = 2)
	private BigDecimal descuentoPct; // puede ser null

	@Column(name = "descuento_monto", precision = 14, scale = 2)
	private BigDecimal descuentoMonto; // puede ser null

	@Column(name = "subtotal", precision = 14, scale = 2, nullable = false)
	private BigDecimal subtotal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", insertable = false, updatable = false)
	@JsonBackReference
	private PedidoEntity pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plato_id", insertable = false, updatable = false)
	@JsonBackReference
	private PlatoEntity plato;
}
