package ec.com.erre.fastfood.infrastructure.api.entities;

import ec.com.erre.fastfood.domain.api.models.enums.PedidoEstado;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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
public class PedidoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pedido_id")
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PedidoEstado estado;
	@Column(name = "total_bruto", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalBruto;
	@Column(name = "total_extras", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalExtras;
	@Column(name = "total_neto", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalNeto;
	@Column(name = "creado_por_sub", length = 64, nullable = false)
	private String creadoPorSub;
	@Column(name = "entregado_por_sub", length = 64)
	private String entregadoPorSub;
	@Column(name = "creado_en", nullable = false)
	private LocalDateTime creadoEn;
	@Column(name = "actualizado_en", nullable = false)
	private LocalDateTime actualizadoEn;
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PedidoItemEntity> items = new ArrayList<>();
}