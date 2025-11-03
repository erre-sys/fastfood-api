package ec.com.erre.fastfood.domain.api.services;

import java.math.BigDecimal;

/**
 * Constantes compartidas para servicios de pedidos. Centraliza valores literales para facilitar mantenimiento y evitar
 * magic strings/numbers.
 */
public final class PedidoConstants {

	// ===== Estados de Pedido =====
	public static final String ESTADO_CREADO = "C";
	public static final String ESTADO_LISTO = "L";
	public static final String ESTADO_ENTREGADO = "E";
	public static final String ESTADO_ANULADO = "A";

	// ===== Estados de Entidad =====
	public static final String ESTADO_ACTIVO = "A";
	public static final String ESTADO_INACTIVO = "I";

	// ===== Indicadores =====
	public static final String INDICADOR_SI = "S";
	public static final String INDICADOR_NO = "N";

	// ===== Valores Numéricos =====
	public static final BigDecimal DESCUENTO_PORCENTAJE_MAX = new BigDecimal("100");
	public static final int ESCALA_PRECIOS = 2;
	public static final int ESCALA_CANTIDADES = 3;
	public static final int ESCALA_DIVISION = 4;

	// ===== Stored Procedures =====
	public static final String SP_PEDIDO_CAMBIAR_ESTADO = "CALL fastfood.sp_pedido_cambiar_estado(:p_id, :p_estado, :p_sub)";

	// ===== Mensajes de Error - Pedido =====
	public static final String MSG_PEDIDO_FINALIZADO = "Pedido finalizado: no se puede modificar";
	public static final String MSG_PEDIDO_YA_FINALIZADO = "El pedido ya está finalizado";
	public static final String MSG_PEDIDO_NO_SE_PUDO_ACTUALIZAR = "No fue posible actualizar el estado";
	public static final String MSG_PEDIDO_NO_SE_PUDO_ANULAR = "No fue posible anular el pedido";
	public static final String MSG_PEDIDO_ESTADO_LISTO_REQUERIDO = "El pedido debe estar en estado LISTO para poder entregarse";
	public static final String MSG_PEDIDO_SIN_ITEMS = "El pedido no tiene ítems";
	public static final String MSG_PEDIDO_STOCK_INSUFICIENTE = "No hay suficiente stock para completar el pedido";
	public static final String MSG_PEDIDO_NO_EXISTE = "Pedido no existe";

	// ===== Mensajes de Error - Items =====
	public static final String MSG_PLATO_ID_OBLIGATORIO = "platoId es obligatorio";
	public static final String MSG_CANTIDAD_MAYOR_CERO = "La cantidad debe ser > 0";
	public static final String MSG_PLATO_INACTIVO = "El plato está inactivo";
	public static final String MSG_ITEMS_CANTIDAD_MAYOR_CERO = "Todos los ítems deben tener cantidad > 0";
	public static final String MSG_ITEMS_SUBTOTAL_NEGATIVO = "Hay ítems con subtotal negativo";

	// ===== Mensajes de Error - Extras =====
	public static final String MSG_EXTRAS_CANTIDAD_INVALIDA = "Hay extras con cantidad inválida en el ítem ";
	public static final String MSG_EXTRAS_PRECIO_INVALIDO = "Hay extras con precio inválido en el ítem ";

	// ===== Mensajes de Error - Receta =====
	public static final String MSG_PLATO_SIN_RECETA = "El plato %d no tiene receta cargada";

	// ===== Mensajes de Error - Transiciones de Estado =====
	public static final String MSG_TRANSICION_INVALIDA = "Transición inválida (%s → %s). Solo se permite CREADO → LISTO";

	// ===== Mensajes de Excepción del SP =====
	public static final String SP_ERROR_STOCK_INSUFICIENTE = "stock insuficiente";
	public static final String SP_ERROR_PEDIDO_FINALIZADO = "pedido ya finalizado";
	public static final String SP_ERROR_PEDIDO_NO_EXISTE = "pedido no existe";

	private PedidoConstants() {
		// Clase de utilidad, no debe ser instanciada
		throw new AssertionError("No se debe instanciar PedidoConstants");
	}
}
