package ec.com.erre.fastfood.infrastructure.commons;

import java.lang.reflect.Field;
import java.util.List;

/**
 * <b> Clase para generar los campos upper case </b>
 *
 * @author eduardo.romero
 * @version $1.0$
 */
public class UpperCaseTransformer {

	private UpperCaseTransformer() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Transforma todas las propiedades de tipo String de un objeto a mayúsculas.
	 *
	 * @param object el objeto a transformar
	 */
	public static void transformToUpperCase(Object object) {
		if (object == null) {
			return;
		}

		// Obtenemos la clase del objeto
		Class<?> clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields(); // Campos declarados en la clase

		for (Field field : fields) {
			// Verificamos si el tipo del campo es String
			if (field.getType() == String.class) {
				field.setAccessible(true); // Permitimos el acceso a campos privados
				try {
					// Obtenemos el valor del campo
					String value = (String) field.get(object);
					if (value != null) {
						// Transformamos el valor a mayúsculas y lo volvemos a asignar
						field.set(object, value.toUpperCase());
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Error al procesar el campo: " + field.getName(), e);
				}
			}
		}
	}

	/**
	 * Transforma todas las propiedades de tipo String de un arreglo de objetos a mayúsculas.
	 *
	 * @param objects el arreglo de objetos a transformar
	 */
	public static void transformArrayToUpperCase(List<?> objects) {
		if (objects == null || objects.isEmpty()) {
			return;
		}

		for (Object object : objects) {
			transformToUpperCase(object);
		}
	}
}
