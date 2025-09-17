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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoProveedorEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pago_proveedor_id")
	private Long id;

	@Column(name = "proveedor_id", nullable = false)
	private Long proveedorId;

	@Column(name = "fecha", nullable = false)
	private LocalDateTime fecha;

	@Column(name = "monto_total", precision = 12, scale = 2, nullable = false)
	private BigDecimal montoTotal;

	@Column(name = "metodo", nullable = false, length = 16)
	private String metodo;

	@Column(name = "referencia", length = 80)
	private String referencia;

	@Column(name = "observaciones", length = 500)
	private String observaciones;

	@Column(name = "creado_por_sub", nullable = false, length = 64)
	private String creadoPorSub;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", insertable = false, updatable = false)
	private ProveedorEntity proveedor;
}