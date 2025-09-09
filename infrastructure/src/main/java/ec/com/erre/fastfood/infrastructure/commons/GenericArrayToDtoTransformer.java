package ec.com.erre.fastfood.infrastructure.commons;

import ec.com.erre.fastfood.domain.commons.ArrayIndex;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * <b> Clase Generica para transfomr de Object[] to DTO </b> Nota: para que este mapeo funcion tener en cuenta que los
 * campos en el dto deben estar en la misma posicion que el resultado del query
 *
 * @author jorge.reyes
 * @version $1.0$
 */
public class GenericArrayToDtoTransformer implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2026877440996415654L;

	public static <T> T transform(Object[] source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}

		try {
			// Obtener el constructor sin argumentos de la clase de destino
			Constructor<T> constructor = targetClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			T target = constructor.newInstance();

			// Obtener todos los campos de la clase de destino
			Field[] targetFields = targetClass.getDeclaredFields();

			for (Field targetField : targetFields) {
				ArrayIndex annotation = targetField.getAnnotation(ArrayIndex.class);
				if (annotation != null) {
					int index = annotation.value();
					if (index < source.length && source[index] != null) {
						targetField.setAccessible(true);
						if (targetField.getType().isAssignableFrom(source[index].getClass())) {
							targetField.set(target, source[index]);
						}
					}
				}
			}

			return target;
		} catch (Exception e) {
			throw new RuntimeException("Error al transformar objeto", e);
		}
	}

	public static <T> T transformMap(Map<String, Object> source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}

		try {
			// Obtener el constructor sin argumentos de la clase de destino
			Constructor<T> constructor = targetClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			T target = constructor.newInstance();

			// Obtener todos los campos de la clase de destino
			Field[] targetFields = targetClass.getDeclaredFields();

			for (Field targetField : targetFields) {
				targetField.setAccessible(true);

				// Obtener el nombre del campo en minúsculas para comparación
				String fieldName = targetField.getName();

				// Si el campo de destino está en el mapa, asignar el valor
				if (source.containsKey(fieldName)) {
					Object value = source.get(fieldName);

					// Si el campo de destino es del mismo tipo que el valor de la fuente, asignar el valor
					if (value != null && targetField.getType().isAssignableFrom(value.getClass())) {
						targetField.set(target, value);
					} else {
						// Manejo de tipos de datos si es necesario
						// Convertir el valor si es necesario antes de asignarlo
					}
				}
			}

			return target;
		} catch (Exception e) {
			throw new RuntimeException("Error al transformar objeto", e);
		}
	}

}
