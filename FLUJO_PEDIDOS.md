# Flujo Simplificado de Gestión de Pedidos

## Estados del Pedido
- **C** - CREADO: Pedido recién creado, se pueden agregar/modificar items
- **L** - LISTO: Pedido terminado y listo para entregar
- **E** - ENTREGADO: Pedido entregado al cliente (descuenta inventario)
- **A** - ANULADO: Pedido anulado/cancelado

## Flujo Principal (Simplificado)

### 1️⃣ CREAR PEDIDO CON ITEMS (Estado inicial: C - CREADO) ⭐

**Endpoint:** `POST /pedidos`

**Request (con items):**
```json
{
  "observaciones": "Mesa 5 - Sin picante",
  "items": [
    {
      "platoId": 5,
      "cantidad": 2
    },
    {
      "platoId": 8,
      "cantidad": 1
    },
    {
      "platoId": 12,
      "cantidad": 3
    }
  ]
}
```

**Response:**
```json
{
  "id": 123
}
```

**Request (sin items - pedido vacío):**
```json
{
  "observaciones": "Mesa 3"
}
```

**Notas:**
- ✅ **Recomendado:** Crear el pedido con todos los items en una sola llamada
- El pedido se crea en estado 'C' (CREADO)
- El backend calcula automáticamente precios con descuentos/promociones
- Los totales se calculan automáticamente
- Aún puedes agregar más items después si lo necesitas

---

### 2️⃣ AGREGAR ITEMS ADICIONALES (Opcional)

**Endpoint:** `POST /pedidos/{pedidoId}/items`

**Request:**
```json
{
  "platoId": 15,
  "cantidad": 1
}
```

**Response:**
```json
{
  "id": 456
}
```

**Notas:**
- Solo si necesitas agregar items después de crear el pedido
- Solo permitido si el pedido está en estado CREADO (C) o LISTO (L)
- No permitido si ya está ENTREGADO (E) o ANULADO (A)

---

### 3️⃣ AGREGAR EXTRAS A UN ITEM (Opcional)

**Endpoint:** `POST /pedidos/{pedidoId}/items/{itemId}/extras`

**Request:**
```json
{
  "pedidoItemId": 456,
  "ingredienteId": 10,
  "cantidad": 1
}
```

**Response:**
```json
{
  "id": 789
}
```

**Notas:**
- Solo ingredientes marcados como "extra" (`es_extra = 'S'`)
- El backend obtiene el precio automáticamente
- Los totales se recalculan incluyendo extras

---

### 4️⃣ CONSULTAR DETALLE DEL PEDIDO

**Endpoint:** `GET /pedidos/{pedidoId}`

**Response:**
```json
{
  "id": 123,
  "estado": "C",
  "totalBruto": 45.50,
  "totalExtras": 3.00,
  "totalNeto": 48.50,
  "observaciones": "Mesa 5 - Sin picante",
  "creadoEn": "2025-10-20 14:30:00",
  "actualizadoEn": "2025-10-20 14:35:00",
  "items": [
    {
      "id": 456,
      "platoId": 5,
      "cantidad": 2,
      "precioUnitario": 15.00,
      "subtotal": 30.00
    }
  ]
}
```

---

### 5️⃣ MARCAR PEDIDO COMO LISTO (C → L)

**Endpoint:** `POST /pedidos/{pedidoId}/marcar-listo`

**Response:** `200 OK`

**Notas:**
- Marca el pedido como terminado y listo para entregar
- Transición: CREADO (C) → LISTO (L)
- El pedido ya no se puede modificar después de esto

---

### 6️⃣ ENTREGAR PEDIDO (L → E) ⚠️ DESCUENTA INVENTARIO

**Endpoint:** `POST /pedidos/{pedidoId}/entregar`

**Response:** `200 OK`

**Notas Importantes:**
- ⚠️ **Este endpoint llama al SP `sp_pedido_cambiar_estado`** que:
  1. Valida que el pedido esté en estado LISTO (L)
  2. Valida que todos los platos tengan recetas cargadas
  3. **DESCUENTA EL INVENTARIO** según las recetas de cada plato
  4. Marca el pedido como ENTREGADO (E)
  5. Registra la fecha y usuario de entrega

