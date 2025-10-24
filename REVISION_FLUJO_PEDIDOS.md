# Revisión Completa del Flujo de Pedidos

**Fecha de Revisión:** 2025-10-21
**Estado del Sistema:** Operativo con inconsistencias detectadas

---

## 🔍 Resumen Ejecutivo

He realizado una revisión exhaustiva del flujo de pedidos y he identificado **3 inconsistencias críticas** entre la implementación y la documentación, además de confirmar que el Stored Procedure `sp_pedido_cambiar_estado` está correctamente configurado para descontar inventario.

---

## ✅ Componentes Correctos

### 1. Stored Procedure: `sp_pedido_cambiar_estado` ✅

**Ubicación:** `.dev/mysql-init/sp.sql` (líneas 130-255)

**Funcionalidad Confirmada:**
- ✅ Valida que el pedido exista
- ✅ Valida que el pedido no esté finalizado antes de cambiar estado
- ✅ **Descuenta inventario automáticamente** cuando `p_estado_nuevo = 'E'`
- ✅ Calcula consumo por receta: `SUM(receta.cantidad * pedido_item.cantidad)`
- ✅ Calcula consumo por extras: `SUM(pedido_item_extra.cantidad)`
- ✅ Valida stock suficiente **antes** de descontar
- ✅ Actualiza tabla `inventario` (snapshot)
- ✅ Registra movimientos en `inventario_mov` (kardex)
- ✅ Marca pedido como ENTREGADO con fecha y usuario
- ✅ Maneja transacciones con rollback en caso de error

**Flujo del SP cuando estado = 'E':**
```sql
1. Lock del pedido (FOR UPDATE)
2. Validar estado actual (no debe estar en 'C' o 'E')
3. Crear tabla temporal tmp_consumo
4. Calcular consumo de ingredientes por receta
5. Agregar consumo de extras
6. Validar stock suficiente (ROLLBACK si insuficiente)
7. Descontar inventario (UPDATE inventario SET stock_actual = stock_actual - cantidad)
8. Insertar en kardex (inventario_mov)
9. Actualizar pedido a estado 'E' con bypass de trigger
10. COMMIT
```

**Mensajes de Error del SP:**
- `"Pedido no existe"` - Si no encuentra el pedido
- `"Pedido ya finalizado"` - Si estado actual es 'C' o 'E'
- `"Stock insuficiente"` - Si no hay inventario suficiente

---

### 2. Servicio de Entrega: `PedidosProcesoServiceImpl` ✅

**Ubicación:** `domain/src/main/java/ec/com/erre/fastfood/domain/api/services/PedidosProcesoServiceImpl.java`

**Método:** `entregar(Long pedidoId, String usuarioSub)` (líneas 44-126)

**Validaciones Implementadas:**
1. ✅ Verifica que el pedido existe
2. ✅ **Valida que el estado sea 'L' (LISTO)** antes de permitir entrega (línea 52)
3. ✅ Verifica que tenga ítems válidos con cantidad > 0
4. ✅ Valida que cada plato tenga receta cargada
5. ✅ Recalcula totales (bruto + extras) antes de entregar
6. ✅ **Llama al SP correctamente** (línea 108):
   ```java
   Query q = em.createNativeQuery("CALL fastfood.sp_pedido_cambiar_estado(:p_id, :p_estado, :p_sub)");
   q.setParameter("p_id", pedidoId);
   q.setParameter("p_estado", "E");  // ← ENTREGADO
   q.setParameter("p_sub", usuarioSub);
   q.executeUpdate();
   ```
7. ✅ Maneja excepciones del SP y las traduce a excepciones de dominio

**Manejo de Errores:**
```java
catch (RuntimeException ex) {
    String msg = deepestMessage(ex);
    if (msg.contains("stock insuficiente"))
        throw new ReglaDeNegocioException("Stock insuficiente para entregar el pedido");
    if (msg.contains("pedido ya finalizado"))
        throw new ReglaDeNegocioException("El pedido ya fue finalizado anteriormente");
    if (msg.contains("pedido no existe"))
        throw new EntidadNoEncontradaException("Pedido no existe");
}
```

---

### 3. Controlador: `PedidoController` ✅

**Ubicación:** `infrastructure/src/main/java/ec/com/erre/fastfood/infrastructure/api/in/rest/PedidoController.java`

**Endpoint de Entrega:** `POST /pedidos/{id}/entregar` (líneas 86-92)
```java
@PostMapping(value = "/{id}/entregar", produces = MediaType.APPLICATION_JSON_VALUE)
@Operation(summary = "Entregar pedido (descuenta inventario vía SP)")
public ResponseEntity<Void> entregar(@PathVariable Long id)
        throws EntidadNoEncontradaException, ReglaDeNegocioException, ServiceException {
    proceso.entregar(id, "USUARIO");  // ← Llama al servicio correcto
    return new ResponseEntity<>(HttpStatus.OK);
}
```

