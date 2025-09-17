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
import jakarta.persistence.OneToMany;
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
@Table(name = "PLATOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PLATO_ID")
	private Long id;

	@Column(name = "GRUPO_PLATO_ID")
	private Long grupoPlatoId;

	@Column(nullable = false, unique = true, length = 40)
	private String codigo;

	@Column(nullable = false, length = 160)
	private String nombre;

	@Column(name = "PRECIO_BASE", precision = 12, scale = 2, nullable = false)
	private BigDecimal precioBase;

	@Column(nullable = false, length = 1)
	private String estado;

	@Column(name = "EN_PROMOCION", precision = 12, scale = 2, nullable = false)
	private String enPromocion;

	@Column(name = "DESCUENTO_PCT", precision = 12, scale = 2, nullable = false)
	private BigDecimal descuentoPct;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_plato_id", insertable = false, updatable = false)
	private GrupoPlatoEntity grupoPlato;

	@OneToMany(mappedBy = "plato", fetch = FetchType.LAZY)
	private List<RecetaItemEntity> receta = new ArrayList<>();
}