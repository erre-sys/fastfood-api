package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pedido_id")
	private Long id;

	@Column(name = "estado", length = 1, nullable = false) // C,P,L,E,X
	private String estado;

	@Column(name = "total_bruto", precision = 14, scale = 2, nullable = false)
	private BigDecimal totalBruto;

	@Column(name = "total_extras", precision = 14, scale = 2, nullable = false)
	private BigDecimal totalExtras;

	@Column(name = "total_neto", precision = 14, scale = 2, nullable = false)
	private BigDecimal totalNeto;

	@Column(name = "observaciones", length = 255)
	private String observaciones;

	@Column(name = "creado_por_sub", length = 64)
	private String creadoPorSub;

	@Column(name = "entregado_por_sub", length = 64)
	private String entregadoPorSub;

	@Column(name = "creado_en", nullable = false)
	private LocalDateTime creadoEn;

	@Column(name = "actualizado_en", nullable = false)
	private LocalDateTime actualizadoEn;

	@Column(name = "entregado_en")
	private LocalDateTime entregadoEn;
}