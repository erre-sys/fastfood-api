/* ===========================
   FASTFOOD – Stored Procedures
   =========================== */
USE fastfood;

-- ----------------------------------------------------------
-- 1) Trigger para guardar el ingrediente en inventario
-- ----------------------------------------------------------
DROP TRIGGER IF EXISTS trg_ingredientes_ai;
DELIMITER $$
CREATE TRIGGER trg_ingredientes_ai
    AFTER INSERT ON ingredientes
    FOR EACH ROW
BEGIN
    INSERT INTO inventario(ingrediente_id, stock_actual, actualizado_en)
    VALUES (NEW.ingrediente_id, 0, NOW())
    ON DUPLICATE KEY UPDATE stock_actual = stock_actual; -- idempotente
END $$
DELIMITER ;

-- ----------------------------------------------------------
-- 1) Aplicar promociones programadas a la tabla PLATOS
--    Enciende/apaga bandera y setea descuento_pct (toma MAX
--    si hubiera traslape de promos para un mismo plato).
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_promos_aplicar;
DELIMITER $$
CREATE PROCEDURE sp_promos_aplicar(IN p_now DATETIME)
BEGIN
    CREATE TEMPORARY TABLE IF NOT EXISTS tmp_promos (
                                                        plato_id       BIGINT UNSIGNED PRIMARY KEY,
                                                        descuento_pct  DECIMAL(5,2) NOT NULL
    ) ENGINE=MEMORY;

    TRUNCATE TABLE tmp_promos;

    INSERT INTO tmp_promos(plato_id, descuento_pct)
    SELECT plato_id, MAX(descuento_pct)
    FROM promo_programada
    WHERE estado = 'A'
      AND p_now >= fecha_inicio
      AND p_now <  fecha_fin
    GROUP BY plato_id;

    -- Enciende/actualiza sólo lo necesario
    UPDATE platos p
        JOIN tmp_promos t ON t.plato_id = p.plato_id
    SET p.en_promocion = 'S',
        p.descuento_pct = t.descuento_pct
    WHERE p.en_promocion <> 'S'
       OR p.descuento_pct <> t.descuento_pct;

    -- Apaga a quienes ya no están en ventana
    UPDATE platos p
        LEFT JOIN tmp_promos t ON t.plato_id = p.plato_id
    SET p.en_promocion = 'N',
        p.descuento_pct = 0
    WHERE t.plato_id IS NULL
      AND (p.en_promocion = 'S' OR p.descuento_pct <> 0);

    DROP TEMPORARY TABLE IF EXISTS tmp_promos;
END $$
DELIMITER ;

-- ----------------------------------------------------------
-- 2) Ajuste manual de inventario
--    Aplica +/− cantidad, registra kardex (AJUSTE) y
--    evita saldo negativo salvo que p_permitir_neg = 'S'.
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_inventario_ajustar;
DELIMITER $$
CREATE PROCEDURE sp_inventario_ajustar(
    IN p_ingrediente_id BIGINT UNSIGNED,
    IN p_cantidad       DECIMAL(14,3),   -- +entrada / -salida
    IN p_referencia     VARCHAR(80),
    IN p_usuario_sub    VARCHAR(64),     -- reservado por si luego lo guardas
    IN p_permitir_neg   VARCHAR(1)       -- 'S'/'N'
)
BEGIN
    DECLARE v_stock DECIMAL(14,3);

    START TRANSACTION;

    -- Asegura fila snapshot (por si se creó ingrediente sin trigger)
    INSERT IGNORE INTO inventario(ingrediente_id, stock_actual)
    VALUES (p_ingrediente_id, 0);

    -- Lock de la fila
    SELECT stock_actual INTO v_stock
    FROM inventario
    WHERE ingrediente_id = p_ingrediente_id
        FOR UPDATE;

    IF v_stock IS NULL THEN
        ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ingrediente sin fila de inventario';
    END IF;

    IF COALESCE(p_permitir_neg,'N') <> 'S' AND (v_stock + p_cantidad) < 0 THEN
        ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente para ajustar';
    END IF;

    -- Actualiza snapshot (bypass para evitar guards si existieran)
    SET @bypass_inv = 1;
    UPDATE inventario
    SET stock_actual = stock_actual + p_cantidad
    WHERE ingrediente_id = p_ingrediente_id;
    SET @bypass_inv = NULL;

    -- Kardex
    INSERT INTO inventario_mov(
        ingrediente_id, fecha, tipo, cantidad, descuento_pct, referencia, compra_item_id, pedido_id
    ) VALUES (
                 p_ingrediente_id, NOW(), 'AJUSTE', p_cantidad, 0,
                 CONCAT('AJUSTE ', COALESCE(p_referencia,'')), NULL, NULL
             );

    COMMIT;
