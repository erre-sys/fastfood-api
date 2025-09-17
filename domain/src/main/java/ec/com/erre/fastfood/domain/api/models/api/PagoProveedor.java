package ec.com.erre.fastfood.domain.api.models.api;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoProveedor {
	private Long id;
	private Long proveedorId;
	private LocalDateTime fecha;
	private BigDecimal montoTotal;
	private String metodo;
	private String referencia;
	private String observaciones;
	private String creadoPorSub;
}