**Validaciones previas (Backend):**
- El pedido debe estar en estado LISTO (L)
- Debe tener al menos un item
- Cada plato debe tener receta cargada
- Debe haber stock suficiente de ingredientes

**Errores posibles:**
- `"El pedido debe estar en estado LISTO para poder entregarse"`
- `"El pedido no tiene ítems"`
- `"El plato X no tiene receta cargada"`
- `"Stock insuficiente para entregar el pedido"` (del SP)

---

### 7️⃣ ANULAR PEDIDO (CUALQUIER ESTADO → A)

**Endpoint:** `POST /pedidos/{pedidoId}/anular`

**Response:** `200 OK`

**Notas:**
- Se puede anular en cualquier estado excepto ENTREGADO (E) o ya ANULADO (A)
- No revierte inventario (solo la entrega descuenta)

---

## Flujo Completo en el Frontend (Simplificado)

```javascript
// ========== EJEMPLO COMPLETO: Crear y gestionar un pedido ==========

async function crearPedidoSimplificado() {
  try {
    // 1. Crear pedido CON ITEMS en una sola llamada
    const pedidoResponse = await axios.post('/pedidos', {
      observaciones: 'Mesa 5 - Sin picante',
      items: [
        { platoId: 5, cantidad: 2 },   // 2 Hamburguesas
        { platoId: 8, cantidad: 1 },   // 1 Pizza
        { platoId: 12, cantidad: 3 }   // 3 Gaseosas
      ]
    });
    const pedidoId = pedidoResponse.data.id;
    console.log('✅ Pedido creado con items:', pedidoId);

    // 2. (Opcional) Agregar extras a un item
    const detalle = await axios.get(`/pedidos/${pedidoId}`);
    const hamburguesaItemId = detalle.data.items[0].id; // Primera hamburguesa

    await axios.post(`/pedidos/${pedidoId}/items/${hamburguesaItemId}/extras`, {
      pedidoItemId: hamburguesaItemId,
      ingredienteId: 10,  // Queso extra
      cantidad: 1
    });
    console.log('✅ Extra agregado');

    // 3. Consultar detalle actualizado
    const detalleActualizado = await axios.get(`/pedidos/${pedidoId}`);
    console.log('📋 Total a pagar:', detalleActualizado.data.totalNeto);

    // 4. Marcar como listo
    await axios.post(`/pedidos/${pedidoId}/marcar-listo`);
    console.log('✅ Pedido marcado como LISTO');

    // 5. Entregar pedido (descuenta inventario)
    await axios.post(`/pedidos/${pedidoId}/entregar`);
    console.log('🎉 Pedido ENTREGADO - Inventario actualizado');

  } catch (error) {
    console.error('❌ Error:', error.response?.data || error.message);
    
    // Si hay error, anular el pedido
    if (pedidoId) {
      await axios.post(`/pedidos/${pedidoId}/anular`);
      console.log('🚫 Pedido anulado');
    }
  }
}
```

---

## Gestión de Extras

### Listar extras de un item
**Endpoint:** `GET /pedidos/{pedidoId}/items/{itemId}/extras`

### Actualizar cantidad de un extra
**Endpoint:** `PUT /pedidos/{pedidoId}/extras/{extraId}`
```json
{ "cantidad": 3 }
```

### Eliminar un extra
**Endpoint:** `DELETE /pedidos/{pedidoId}/extras/{extraId}`

---

## Buscar Pedidos (Paginado)

**Endpoint:** `POST /pedidos/search`

**Request:**
```json
{
  "page": 0,
  "size": 10,
  "direction": "desc",
  "orderBy": "creadoEn",
  "filters": [
    {
      "llave": "estado",
      "operacion": "=",
      "valor": "L"
    }
  ]
}
```

**Operaciones de filtrado:**
- `:` - Contiene (like)
- `=` - Igual
- `>`, `<`, `>=`, `<=` - Comparaciones numéricas/fechas

**Ejemplos de filtros:**
```javascript
// Buscar pedidos LISTOS
filters: [{ llave: "estado", operacion: "=", valor: "L" }]

// Buscar pedidos de hoy
filters: [
  { llave: "creadoEn", operacion: ">=", valor: "2025-10-20 00:00:00" }
]

// Buscar por total mayor a $50
filters: [{ llave: "totalNeto", operacion: ">", valor: "50" }]
```

