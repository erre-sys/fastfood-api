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
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "compra_id")
	private Long id;

	@Column(name = "proveedor_id", nullable = false)
	private Long proveedorId;

	@Column(name = "fecha", nullable = false)
	private LocalDateTime fecha;

	@Column(name = "referencia", length = 80)
	private String referencia;

	@Column(name = "creado_por_sub", nullable = false, length = 64)
	private String creadoPorSub;

	@Column(name = "observaciones", length = 500)
	private String observaciones;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", insertable = false, updatable = false)
	private ProveedorEntity proveedor;

	@OneToMany(mappedBy = "compra", fetch = FetchType.LAZY)
	private List<CompraItemEntity> items = new ArrayList<>();
}