package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para solicitar un ajuste manual de inventario
 *
 * @author eduardo.romero
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteInventarioDto {

	@NotNull(message = "ingredienteId es obligatorio")
	private Long ingredienteId;

	@NotNull(message = "cantidad es obligatoria")
	private BigDecimal cantidad; // Positivo = entrada, Negativo = salida

	@Size(max = 80, message = "referencia no puede exceder 80 caracteres")
	private String referencia; // Descripción del ajuste (ej: "Merma por vencimiento", "Compra directa")

	@Builder.Default
	private Boolean permitirNegativo = false; // Permitir que el stock quede en negativo
}
