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
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grupo_ingrediente")
@ToString
public class GrupoIngredienteEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grupo_ingrediente_id")
	private Long id;

	@Column(name = "nombre", nullable = false, length = 120, unique = true)
	private String nombre;

	@Column(name = "estado", nullable = false, length = 1) // A/I
	private String estado;

	@Column(name = "aplica_comida", nullable = false, length = 1) // S/N
	private String aplicaComida;
}
