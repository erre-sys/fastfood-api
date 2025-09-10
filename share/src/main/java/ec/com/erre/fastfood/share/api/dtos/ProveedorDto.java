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
	@Size(max = 160)
	private String nombre;
	@Size(max = 20)
	private String ruc;
	@Size(max = 40)
	private String telefono;
	@Email
	@Size(max = 160)
	private String email;
	private Boolean activo;

	// Interfaces para definir grupos
	public interface Crear {
	}

	public interface Actualizar {
	}
}
