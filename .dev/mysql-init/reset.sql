USE fastfood;

-- =========================
-- 0) Limpieza de datos
-- =========================
SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE pedido_item_extra;
TRUNCATE TABLE pedido_item;
TRUNCATE TABLE pedidos;

TRUNCATE TABLE compra_item;
TRUNCATE TABLE compra;

TRUNCATE TABLE pago_cliente;
TRUNCATE TABLE pago_proveedor;

TRUNCATE TABLE inventario_mov;
TRUNCATE TABLE receta_item;

TRUNCATE TABLE platos;
TRUNCATE TABLE grupo_plato;

TRUNCATE TABLE inventario;
TRUNCATE TABLE ingredientes;

TRUNCATE TABLE proveedores;          -- aún no los usarás
TRUNCATE TABLE promo_programada;

TRUNCATE TABLE grupo_ingrediente;

SET FOREIGN_KEY_CHECKS=1;

-- =========================
-- 1) Catálogo: grupos de ingredientes
-- =========================
INSERT INTO grupo_ingrediente (nombre, estado) VALUES
                                                   ('Vegetales','A'),
                                                   ('Embutidos','A'),
                                                   ('Carnes','A'),
                                                   ('Lácteos','A'),
                                                   ('Panes','A'),
                                                   ('Bebidas','A'),
                                                   ('Granos','A'),
                                                   ('Frutas','A'),
                                                   ('Postres','A');

-- =========================
-- 2) Ingredientes
--     (el trigger debe crear la fila en inventario=0)
-- =========================
INSERT INTO ingredientes (codigo,nombre,grupo_ingrediente_id,unidad,es_extra,precio_extra,stock_minimo,activo)
SELECT 'PAPAS','Papas',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Vegetales'),
       'gr','N',NULL,500,'S'
UNION ALL
SELECT 'SALCHICHA','Salchicha',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Embutidos'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'TOCINO','Tocino',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Carnes'),
       'gr','N',NULL,100,'S'
UNION ALL
SELECT 'QUESO_CHEDAR','Queso Chedar',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Lácteos'),
       'gr','N',NULL,100,'S'
UNION ALL
SELECT 'POLLO','Pollo',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Carnes'),
       'gr','S',0.50,200,'S'
UNION ALL
SELECT 'CHORIZO','Chorizo',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Embutidos'),
       'u','S',0.35,10,'S'
UNION ALL
SELECT 'PAN_HOT_DOG','Pan Hot Dog',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Panes'),
       'u','N',NULL,20,'S'
UNION ALL
SELECT 'COLA','Cola',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Bebidas'),
       'u','S',0.30,10,'S'
UNION ALL
SELECT 'PAN_BRIOCHE','Pan Brioche',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Panes'),
       'u','N',NULL,20,'S'
UNION ALL
SELECT 'CARNE_HAMB','Carne de Hamburguesa',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Carnes'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'JAMON','Jamón',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Carnes'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'HUEVO','Huevo',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Carnes'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'ARROZ','Arroz',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Granos'),
       'gr','N',NULL,1000,'S'
UNION ALL
SELECT 'ARROZ_RELLENO','Arroz Relleno',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Granos'),
       'gr','N',NULL,1000,'S'
UNION ALL
SELECT 'AGUA_AROM','Agua Arómatica',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Bebidas'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'CAFE_PASADO','Café Pasado',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Bebidas'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'CAFE_LECHE','Café con Leche',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Bebidas'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'CHOCOLATE_BEB','Chocolate (bebida)',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Bebidas'),
       'u','S',0.50,10,'S'
UNION ALL
SELECT 'FRUTA','Fruta',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Frutas'),
       'gr','S',0.75,1000,'S'
UNION ALL
SELECT 'LECHE','Leche',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Lácteos'),
       'ml','N',NULL,1000,'S'
UNION ALL
SELECT 'FUZE_TEA','Fuze Tea',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Bebidas'),
       'u','S',0.50,10,'S';

-- Backfill de inventario por si no se creó por trigger (idempotente)
INSERT INTO inventario(ingrediente_id, stock_actual)
SELECT i.ingrediente_id, 0
FROM ingredientes i
         LEFT JOIN inventario inv USING (ingrediente_id)
WHERE inv.ingrediente_id IS NULL;

-- =========================
-- 3) Grupos de platos
-- =========================
INSERT INTO grupo_plato (nombre) VALUES
                                     ('Combos'),
                                     ('Hot Dogs'),
                                     ('Hamburguesas'),
                                     ('Platos'),
                                     ('Bebidas'),
                                     ('Cafetería');

