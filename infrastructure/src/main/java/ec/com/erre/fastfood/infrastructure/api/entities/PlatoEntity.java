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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "platos")
public class PlatoEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plato_id")
	private Long id;

	@Column(name = "codigo", nullable = false, length = 40, unique = true)
	private String codigo;

	@Column(name = "nombre", nullable = false, length = 120)
	private String nombre;

	@Column(name = "grupo_plato_id", nullable = false)
	private Long grupoPlatoId;

	@Column(name = "precio_base", precision = 14, scale = 2, nullable = false)
	private BigDecimal precioBase;

	@Column(name = "estado", nullable = false, length = 1) // 'A'/'I'
	private String estado;

	@Column(name = "en_promocion", nullable = false, length = 1) // 'S'/'N'
	private String enPromocion;

	@Column(name = "descuento_pct", precision = 5, scale = 2, nullable = false)
	private BigDecimal descuentoPct;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_plato_id", insertable = false, updatable = false)
	@JsonBackReference
	private GrupoPlatoEntity grupo;
}