END $$
DELIMITER ;

-- ----------------------------------------------------------
-- 3) Cambiar estado de pedido (incluye ENTREGADO)
--    Al pasar a ENTREGADO:
--      - calcula consumo por ingrediente (receta + extras),
--      - valida stock,
--      - descuenta snapshot,
--      - registra kardex (CONSUMO) con % desc ponderado,
--      - marca pedido como ENTREGADO (con bypass de guard).
-- ----------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_pedido_cambiar_estado;
DELIMITER $$
CREATE PROCEDURE sp_pedido_cambiar_estado (
    IN p_pedido_id    BIGINT UNSIGNED,
    IN p_estado_nuevo VARCHAR(12),
    IN p_usuario_sub  VARCHAR(64)
)
BEGIN
    DECLARE v_estado_actual VARCHAR(1);
    DECLARE v_tiene_consumo INT DEFAULT 0;

    START TRANSACTION;

    -- Lock del pedido
    SELECT estado
    INTO v_estado_actual
    FROM pedidos
    WHERE pedido_id = p_pedido_id
        FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Pedido no existe';
    END IF;

    IF v_estado_actual IN ('C','E') THEN
        ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Pedido ya finalizado';
    END IF;

    IF p_estado_nuevo = 'E' THEN
        CREATE TEMPORARY TABLE IF NOT EXISTS tmp_consumo(
                                                            ingrediente_id     BIGINT UNSIGNED PRIMARY KEY,
                                                            cantidad_total     DECIMAL(14,3) NOT NULL,
                                                            descuento_pct_sum  DECIMAL(14,6) NOT NULL DEFAULT 0
        ) ENGINE=MEMORY;
        TRUNCATE TABLE tmp_consumo;

        -- Consumo por receta
        INSERT INTO tmp_consumo(ingrediente_id, cantidad_total, descuento_pct_sum)
        SELECT r.ingrediente_id,
               SUM(r.cantidad * pi.cantidad),
               SUM(r.cantidad * pi.cantidad * COALESCE(pi.descuento_pct,0))
        FROM pedido_item pi
                 JOIN receta_item r ON r.plato_id = pi.plato_id
        WHERE pi.pedido_id = p_pedido_id
        GROUP BY r.ingrediente_id
        ON DUPLICATE KEY UPDATE
                             cantidad_total    = cantidad_total    + VALUES(cantidad_total),
                             descuento_pct_sum = descuento_pct_sum + VALUES(descuento_pct_sum);

        -- Consumo por extras (sin % desc)
        INSERT INTO tmp_consumo(ingrediente_id, cantidad_total, descuento_pct_sum)
        SELECT pie.ingrediente_id, SUM(pie.cantidad), 0
        FROM pedido_item pi
                 JOIN pedido_item_extra pie ON pie.pedido_item_id = pi.pedido_item_id
        WHERE pi.pedido_id = p_pedido_id
        GROUP BY pie.ingrediente_id
        ON DUPLICATE KEY UPDATE
            cantidad_total = cantidad_total + VALUES(cantidad_total);

        -- ¿Hay algo que descontar?
        SELECT COUNT(*) > 0 INTO v_tiene_consumo FROM tmp_consumo;

        IF v_tiene_consumo = 1 THEN
            -- Asegura filas de inventario para todo lo involucrado
            INSERT IGNORE INTO inventario(ingrediente_id, stock_actual)
            SELECT ingrediente_id, 0 FROM tmp_consumo;

            -- Lock de inventario involucrado
            SELECT inv.ingrediente_id
            FROM inventario inv
                     JOIN tmp_consumo t USING(ingrediente_id)
                FOR UPDATE;

            -- Validación de stock
            IF EXISTS (
                SELECT 1
                FROM tmp_consumo t
                         LEFT JOIN inventario inv USING(ingrediente_id)
                WHERE COALESCE(inv.stock_actual,0) < t.cantidad_total
            ) THEN
                DROP TEMPORARY TABLE IF EXISTS tmp_consumo;
                ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente';
            END IF;

            -- Descuento de snapshot
            UPDATE inventario inv
                JOIN tmp_consumo t USING(ingrediente_id)
            SET inv.stock_actual = inv.stock_actual - t.cantidad_total;

            -- Kardex CONSUMO (negativo), %desc ponderado por cantidad
            INSERT INTO inventario_mov(ingrediente_id, fecha, tipo, cantidad, descuento_pct, referencia, pedido_id)
            SELECT t.ingrediente_id,
                   NOW(),
                   'CONSUMO',
                   -t.cantidad_total,
                   ROUND(CASE WHEN t.cantidad_total > 0
                                  THEN t.descuento_pct_sum / t.cantidad_total
                              ELSE 0 END, 2),
                   CONCAT('PED ', p_pedido_id),
                   p_pedido_id
            FROM tmp_consumo t;
        END IF;

        DROP TEMPORARY TABLE IF EXISTS tmp_consumo;

        -- Cambio de estado con bypass del trigger guard
        SET @bypass_entrega = 1;
        UPDATE pedidos
        SET estado = 'E',
            entregado_por_sub = p_usuario_sub,
            entregado_en = NOW(),
            actualizado_en = NOW()
        WHERE pedido_id = p_pedido_id;
        SET @bypass_entrega = NULL;

    ELSE
        -- Otros estados simples
        UPDATE pedidos
        SET estado = p_estado_nuevo,
            actualizado_en = NOW()
        WHERE pedido_id = p_pedido_id;
    END IF;

    COMMIT;
