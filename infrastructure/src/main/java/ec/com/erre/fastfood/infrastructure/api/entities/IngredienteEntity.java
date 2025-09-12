package ec.com.erre.fastfood.infrastructure.api.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "INGREDIENTES")
@ToString
public class IngredienteEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ingrediente_id")
	private Long id;

	@Column(name = "GRUPO_INGREDIENTE_ID")
	private Long grupoIngredienteId;

	@Column(nullable = false, unique = true, length = 40)
	private String codigo;

	@Column(nullable = false, length = 160)
	private String nombre;

	@Column(nullable = false, length = 16)
	private String unidad;

	@Column(name = "es_extra", nullable = false)
	private String esExtra;

	@Column(name = "precio_extra", precision = 12, scale = 2)
	private BigDecimal precioExtra;

	@Column(name = "stock_minimo", precision = 14, scale = 3, nullable = false)
	private BigDecimal stockMinimo;

	@Column(nullable = false)
	private String activo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_ingrediente_id", insertable = false, updatable = false)
	@JsonBackReference
	private GrupoIngredienteEntity grupoIngrediente;

}