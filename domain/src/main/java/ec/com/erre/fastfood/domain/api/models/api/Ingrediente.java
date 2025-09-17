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
public class Ingrediente {
	private Long id;
	private Long grupoIngredienteId;
	private String codigo;
	private String nombre;
	private String unidad;
	private String esExtra; // S/N
	private BigDecimal precioExtra;
	private BigDecimal stockMinimo;
	private String estado;
}