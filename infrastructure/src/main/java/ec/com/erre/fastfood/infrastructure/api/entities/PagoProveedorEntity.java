package ec.com.erre.fastfood.infrastructure.api.entities;

import ec.com.erre.fastfood.domain.api.models.enums.MetodoPago;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoProveedorEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pago_proveedor_id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", nullable = false)
	private ProveedorEntity proveedor;
	@Column(nullable = false)
	private LocalDateTime fecha;
	@Column(name = "monto_total", precision = 12, scale = 2, nullable = false)
	private BigDecimal montoTotal;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MetodoPago metodo;
	@Column(length = 80)
	private String referencia;
	@Column(length = 500)
	private String observaciones;
	@Column(name = "creado_por_sub", length = 64, nullable = false)
	private String creadoPorSub;
}