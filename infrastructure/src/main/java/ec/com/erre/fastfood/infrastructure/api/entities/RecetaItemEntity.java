package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "receta_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaItemEntity {
	@EmbeddedId
	private RecetaItemId id;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("platoId")
	@JoinColumn(name = "plato_id")
	private PlatoEntity plato;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("ingredienteId")
	@JoinColumn(name = "ingrediente_id")
	private IngredienteEntity ingrediente;
	@Column(precision = 14, scale = 3, nullable = false)
	private BigDecimal cantidad;
}