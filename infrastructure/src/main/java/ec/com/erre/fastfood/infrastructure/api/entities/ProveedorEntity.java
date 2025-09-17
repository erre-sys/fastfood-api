package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proveedor_id")
	private Long id;

	@Column(name = "nombre", nullable = false, length = 160)
	private String nombre;

	@Column(name = "ruc", length = 20, unique = true)
	private String ruc;

	@Column(name = "telefono", length = 40)
	private String telefono;

	@Column(name = "email", length = 160)
	private String email;

	@Column(name = "estado", nullable = false, length = 1) // S/N
	private String estado;
}