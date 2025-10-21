package ec.com.erre.fastfood.domain.api.models.api;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {
	private Long id;
	private String codigo;
	private String nombre;
	private Long grupoPlatoId;
	private BigDecimal precioBase; // escala 2
	private String estado; // 'A'/'I'
	private String enPromocion; // 'S'/'N' (solo lectura desde CRUD)
	private BigDecimal descuentoPct; // escala 2 (solo lectura desde CRUD)
}