-- =========================
-- 4) Platos (precio_base, activo S, sin promo)
-- =========================
INSERT INTO platos (codigo, nombre, grupo_plato_id, precio_base, estado, en_promocion, descuento_pct)
SELECT 'SALCHIPAPA','Salchipapa',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Combos'), 1.25, 'A','N',0
UNION ALL
SELECT 'SALCHI_ESTUD','Salchi Estudiantil',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Combos'), 1.00, 'A','N',0
UNION ALL
SELECT 'PAPA_CASA','Papa de la Casa',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Combos'), 2.00, 'A','N',0
UNION ALL
SELECT 'PAPI_POLLO','Papi Pollo',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Combos'), 2.00, 'A','N',0
UNION ALL
SELECT 'POLLO_LOCO','Pollo Loco',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Combos'), 2.00, 'A','N',0
UNION ALL
SELECT 'CHORI_POLLO','Chori Pollo',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Combos'), 2.00, 'A','N',0
UNION ALL
SELECT 'CHORI_PAPA','Chori Papa',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Combos'), 2.00, 'A','N',0
UNION ALL
SELECT 'PERRO_CAL','Perro Caliente',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Hot Dogs'), 1.50, 'A','N',0
UNION ALL
SELECT 'HB_CLASICA','Hamburguesa Clasica',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Hamburguesas'), 2.00, 'A','N',0
UNION ALL
SELECT 'HB_SUPREMA','Hamburguesa Suprema',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Hamburguesas'), 3.50, 'A','N',0
UNION ALL
SELECT 'SECO_POLLO','Seco de Pollo',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Platos'), 2.50, 'A','N',0
UNION ALL
SELECT 'ARROZ_RELLENO_PL','Arroz Relleno',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Platos'), 1.00, 'A','N',0
UNION ALL
SELECT 'AGUA_AROM_BEB','Agua Arómatica',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Cafetería'), 0.50, 'A','N',0
UNION ALL
SELECT 'CAFE_PASADO_BEB','Café Pasado',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Cafetería'), 0.75, 'A','N',0
UNION ALL
SELECT 'CAFE_LECHE_BEB','Café con Leche',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Cafetería'), 1.00, 'A','N',0
UNION ALL
SELECT 'CHOCOLATE_BEB','Chocolate',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Cafetería'), 1.25, 'A','N',0
UNION ALL
SELECT 'JUGO_NAT','Jugo Natural',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Bebidas'), 0.75, 'A','N',0
UNION ALL
SELECT 'BATIDO_FRUTA','Batido de Fruta',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Bebidas'), 1.25, 'A','N',0
UNION ALL
SELECT 'COLAS_PL','Colas',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Bebidas'), 0.50, 'A','N',0
UNION ALL
SELECT 'FUZE_TEA_PL','Fuze Tea',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Bebidas'), 0.50, 'A','N',0;

-- =========================
-- 5) Recetas (cantidades por unidad de plato)
-- =========================
-- Helper: obtener IDs
SET @ID_PAPAS       := (SELECT ingrediente_id FROM ingredientes WHERE codigo='PAPAS');
SET @ID_SALCHICHA   := (SELECT ingrediente_id FROM ingredientes WHERE codigo='SALCHICHA');
SET @ID_TOCINO      := (SELECT ingrediente_id FROM ingredientes WHERE codigo='TOCINO');
SET @ID_QCHEDAR     := (SELECT ingrediente_id FROM ingredientes WHERE codigo='QUESO_CHEDAR');
SET @ID_POLLO       := (SELECT ingrediente_id FROM ingredientes WHERE codigo='POLLO');
SET @ID_CHORIZO     := (SELECT ingrediente_id FROM ingredientes WHERE codigo='CHORIZO');
SET @ID_PAN_HD      := (SELECT ingrediente_id FROM ingredientes WHERE codigo='PAN_HOT_DOG');
SET @ID_COLA        := (SELECT ingrediente_id FROM ingredientes WHERE codigo='COLA');
SET @ID_PAN_BR      := (SELECT ingrediente_id FROM ingredientes WHERE codigo='PAN_BRIOCHE');
SET @ID_CARNE_HAMB  := (SELECT ingrediente_id FROM ingredientes WHERE codigo='CARNE_HAMB');
SET @ID_JAMON       := (SELECT ingrediente_id FROM ingredientes WHERE codigo='JAMON');
SET @ID_HUEVO       := (SELECT ingrediente_id FROM ingredientes WHERE codigo='HUEVO');
SET @ID_ARROZ       := (SELECT ingrediente_id FROM ingredientes WHERE codigo='ARROZ');
SET @ID_ARROZ_REL   := (SELECT ingrediente_id FROM ingredientes WHERE codigo='ARROZ_RELLENO');
SET @ID_AGUA_AROM   := (SELECT ingrediente_id FROM ingredientes WHERE codigo='AGUA_AROM');
SET @ID_CAFE_PAS    := (SELECT ingrediente_id FROM ingredientes WHERE codigo='CAFE_PASADO');
SET @ID_CAFE_LEC    := (SELECT ingrediente_id FROM ingredientes WHERE codigo='CAFE_LECHE');
SET @ID_CHOC_BEB    := (SELECT ingrediente_id FROM ingredientes WHERE codigo='CHOCOLATE_BEB');
SET @ID_FRUTA       := (SELECT ingrediente_id FROM ingredientes WHERE codigo='FRUTA');
SET @ID_LECHE       := (SELECT ingrediente_id FROM ingredientes WHERE codigo='LECHE');
SET @ID_FUZE_TEA    := (SELECT ingrediente_id FROM ingredientes WHERE codigo='FUZE_TEA');

