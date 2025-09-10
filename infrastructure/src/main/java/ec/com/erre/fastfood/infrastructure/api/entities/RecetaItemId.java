package ec.com.erre.fastfood.infrastructure.api.entities;

import jakarta.persistence.Embeddable;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaItemId implements Serializable {
	@Column(name = "plato_id")
	Long platoId;
	@Column(name = "ingrediente_id")
	Long ingredienteId;
}
