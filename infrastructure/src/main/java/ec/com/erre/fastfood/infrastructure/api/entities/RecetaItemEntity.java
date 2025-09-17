package ec.com.erre.fastfood.infrastructure.api.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Data;
import jakarta.persistence.IdClass;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receta_item")
@ToString
@IdClass(RecetaItemPK.class)
public class RecetaItemEntity implements Serializable {

	@Id
	@Column(name = "ingrediente_id")
	private Long ingredienteId;
	@Id
	@Column(name = "plato_id")
	private Long platoId;

	@Column(name = "cantidad", precision = 14, scale = 3, nullable = false)
	private BigDecimal cantidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plato_id", insertable = false, updatable = false)
	@JsonBackReference
	private PlatoEntity plato;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingrediente_id", insertable = false, updatable = false)
	@JsonBackReference
	private IngredienteEntity ingrediente;
}