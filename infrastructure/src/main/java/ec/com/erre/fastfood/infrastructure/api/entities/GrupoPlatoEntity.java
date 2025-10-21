package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grupo_plato")
@ToString
public class GrupoPlatoEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grupo_plato_id")
	private Long id;

	@Column(name = "nombre", nullable = false, length = 120)
	private String nombre;

	@Column(name = "estado", nullable = false, length = 1)
	private String estado;
}