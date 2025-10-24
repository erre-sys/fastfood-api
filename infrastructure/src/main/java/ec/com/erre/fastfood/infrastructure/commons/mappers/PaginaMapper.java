package ec.com.erre.fastfood.infrastructure.commons.mappers;

import ec.com.erre.fastfood.share.commons.Pagina;

import java.util.function.Function;

/**
 * Utilidad para mapear objetos Pagina de un tipo a otro Elimina duplicación de código en controllers
 *
 * @author eduardo.romero
 * @version 1.0
 */
public class PaginaMapper {

	/**
	 * Mapea una Pagina de un tipo E (Entity/Domain) a un tipo D (DTO)
	 *
	 * @param source Pagina original
	 * @param mapper Función para mapear cada elemento individual
	 * @param <D> Tipo de destino (DTO)
	 * @param <E> Tipo de origen (Entity/Domain)
	 * @return Pagina mapeada al tipo de destino
	 */
	public static <D, E> Pagina<D> map(Pagina<E> source, Function<E, D> mapper) {
		return Pagina.<D> builder().contenido(source.getContenido().stream().map(mapper).toList())
				.totalRegistros(source.getTotalRegistros()).paginaActual(source.getPaginaActual())
				.totalpaginas(source.getTotalpaginas()).build();
	}
}
