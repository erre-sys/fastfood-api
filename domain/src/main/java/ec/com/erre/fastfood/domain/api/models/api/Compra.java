package ec.com.erre.fastfood.domain.api.models.api;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compra {
	private Long id;
	private Long proveedorId;
	private LocalDateTime fecha;
	private String referencia;
	private String creadoPorSub;
	private String observaciones;

	private List<CompraItem> compras = new ArrayList<>();

}