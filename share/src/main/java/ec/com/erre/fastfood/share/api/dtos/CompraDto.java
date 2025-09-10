package ec.com.erre.fastfood.share.api.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraDto {
	@Null(groups = Crear.class)
	@NotNull(groups = Actualizar.class)
	private Long id;
	@NotNull(groups = { Crear.class, Actualizar.class })
	private Long proveedorId;
	private String referencia;
	@Size(max = 500)
	private String observaciones;
	private String creadoPorSub;
	private LocalDateTime fecha;
	private List<CompraItemDto> items; // opcional
	// Interfaces para definir grupos

	public interface Crear {
	}

	public interface Actualizar {
	}
}