-- Platos IDs
SET @P_SALCHIPAPA   := (SELECT plato_id FROM platos WHERE codigo='SALCHIPAPA');
SET @P_SALCHI_ESTUD := (SELECT plato_id FROM platos WHERE codigo='SALCHI_ESTUD');
SET @P_PAPA_CASA    := (SELECT plato_id FROM platos WHERE codigo='PAPA_CASA');
SET @P_PAPI_POLLO   := (SELECT plato_id FROM platos WHERE codigo='PAPI_POLLO');
SET @P_POLLO_LOCO   := (SELECT plato_id FROM platos WHERE codigo='POLLO_LOCO');
SET @P_CHORI_POLLO  := (SELECT plato_id FROM platos WHERE codigo='CHORI_POLLO');
SET @P_CHORI_PAPA   := (SELECT plato_id FROM platos WHERE codigo='CHORI_PAPA');
SET @P_PERRO_CAL    := (SELECT plato_id FROM platos WHERE codigo='PERRO_CAL');
SET @P_HB_CLASICA   := (SELECT plato_id FROM platos WHERE codigo='HB_CLASICA');
SET @P_HB_SUPREMA   := (SELECT plato_id FROM platos WHERE codigo='HB_SUPREMA');
SET @P_SECO_POLLO   := (SELECT plato_id FROM platos WHERE codigo='SECO_POLLO');
SET @P_ARROZ_REL_PL := (SELECT plato_id FROM platos WHERE codigo='ARROZ_RELLENO_PL');
SET @P_AGUA_AROM    := (SELECT plato_id FROM platos WHERE codigo='AGUA_AROM_BEB');
SET @P_CAFE_PAS     := (SELECT plato_id FROM platos WHERE codigo='CAFE_PASADO_BEB');
SET @P_CAFE_LECHE   := (SELECT plato_id FROM platos WHERE codigo='CAFE_LECHE_BEB');
SET @P_CHOC_BEB     := (SELECT plato_id FROM platos WHERE codigo='CHOCOLATE_BEB');
SET @P_JUGO_NAT     := (SELECT plato_id FROM platos WHERE codigo='JUGO_NAT');
SET @P_BATIDO       := (SELECT plato_id FROM platos WHERE codigo='BATIDO_FRUTA');
SET @P_COLAS        := (SELECT plato_id FROM platos WHERE codigo='COLAS_PL');
SET @P_FUZE_TEA     := (SELECT plato_id FROM platos WHERE codigo='FUZE_TEA_PL');

-- Limpia posibles recetas previas (por si re-ejecutas)
DELETE FROM receta_item WHERE plato_id IN (
                                           @P_SALCHIPAPA,@P_SALCHI_ESTUD,@P_PAPA_CASA,@P_PAPI_POLLO,@P_POLLO_LOCO,@P_CHORI_POLLO,@P_CHORI_PAPA,
                                           @P_PERRO_CAL,@P_HB_CLASICA,@P_HB_SUPREMA,@P_SECO_POLLO,@P_ARROZ_REL_PL,@P_AGUA_AROM,@P_CAFE_PAS,
                                           @P_CAFE_LECHE,@P_CHOC_BEB,@P_JUGO_NAT,@P_BATIDO,@P_COLAS,@P_FUZE_TEA
    );

-- === Recetas ===
-- Salchipapa
INSERT INTO receta_item(plato_id, ingrediente_id, cantidad) VALUES
                                                                (@P_SALCHIPAPA, @ID_PAPAS, 200),
                                                                (@P_SALCHIPAPA, @ID_SALCHICHA, 1);

