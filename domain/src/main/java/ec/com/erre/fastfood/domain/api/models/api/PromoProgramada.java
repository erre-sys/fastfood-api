package ec.com.erre.fastfood.domain.api.models.api;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoProgramada {
	private Long id;
	private Long platoId;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private BigDecimal descuentoPct;
	private String estado; // 'A' / 'I'
	private String creadoPorSub;
}