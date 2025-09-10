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
public class CompraEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "compra_id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", nullable = false)
	private ProveedorEntity proveedor;
	@Column(nullable = false)
	private LocalDateTime fecha;
	@Column(length = 80)
	private String referencia;
	@Column(name = "creado_por_sub", length = 64, nullable = false)
	private String creadoPorSub;
	@Column(length = 500)
	private String observaciones;
	@OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CompraItemEntity> items = new ArrayList<>();
}