-- Salchi Estudiantil
INSERT INTO receta_item VALUES
                            (@P_SALCHI_ESTUD, @ID_PAPAS, 100),
                            (@P_SALCHI_ESTUD, @ID_SALCHICHA, 1);

-- Papa de la Casa
INSERT INTO receta_item VALUES
                            (@P_PAPA_CASA, @ID_PAPAS, 200),
                            (@P_PAPA_CASA, @ID_SALCHICHA, 1),
                            (@P_PAPA_CASA, @ID_TOCINO, 50),
                            (@P_PAPA_CASA, @ID_QCHEDAR, 50);

-- Papi Pollo
INSERT INTO receta_item VALUES
                            (@P_PAPI_POLLO, @ID_PAPAS, 200),
                            (@P_PAPI_POLLO, @ID_POLLO, 50); -- asumimos 50gr

-- Pollo Loco
INSERT INTO receta_item VALUES
                            (@P_POLLO_LOCO, @ID_PAPAS, 200),
                            (@P_POLLO_LOCO, @ID_SALCHICHA, 1),
                            (@P_POLLO_LOCO, @ID_POLLO, 50),
                            (@P_POLLO_LOCO, @ID_QCHEDAR, 50);

-- Chori Pollo
INSERT INTO receta_item VALUES
                            (@P_CHORI_POLLO, @ID_PAPAS, 200),
                            (@P_CHORI_POLLO, @ID_CHORIZO, 0.5),
                            (@P_CHORI_POLLO, @ID_POLLO, 50);

-- Chori Papa
INSERT INTO receta_item VALUES
                            (@P_CHORI_PAPA, @ID_PAPAS, 200),
                            (@P_CHORI_PAPA, @ID_CHORIZO, 1);

-- Perro Caliente
INSERT INTO receta_item VALUES
                            (@P_PERRO_CAL, @ID_PAN_HD, 1),
                            (@P_PERRO_CAL, @ID_SALCHICHA, 1),
                            (@P_PERRO_CAL, @ID_COLA, 1); -- la carta lo muestra como combo con cola

-- Hamburguesa Clásica
INSERT INTO receta_item VALUES
                            (@P_HB_CLASICA, @ID_PAN_BR, 1),
                            (@P_HB_CLASICA, @ID_CARNE_HAMB, 1),
                            (@P_HB_CLASICA, @ID_QCHEDAR, 50);

-- Hamburguesa Suprema
INSERT INTO receta_item VALUES
                            (@P_HB_SUPREMA, @ID_PAN_BR, 1),
                            (@P_HB_SUPREMA, @ID_PAPAS, 100),
                            (@P_HB_SUPREMA, @ID_CARNE_HAMB, 1),
                            (@P_HB_SUPREMA, @ID_QCHEDAR, 50),
                            (@P_HB_SUPREMA, @ID_JAMON, 1),
                            (@P_HB_SUPREMA, @ID_HUEVO, 1);

-- Seco de Pollo
INSERT INTO receta_item VALUES
                            (@P_SECO_POLLO, @ID_POLLO, 100), -- asumimos 100gr para 1 porción
                            (@P_SECO_POLLO, @ID_ARROZ, 300);

-- Arroz Relleno
INSERT INTO receta_item VALUES
    (@P_ARROZ_REL_PL, @ID_ARROZ_REL, 400);

-- Bebidas / Cafetería
INSERT INTO receta_item VALUES
                            (@P_AGUA_AROM, @ID_AGUA_AROM, 1),
                            (@P_CAFE_PAS, @ID_CAFE_PAS, 1),
                            (@P_CAFE_LECHE, @ID_CAFE_LEC, 1),
                            (@P_CHOC_BEB, @ID_CHOC_BEB, 1),
                            (@P_JUGO_NAT, @ID_FRUTA, 1000),
                            (@P_BATIDO, @ID_FRUTA, 1000),
                            (@P_BATIDO, @ID_LECHE, 200),   -- asumimos 200ml
                            (@P_COLAS, @ID_COLA, 1),
                            (@P_FUZE_TEA, @ID_FUZE_TEA, 1);

-- =========================
-- 6) Verificaciones rápidas
-- =========================
-- SELECT COUNT(*) AS ingredientes FROM ingredientes;
-- SELECT COUNT(*) AS platos FROM platos;
-- SELECT p.codigo, i.nombre, r.cantidad
--   FROM receta_item r
--   JOIN platos p ON p.plato_id=r.plato_id
--   JOIN ingredientes i ON i.ingrediente_id=r.ingrediente_id
--  ORDER BY p.codigo;