✅ **Correcto:** Llama a `PedidosProcesoService.entregar()` que ejecuta el SP

---

## ❌ Inconsistencias Detectadas

### **INCONSISTENCIA #1: Validación de Estado en SP vs Código** 🔴

**Problema:** El Stored Procedure y el código Java validan estados diferentes.

**En el SP** (línea 154):
```sql
IF v_estado_actual IN ('C','E') THEN
    ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Pedido ya finalizado';
END IF;
```
✅ **Lógica:** No permite entregar si el pedido está en estado 'C' (CREADO) o 'E' (ENTREGADO)

**En el código Java** - `PedidosProcesoServiceImpl.java` (línea 52):
```java
if (!"L".equalsIgnoreCase(pedido.getEstado())) {
    throw new ReglaDeNegocioException("El pedido debe estar en estado LISTO para poder entregarse");
}
```
✅ **Lógica:** Solo permite entregar si el estado es 'L' (LISTO)

**Estado:** ✅ **COHERENTES** - Ambas validaciones son correctas:
- El código Java valida positivamente (debe ser L)
- El SP valida negativamente (no debe ser C o E)
- Resultado: Solo se permite entregar pedidos en estado 'L' (LISTO)

---

### **INCONSISTENCIA #2: Estados Simplificados en Documentación vs SP** 🟡

**En la Documentación** (`FLUJO_PEDIDOS.md`):
```
Estados del Pedido:
- C - CREADO
- L - LISTO
- E - ENTREGADO
- A - ANULADO
```

**En el Stored Procedure** (línea 154):
```sql
IF v_estado_actual IN ('C','E') THEN
    ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Pedido ya finalizado';
END IF;
```

**Problema:** El SP considera 'C' (CREADO) como estado finalizado, lo cual es **INCORRECTO** según la documentación.

**Estado Real Según el Código:**
- Los estados finales son: **'E' (ENTREGADO)** y **'A' (ANULADO)**
- 'C' (CREADO) NO es un estado final

**Impacto:** 🔴 **CRÍTICO**
- El SP **bloqueará** la entrega de pedidos si intentamos cambiar de 'C' → 'E'
- Esto contradice el flujo simplificado donde se debe pasar por: C → L → E

**Evidencia en el código Java** - `PedidoGestionServiceImpl.java` (línea 139):
```java
// Estados finales: no se pueden cambiar
if ("E".equalsIgnoreCase(estadoActual) || "A".equalsIgnoreCase(estadoActual)) {
    throw new ReglaDeNegocioException("Pedido finalizado: no se puede cambiar estado");
}
```

**Conclusión:** El SP tiene un **BUG** en la validación de estados finales.

**Corrección Requerida:**
```sql
-- ANTES (INCORRECTO):
IF v_estado_actual IN ('C','E') THEN

-- DESPUÉS (CORRECTO):
IF v_estado_actual IN ('E','A') THEN
```

---

### **INCONSISTENCIA #3: Transición de Estados en `cambiarEstado()`** 🟡

**Ubicación:** `PedidoGestionServiceImpl.java` (líneas 143-146)

```java
// Solo permitir C -> L (CREADO -> LISTO) || L -> E (ENTREGADO -> PAGADO)
boolean ok = ("C".equalsIgnoreCase(estadoActual) && "L".equals(estadoNuevo))
        || ("E".equalsIgnoreCase(estadoActual) && "P".equals(estadoNuevo))
        || estadoActual.equalsIgnoreCase(estadoNuevo); // Idempotencia
```

**Problemas Detectados:**

1. **Comentario contradictorio con el código:**
   - Comentario dice: `L -> E (ENTREGADO -> PAGADO)`
   - Código valida: `E -> P`
   - ❌ **INCOHERENTE**: Si 'E' es ENTREGADO, ¿por qué permitir E → P?

2. **Estado 'P' no documentado:**
   - La documentación solo menciona 4 estados: C, L, E, A
   - El código permite transición a estado 'P' (¿PAGADO?)
   - ❌ **INCONSISTENCIA**: Estado 'P' no existe en la documentación

3. **Mensaje de error incompleto (línea 150):**
   ```java
   throw new ReglaDeNegocioException(
       "Transición inválida (" + estadoActual + " → " + estadoNuevo
       + "). Solo se permite CREADO → LISTO");
   ```
   - El mensaje solo menciona C → L
   - Pero el código también permite E → P
   - ❌ **CONFUSO** para el usuario

**Análisis:**

Según la documentación simplificada (`FLUJO_PEDIDOS.md`), **NO debería existir** transición E → P porque:
- El endpoint `/entregar` es el que cambia L → E (vía SP)
- No existe endpoint para "marcar como pagado"
- Los estados finales son E y A