END $$
DELIMITER ;



--

    -- Versión (CHECKs requieren MySQL ≥ 8.0.16)
SELECT DATABASE() AS db, @@version AS mysql_version;

-- ========== Listados crudos ==========
-- SP
SELECT ROUTINE_NAME
FROM INFORMATION_SCHEMA.ROUTINES
WHERE ROUTINE_SCHEMA='fastfood' AND ROUTINE_TYPE='PROCEDURE'
ORDER BY ROUTINE_NAME;

-- Triggers
SELECT TRIGGER_NAME, EVENT_OBJECT_TABLE AS table_name, ACTION_TIMING, EVENT_MANIPULATION
FROM INFORMATION_SCHEMA.TRIGGERS
WHERE TRIGGER_SCHEMA='fastfood'
ORDER BY TRIGGER_NAME;

-- CHECK constraints
SELECT tc.TABLE_NAME, tc.CONSTRAINT_NAME, cc.CHECK_CLAUSE
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
         JOIN INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc
              ON cc.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
WHERE tc.CONSTRAINT_SCHEMA='fastfood' AND tc.CONSTRAINT_TYPE='CHECK'
ORDER BY tc.TABLE_NAME, tc.CONSTRAINT_NAME;

-- Únicos por columna que nos importan
SELECT TABLE_NAME, INDEX_NAME
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA='fastfood' AND NON_UNIQUE=0
  AND ( (TABLE_NAME='ingredientes' AND COLUMN_NAME='codigo')
    OR (TABLE_NAME='platos'       AND COLUMN_NAME='codigo')
    OR (TABLE_NAME='grupo_ingrediente' AND COLUMN_NAME='nombre')
    OR (TABLE_NAME='grupo_plato'       AND COLUMN_NAME='nombre') )
GROUP BY TABLE_NAME, INDEX_NAME
ORDER BY TABLE_NAME;

-- Eventos (scheduler)
SHOW EVENTS FROM fastfood;