---

## Restricciones y Reglas de Negocio

### ✅ Permitido en cada estado:

| Acción | C (Creado) | L (Listo) | E (Entregado) | A (Anulado) |
|--------|------------|-----------|---------------|-------------|
| Agregar items | ✅ | ✅ | ❌ | ❌ |
| Agregar extras | ✅ | ✅ | ❌ | ❌ |
| Marcar listo | ✅ | - | ❌ | ❌ |
| Entregar | ❌ | ✅ | - | ❌ |
| Anular | ✅ | ✅ | ❌ | - |

### ⚠️ Puntos Críticos:

1. **Crear con items es más eficiente**: Una sola llamada HTTP vs múltiples
2. **La entrega descuenta inventario**: Solo llamar `/entregar` cuando realmente se entregue
3. **Cada plato necesita receta**: Asegurar recetas cargadas antes de entregar
4. **Stock insuficiente**: Manejar el error cuando no hay inventario

---

## Diagrama de Estados (Simplificado)

```
    ┌─────────┐
    │ CREADO  │ (C)
    │         │
    └────┬────┘
         │ 
         │ /marcar-listo
         ├──────────┐
         │          │ /anular
         ↓          ↓
    ┌─────────┐   ┌─────────┐
    │  LISTO  │──→│ ANULADO │ (A)
    │   (L)   │   │ (Final) │
    └────┬────┘   └─────────┘
         │
         │ /entregar (⚠️ DESCUENTA INVENTARIO)
         ↓
    ┌──────────┐
    │ENTREGADO │ (E)
    │ (Final)  │
    └──────────┘
```

---

## Endpoints Disponibles

### Gestión de Pedidos
- `POST /pedidos` - Crear pedido (con o sin items)
- `GET /pedidos/{id}` - Obtener detalle
- `POST /pedidos/{id}/items` - Agregar item
- `POST /pedidos/{id}/marcar-listo` - Marcar como LISTO
- `POST /pedidos/{id}/entregar` - Entregar (descuenta inventario)
- `POST /pedidos/{id}/anular` - Anular pedido
- `POST /pedidos/search` - Buscar con filtros

### Gestión de Extras
- `POST /pedidos/{pedidoId}/items/{itemId}/extras` - Agregar extra
- `GET /pedidos/{pedidoId}/items/{itemId}/extras` - Listar extras
- `PUT /pedidos/{pedidoId}/extras/{extraId}` - Actualizar cantidad
- `DELETE /pedidos/{pedidoId}/extras/{extraId}` - Eliminar extra

---

## Componentes Frontend Recomendados

### 1. Vista: Crear Pedido
- Lista de platos disponibles con precios
- Carrito de compras (agregar múltiples items)
- Botón "Crear Pedido" (crea con todos los items)
- Opción de agregar extras por item
- Botón "Marcar como Listo"

### 2. Vista: Pedidos Listos
- Lista de pedidos en estado L (LISTO)
- Botón "Entregar" con confirmación
- Mostrar totales y detalles
- Alerta de stock insuficiente si aplica

### 3. Vista: Historial
- Búsqueda con filtros (estado, fecha, totales)
- Paginación
- Ver detalle de cualquier pedido
- Filtro rápido por estado

---

## Diferencias Clave vs Flujo Anterior

| Aspecto | Anterior | Nuevo (Simplificado) |
|---------|----------|---------------------|
| Estados | 5 estados (C, P, L, E, X) | 4 estados (C, L, E, A) |
| Crear pedido | Vacío primero | Con items incluidos |
| Llamadas HTTP | 1 + N items | 1 llamada total |
| Transiciones | C→P→L→E | C→L→E |
| Cancelar | `/cancelar` | `/anular` |
| Estado cancelado | X | A |
| Cambiar estado | `/estado/{nuevo}` genérico | `/marcar-listo` específico |

---

**Última actualización:** 2025-10-20  
**Versión API:** 2.0.0 (Simplificada)  
**Ventajas:** Menos complejidad, menos llamadas HTTP, flujo más directo
