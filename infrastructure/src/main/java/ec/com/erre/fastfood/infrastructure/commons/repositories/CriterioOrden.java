package ec.com.erre.fastfood.infrastructure.commons.repositories;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CriterioOrden {

	public CriterioOrden(String campo, String direccion) {
		this.campo = campo;
		this.orden = Direccion.valueOf(direccion);
	}

	private String campo;
	private Direccion orden;

	public enum Direccion {
		ASC, DESC
	}

	public boolean isAscendente() {
		return Direccion.ASC.equals(orden);
	}

}
