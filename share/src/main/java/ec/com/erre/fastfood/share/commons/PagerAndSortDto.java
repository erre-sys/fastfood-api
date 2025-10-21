package ec.com.erre.fastfood.share.commons;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PagerAndSortDto {
	private String direction;

	private String orderBy;

	private Integer page = 0;

	private Integer size = 0;

	public boolean datosOrdenamientoCompleto() {
		return direction != null && orderBy != null && !direction.isEmpty() && !orderBy.isEmpty();
	}

	public boolean isAscendente() {
		return "asc".equals(orderBy.toLowerCase());
	}
}