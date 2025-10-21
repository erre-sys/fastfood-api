package ec.com.erre.fastfood.infrastructure.api.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "promo_programada")
public class PromoProgramadaEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "promo_id")
	private Long id;

	@Column(name = "plato_id", nullable = false)
	private Long platoId;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDateTime fechaInicio;

	@Column(name = "fecha_fin", nullable = false)
	private LocalDateTime fechaFin;

	@Column(name = "descuento_pct", precision = 5, scale = 2, nullable = false)
	private BigDecimal descuentoPct;

	@Column(name = "estado", length = 1, nullable = false)
	private String estado;

	@Column(name = "creado_por_sub", length = 64)
	private String creadoPorSub;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plato_id", insertable = false, updatable = false)
	@JsonBackReference
	private PlatoEntity plato;
}
