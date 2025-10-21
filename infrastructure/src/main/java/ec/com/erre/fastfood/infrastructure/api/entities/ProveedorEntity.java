package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProveedorEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proveedor_id")
	private Long id;

	@Column(name = "nombre", nullable = false, length = 120, unique = true)
	private String nombre;

	@Column(name = "ruc", nullable = false, length = 13, unique = true)
	private String ruc;

	@Column(name = "telefono", length = 30)
	private String telefono;

	@Column(name = "email", length = 120)
	private String email;

	@Column(name = "estado", nullable = false, length = 1)
	private String estado;

}