package ec.com.erre.fastfood.share.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

	@NotNull(groups = { Crear.class })
	private Long proveedorId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	private LocalDateTime fecha;

	@Size(max = 80)
	private String referencia;

	@Size(max = 255)
	private String observaciones;

	private BigDecimal total;

	@NotEmpty(groups = { Crear.class })
	@Builder.Default
	private List<CompraItemDto> items = new ArrayList<>();

	public interface Crear {
	}

	public interface Actualizar {
	}

}