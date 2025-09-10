package ec.com.erre.fastfood.domain.api.models.api;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {
	private Long id;
	private String codigo;
	private String nombre;
	private GrupoPlato grupo;
	private BigDecimal precioBase;
	private boolean activo;
}