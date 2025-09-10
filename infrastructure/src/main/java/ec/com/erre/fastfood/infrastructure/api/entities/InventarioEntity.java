package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioEntity {
	@Id
	@Column(name = "ingrediente_id")
	private Long ingredienteId;
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "ingrediente_id")
	private IngredienteEntity ingrediente;
	@Column(name = "stock_actual", precision = 14, scale = 3, nullable = false)
	private BigDecimal stockActual;
	@Column(name = "actualizado_en", nullable = false)
	private LocalDateTime actualizadoEn;
}
