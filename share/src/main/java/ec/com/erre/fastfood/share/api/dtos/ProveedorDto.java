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
public class ProveedorDto {
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 120)
	private String nombre;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 13)
	private String ruc;

	@Size(max = 30)
	private String telefono;

	@Size(max = 120)
	@Email
	private String email;

	@NotBlank(groups = { Crear.class, Actualizar.class })
	@Size(max = 1)
	private String estado;

	public interface Crear {
	}

	public interface Actualizar {
	}
}