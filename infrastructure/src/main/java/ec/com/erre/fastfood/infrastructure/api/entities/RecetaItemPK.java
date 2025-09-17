package ec.com.erre.fastfood.infrastructure.api.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RecetaItemPK implements Serializable {
	private Long platoId;
	private Long ingredienteId;
}