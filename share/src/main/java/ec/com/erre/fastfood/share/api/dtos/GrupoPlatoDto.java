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
public class GrupoPlatoDto {
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;
	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 120)
	private String nombre;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}