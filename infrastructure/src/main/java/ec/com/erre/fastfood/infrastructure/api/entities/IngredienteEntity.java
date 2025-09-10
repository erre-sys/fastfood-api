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
@Table(name = "ingredientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ingrediente_id")
	private Long id;
	@Column(nullable = false, unique = true, length = 40)
	private String codigo;
	@Column(nullable = false, length = 160)
	private String nombre;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_ingrediente_id", nullable = false)
	private GrupoIngredienteEntity grupo;
	@Column(nullable = false, length = 16)
	private String unidad;
	@Column(name = "es_extra", nullable = false)
	private boolean esExtra;
	@Column(name = "precio_extra", precision = 12, scale = 2)
	private BigDecimal precioExtra;
	@Column(name = "stock_minimo", precision = 14, scale = 3, nullable = false)
	private BigDecimal stockMinimo;
	@Column(nullable = false)
	private boolean activo = true;
}