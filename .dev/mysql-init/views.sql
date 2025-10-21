USE fastfood;

-- 1) Detalle: platos + receta + ingredientes (con precio vigente)
CREATE OR REPLACE VIEW v_plato_receta_detalle AS
SELECT
    p.plato_id,
    p.codigo              AS plato_codigo,
    p.nombre              AS plato_nombre,
    gp.nombre             AS grupo_plato,
    p.precio_base,
    p.en_promocion,
    p.descuento_pct,
    ROUND(
            CASE WHEN p.en_promocion = 'S'
                     THEN p.precio_base * (1 - p.descuento_pct/100)
                 ELSE p.precio_base
                END, 2
        )                     AS precio_vigente,
    i.ingrediente_id,
    i.codigo              AS ing_codigo,
    i.nombre              AS ing_nombre,
    i.unidad              AS ing_unidad,         -- gr | u | ml
    i.es_extra,                                  -- S/N (apto para extras)
    i.precio_extra,
    r.cantidad            AS receta_cantidad,
    inv.stock_actual      AS stock_actual
FROM platos p
         JOIN grupo_plato gp     ON gp.grupo_plato_id = p.grupo_plato_id
         JOIN receta_item r      ON r.plato_id = p.plato_id
         JOIN ingredientes i     ON i.ingrediente_id = r.ingrediente_id
         LEFT JOIN inventario inv ON inv.ingrediente_id = i.ingrediente_id
ORDER BY p.codigo, i.nombre;

-- 2) Resumen por plato: cuántos ingredientes lleva y si tiene promo activa
CREATE OR REPLACE VIEW v_plato_receta_resumen AS
SELECT
    p.plato_id,
    p.codigo        AS plato_codigo,
    p.nombre        AS plato_nombre,
    gp.nombre       AS grupo_plato,
    p.precio_base,
    p.en_promocion,
    p.descuento_pct,
    ROUND(
            CASE WHEN p.en_promocion = 'S'
                     THEN p.precio_base * (1 - p.descuento_pct/100)
                 ELSE p.precio_base
                END, 2
        )               AS precio_vigente,
    COUNT(r.ingrediente_id)                       AS ingredientes_total,
    SUM(CASE WHEN i.es_extra = 'S' THEN 1 ELSE 0 END) AS ingredientes_marcados_extra,
    SUM(CASE WHEN inv.stock_actual IS NULL THEN 1 ELSE 0 END) AS ingredientes_sin_snapshot
FROM platos p
         JOIN grupo_plato gp ON gp.grupo_plato_id = p.grupo_plato_id
         LEFT JOIN receta_item r ON r.plato_id = p.plato_id
         LEFT JOIN ingredientes i ON i.ingrediente_id = r.ingrediente_id
         LEFT JOIN inventario inv ON inv.ingrediente_id = i.ingrediente_id
GROUP BY p.plato_id, p.codigo, p.nombre, gp.nombre, p.precio_base, p.en_promocion, p.descuento_pct
ORDER BY p.codigo;

-- 3) Healthcheck: debe quedar todo en 0 para “perfecto”
CREATE OR REPLACE VIEW v_fastfood_healthcheck AS
SELECT 'platos_sin_receta' AS check_name,
       COUNT(*) AS n
FROM platos p
         LEFT JOIN receta_item r ON r.plato_id = p.plato_id
WHERE r.plato_id IS NULL

UNION ALL
SELECT 'ingredientes_sin_snapshot',
       COUNT(*)
FROM ingredientes i
         LEFT JOIN inventario inv ON inv.ingrediente_id = i.ingrediente_id
WHERE inv.ingrediente_id IS NULL

UNION ALL
SELECT 'recetas_con_cantidad_invalida',
       COUNT(*)
FROM receta_item
WHERE cantidad <= 0

UNION ALL
SELECT 'ingredientes_inactivos_en_receta',
       COUNT(*)
FROM receta_item r
         JOIN ingredientes i ON i.ingrediente_id = r.ingrediente_id
WHERE i.activo <> 'S'

UNION ALL
SELECT 'platos_inactivos_con_receta',
       COUNT(*)
FROM platos p
         JOIN receta_item r ON r.plato_id = p.plato_id
WHERE p.estado <> 'A'

UNION ALL
SELECT 'duplicados_en_receta_mismo_plato',
       COUNT(*) FROM (
                         SELECT plato_id, ingrediente_id, COUNT(*) c
                         FROM receta_item
                         GROUP BY plato_id, ingrediente_id
                         HAVING COUNT(*) > 1
                     ) t

UNION ALL
SELECT 'ingredientes_sin_uso_en_receta',
       COUNT(*)
FROM ingredientes i
         LEFT JOIN receta_item r ON r.ingrediente_id = i.ingrediente_id
WHERE r.ingrediente_id IS NULL;

-- (Opcional) Vista de platos sin receta (para inspección directa)
CREATE OR REPLACE VIEW v_platos_sin_receta AS
SELECT p.*
FROM platos p
         LEFT JOIN receta_item r ON r.plato_id = p.plato_id
WHERE r.plato_id IS NULL;


-- Ejecucion de vistas
SELECT * FROM v_plato_receta_resumen;

SELECT * FROM v_fastfood_healthcheck;

SELECT * FROM v_platos_sin_receta;