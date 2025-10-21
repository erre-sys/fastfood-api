package ec.com.erre.fastfood.infrastructure.api.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "compra_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraItemEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "compra_item_id")
	private Long id;

	@Column(name = "compra_id", nullable = false)
	private Long compraId;

	@Column(name = "ingrediente_id", nullable = false)
	private Long ingredienteId;

	@Column(name = "cantidad", precision = 14, scale = 3, nullable = false)
	private BigDecimal cantidad;

	@Column(name = "costo_unitario", precision = 14, scale = 2, nullable = false)
	private BigDecimal costoUnitario;

	// Navegación opcional (no necesaria para tu flujo)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compra_id", insertable = false, updatable = false)
	@JsonBackReference
	private CompraEntity compra;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingrediente_id", insertable = false, updatable = false)
	@JsonBackReference
	private IngredienteEntity ingrediente;

}