**Conclusión:** Esta validación parece ser **código legacy** del flujo anterior con 5 estados.

**Corrección Requerida:**
```java
// Solo permitir C -> L (CREADO -> LISTO)
boolean ok = ("C".equalsIgnoreCase(estadoActual) && "L".equals(estadoNuevo))
        || estadoActual.equalsIgnoreCase(estadoNuevo); // Idempotencia

if (!ok) {
    throw new ReglaDeNegocioException(
        "Transición inválida (" + estadoActual + " → " + estadoNuevo
        + "). Solo se permite CREADO → LISTO");
}
```

---

### **INCONSISTENCIA #4: Repositorio permite estado 'P' en `cambiarEstadoSimple()`** 🟡

**Ubicación:** `PedidoRepositoryImpl.java` (línea 75)

```java
@Override
public boolean cambiarEstadoSimple(Long pedidoId, String nuevoEstado) {
    long upd = getQueryFactory().update(pedidoEntity)
            .where(pedidoEntity.id.eq(pedidoId), pedidoEntity.estado.in("C", "P", "L"))
            .set(pedidoEntity.estado, nuevoEstado)
            .set(pedidoEntity.actualizadoEn, java.time.LocalDateTime.now())
            .execute();
    return upd > 0;
}
```

**Problema:**
- Permite cambiar estado si el pedido está en: 'C', 'P', o 'L'
- Estado 'P' **NO está documentado** en el flujo simplificado
- ❌ **INCONSISTENCIA**: Código legacy del sistema de 5 estados

**Corrección Requerida:**
```java
.where(pedidoEntity.id.eq(pedidoId), pedidoEntity.estado.in("C", "L"))
```

---

## 📊 Matriz de Validación de Flujo

| Componente | Validación | Estado | Observaciones |
|------------|-----------|--------|---------------|
| **SP: `sp_pedido_cambiar_estado`** | ❌ Estados finales incorrectos | 🔴 BUG | Valida ('C','E') debe ser ('E','A') |
| **SP: Descuento de inventario** | ✅ Funciona correctamente | ✅ OK | Calcula receta + extras |
| **SP: Validación de stock** | ✅ Funciona correctamente | ✅ OK | Rollback si insuficiente |
| **Service: `entregar()`** | ✅ Valida estado = 'L' | ✅ OK | Correcto |
| **Service: Llamada al SP** | ✅ Parámetros correctos | ✅ OK | Estado 'E' |
| **Controller: Endpoint `/entregar`** | ✅ Llama servicio correcto | ✅ OK | `proceso.entregar()` |
| **Service: `cambiarEstado()`** | ⚠️ Permite E → P | 🟡 LEGACY | Código antiguo |
| **Repository: `cambiarEstadoSimple()`** | ⚠️ Permite estado 'P' | 🟡 LEGACY | Código antiguo |
| **Documentación: Estados** | ✅ 4 estados (C,L,E,A) | ✅ OK | Simplificado |

---

## 🔧 Correcciones Recomendadas

### **CRÍTICO - Debe corregirse:**

#### 1. **Stored Procedure: Corregir validación de estados finales**

**Archivo:** `.dev/mysql-init/sp.sql` (línea 154)

**ANTES:**
```sql
IF v_estado_actual IN ('C','E') THEN
    ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Pedido ya finalizado';
END IF;
```

**DESPUÉS:**
```sql
IF v_estado_actual IN ('E','A') THEN
    ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Pedido ya finalizado';
END IF;
```

**Impacto:** Sin esta corrección, el SP **bloqueará** entregas legítimas.

---

### **RECOMENDADO - Código legacy:**

#### 2. **Service: Eliminar transición E → P**

**Archivo:** `domain/src/main/java/ec/com/erre/fastfood/domain/api/services/PedidoGestionServiceImpl.java` (líneas 143-150)

**ANTES:**
```java
// Solo permitir C -> L (CREADO -> LISTO) || L -> E (ENTREGADO -> PAGADO)
boolean ok = ("C".equalsIgnoreCase(estadoActual) && "L".equals(estadoNuevo))
        || ("E".equalsIgnoreCase(estadoActual) && "P".equals(estadoNuevo))
        || estadoActual.equalsIgnoreCase(estadoNuevo);

if (!ok) {
    throw new ReglaDeNegocioException(
        "Transición inválida (" + estadoActual + " → " + estadoNuevo
        + "). Solo se permite CREADO → LISTO");
}
```

**DESPUÉS:**
```java
// Solo permitir C -> L (CREADO -> LISTO)
boolean ok = ("C".equalsIgnoreCase(estadoActual) && "L".equals(estadoNuevo))
        || estadoActual.equalsIgnoreCase(estadoNuevo); // Idempotencia

if (!ok) {
    throw new ReglaDeNegocioException(
        "Transición inválida (" + estadoActual + " → " + estadoNuevo
        + "). Solo se permite CREADO → LISTO");
}
```

