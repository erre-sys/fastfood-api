package ec.com.erre.fastfood.domain.api.models.api;

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
public class Proveedor {
	private Long id;
	private String nombre;
	private String ruc;
	private String telefono;
	private String email;
	private String estado;
}