-- ========== Resumen OK/FALTA ==========
WITH exp_trg AS (
    SELECT 'trg_ingredientes_ai' n UNION ALL
    SELECT 'trg_compra_item_ai'  UNION ALL
    SELECT 'trg_pedidos_bu_estado_guard'
),
     has_trg AS (
         SELECT TRIGGER_NAME n FROM INFORMATION_SCHEMA.TRIGGERS WHERE TRIGGER_SCHEMA='fastfood'
     ),
     exp_chk AS (
         SELECT 'ingredientes' tbl, 'chk_ing_extra' n UNION ALL
         SELECT 'ingredientes','chk_ing_extra_precio' UNION ALL
         SELECT 'platos','chk_pl_enprom' UNION ALL
         SELECT 'pedido_item','chk_pi_valores' UNION ALL
         SELECT 'pedidos','chk_ped_totales' UNION ALL
         SELECT 'inventario_mov','chk_inv_tipo'
     ),
     has_chk AS (
         SELECT tc.TABLE_NAME tbl, tc.CONSTRAINT_NAME n
         FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
         WHERE tc.CONSTRAINT_SCHEMA='fastfood' AND tc.CONSTRAINT_TYPE='CHECK'
     ),
     exp_uni AS (
         SELECT 'ingredientes' tbl, 'codigo' col UNION ALL
         SELECT 'platos','codigo' UNION ALL
         SELECT 'grupo_ingrediente','nombre' UNION ALL
         SELECT 'grupo_plato','nombre'
     ),
     has_uni AS (
         SELECT TABLE_NAME tbl, GROUP_CONCAT(DISTINCT COLUMN_NAME) cols
         FROM INFORMATION_SCHEMA.STATISTICS
         WHERE TABLE_SCHEMA='fastfood' AND NON_UNIQUE=0
         GROUP BY TABLE_NAME
     )
SELECT 'TRIGGER' tipo, e.n objeto, CASE WHEN h.n IS NULL THEN 'FALTA' ELSE 'OK' END estado
FROM exp_trg e LEFT JOIN has_trg h USING(n)
UNION ALL
SELECT 'CHECK', CONCAT(e.tbl,'.',e.n), CASE WHEN h.n IS NULL THEN 'FALTA' ELSE 'OK' END
FROM exp_chk e LEFT JOIN has_chk h ON h.tbl=e.tbl AND h.n=e.n
UNION ALL
SELECT 'UNIQUE', CONCAT(e.tbl,'(',e.col,')'),
       CASE WHEN h.cols IS NULL OR FIND_IN_SET(e.col, h.cols)=0 THEN 'FALTA' ELSE 'OK' END
FROM exp_uni e LEFT JOIN has_uni h ON h.tbl=e.tbl
ORDER BY tipo, objeto;

--

DROP TRIGGER IF EXISTS trg_compra_item_ai;
DELIMITER $$
CREATE TRIGGER trg_compra_item_ai
    AFTER INSERT ON compra_item
    FOR EACH ROW
BEGIN
    -- snapshot
    INSERT INTO inventario(ingrediente_id, stock_actual)
    VALUES (NEW.ingrediente_id, NEW.cantidad)
    ON DUPLICATE KEY UPDATE stock_actual = stock_actual + NEW.cantidad;
    -- kardex
    INSERT INTO inventario_mov(ingrediente_id, fecha, tipo, cantidad, descuento_pct, referencia, compra_item_id)
    VALUES (NEW.ingrediente_id, NOW(), 'COMPRA', NEW.cantidad, 0, CONCAT('COMPRA ', NEW.compra_id), NEW.compra_item_id);
END $$
DELIMITER ;

--

DROP TRIGGER IF EXISTS trg_pedidos_bu_estado_guard;
DELIMITER $$
CREATE TRIGGER trg_pedidos_bu_estado_guard
    BEFORE UPDATE ON pedidos
    FOR EACH ROW
BEGIN
    IF (NEW.estado = 'E' AND OLD.estado <> 'E') THEN
        IF (@bypass_entrega IS NULL OR @bypass_entrega <> 1) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usa sp_pedido_cambiar_estado para ENTREGAR';
        END IF;
    END IF;
END $$
DELIMITER ;

select * from inventario;
