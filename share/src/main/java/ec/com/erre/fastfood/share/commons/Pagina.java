package ec.com.erre.fastfood.share.commons;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Pagina<T> {

	private List<T> contenido;

	private Long totalRegistros;

	private Integer paginaActual;

	private Integer totalpaginas;

}
