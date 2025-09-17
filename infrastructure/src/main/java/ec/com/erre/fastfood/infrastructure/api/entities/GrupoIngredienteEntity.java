package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
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
@Table(name = "grupo_ingrediente")
@ToString
public class GrupoIngredienteEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grupo_ingrediente_id")
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String nombre;

	@Column(nullable = false, unique = true, length = 120)
	private String estado;

	@OneToMany(mappedBy = "grupoIngrediente", fetch = FetchType.LAZY)
	private List<IngredienteEntity> ingredientes;
}
