package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoIngredienteDto {

	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 120)
	private String nombre;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 120)
	private String estado;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}

}
