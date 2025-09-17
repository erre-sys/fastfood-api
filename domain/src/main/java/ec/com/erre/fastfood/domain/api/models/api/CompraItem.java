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
public class CompraItem {
	private Long id;
	private Long compraId;
	private Long ingredienteId;
	private BigDecimal cantidad;
	private BigDecimal costoUnitario;
}