---

#### 3. **Repository: Eliminar estado 'P' de `cambiarEstadoSimple()`**

**Archivo:** `infrastructure/src/main/java/ec/com/erre/fastfood/infrastructure/api/out/db/PedidoRepositoryImpl.java` (línea 75)

**ANTES:**
```java
.where(pedidoEntity.id.eq(pedidoId), pedidoEntity.estado.in("C", "P", "L"))
```

**DESPUÉS:**
```java
.where(pedidoEntity.id.eq(pedidoId), pedidoEntity.estado.in("C", "L"))
```

---

## 🎯 Flujo Correcto (Post-Corrección)

```
┌─────────────────────────────────────────────────────────────┐
│                    FLUJO DE PEDIDOS                         │
└─────────────────────────────────────────────────────────────┘

1️⃣ CREAR PEDIDO (con items)
   POST /pedidos
   ↓
   Estado: C (CREADO)
   Inventario: Sin cambios

2️⃣ (Opcional) AGREGAR ITEMS/EXTRAS
   POST /pedidos/{id}/items
   POST /pedidos/{id}/items/{itemId}/extras
   ↓
   Estado: C (CREADO)
   Inventario: Sin cambios

3️⃣ MARCAR COMO LISTO
   POST /pedidos/{id}/marcar-listo
   ↓
   Estado: L (LISTO)
   Inventario: Sin cambios
   Validación: cambiarEstado() permite C → L ✅

4️⃣ ENTREGAR PEDIDO
   POST /pedidos/{id}/entregar
   ↓
   PedidosProcesoService.entregar()
     ├─ Valida estado = 'L' ✅
     ├─ Valida items y recetas ✅
     ├─ Recalcula totales ✅
     └─ Llama SP: sp_pedido_cambiar_estado(id, 'E', usuario)
         ├─ SP valida estado no in ('E','A') ✅ [DESPUÉS DE CORRECCIÓN]
         ├─ Calcula consumo (receta + extras) ✅
         ├─ Valida stock suficiente ✅
         ├─ Descuenta inventario ✅
         ├─ Registra kardex ✅
         └─ Marca pedido como ENTREGADO ✅
   ↓
   Estado: E (ENTREGADO)
   Inventario: DESCONTADO ✅

─── O ───

🚫 ANULAR PEDIDO (desde cualquier estado excepto E o A)
   POST /pedidos/{id}/anular
   ↓
   Estado: A (ANULADO)
   Inventario: Sin cambios (la anulación NO revierte inventario)
```

---

## 📝 Conclusiones

### ✅ **Aspectos Positivos:**
1. El Stored Procedure `sp_pedido_cambiar_estado` está **correctamente implementado** para descontar inventario
2. El servicio `PedidosProcesoServiceImpl.entregar()` valida correctamente el estado 'L' antes de llamar al SP
3. El endpoint `/entregar` llama al servicio correcto
4. La documentación `FLUJO_PEDIDOS.md` está bien estructurada

### ❌ **Problemas Detectados:**
1. **CRÍTICO:** El SP valida estados finales incorrectos ('C','E') debe ser ('E','A')
2. **LEGACY:** Código permite transiciones a estado 'P' que no existe en el flujo simplificado
3. **LEGACY:** Repositorio permite estado 'P' en query de actualización

### 🔧 **Acciones Requeridas:**
1. **URGENTE:** Corregir validación en SP (línea 154 de sp.sql)
2. **RECOMENDADO:** Eliminar código legacy relacionado con estado 'P'
3. **VERIFICAR:** Ejecutar tests end-to-end después de las correcciones

---

## 🧪 Plan de Pruebas Post-Corrección

### Caso 1: Flujo Completo Exitoso
```sql
1. Crear pedido con items → Estado C
2. Marcar como listo → Estado L (cambiarEstado permite C→L ✅)
3. Entregar → Estado E (SP permite L→E ✅, descuenta inventario ✅)
```

### Caso 2: Intentar Entregar sin Marcar Listo
```sql
1. Crear pedido con items → Estado C
2. Intentar entregar directamente → ❌ Error: "El pedido debe estar en estado LISTO"
```

### Caso 3: Stock Insuficiente
```sql
1. Crear pedido con items → Estado C
2. Marcar como listo → Estado L
3. Entregar → ❌ Error del SP: "Stock insuficiente"
   - Pedido permanece en estado L
   - Inventario no se modifica
```

### Caso 4: Anular Pedido
```sql
1. Crear pedido → Estado C
2. Anular → Estado A
3. Intentar entregar → ❌ Error: "Pedido finalizado"
```

---

**Fin del Documento**
