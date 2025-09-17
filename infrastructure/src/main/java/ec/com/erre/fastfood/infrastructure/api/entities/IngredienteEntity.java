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
@Table(name = "ingredientes")
@ToString
public class IngredienteEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ingrediente_id")
	private Long id;

	@Column(name = "grupo_ingrediente_id", nullable = false)
	private Long grupoIngredienteId;

	@Column(name = "codigo", nullable = false, length = 40, unique = true)
	private String codigo;

	@Column(name = "nombre", nullable = false, length = 160)
	private String nombre;

	@Column(name = "unidad", nullable = false, length = 16)
	private String unidad;

	@Column(name = "es_extra", nullable = false, length = 1) // S/N
	private String esExtra;

	@Column(name = "precio_extra", precision = 12, scale = 2)
	private BigDecimal precioExtra;

	@Column(name = "stock_minimo", precision = 14, scale = 3, nullable = false)
	private BigDecimal stockMinimo;

	@Column(name = "activo", nullable = false, length = 1) // S/N
	private String estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_ingrediente_id", insertable = false, updatable = false)
	@JsonBackReference
	private GrupoIngredienteEntity grupoIngrediente;

}