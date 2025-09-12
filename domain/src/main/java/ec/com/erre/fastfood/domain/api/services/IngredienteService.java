package ec.com.erre.fastfood.domain.api.services;

import ec.com.erre.fastfood.domain.api.models.api.GrupoIngrediente;
import ec.com.erre.fastfood.domain.api.models.api.Ingrediente;
import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import ec.com.erre.fastfood.domain.commons.exceptions.RegistroDuplicadoException;
import ec.com.erre.fastfood.domain.commons.exceptions.ReglaDeNegocioException;

import java.util.List;

/**
 * <b>Servicio de ingredientes </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public interface IngredienteService {

	List<Ingrediente> buscarTodos();

	Ingrediente buscarPorId(Long ingredienteId);

	List<Ingrediente> buscarPorGrupoId(Long grupoId) throws EntidadNoEncontradaException;

	Ingrediente buscarPorNombre(String nombre);

	void crear(Ingrediente ingrediente) throws RegistroDuplicadoException;

	void actualizar(Ingrediente ingrediente);
}