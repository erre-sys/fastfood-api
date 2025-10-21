-- =====================
-- Grupos de ingredientes
-- =====================
INSERT INTO grupo_ingrediente (nombre, estado) VALUES
  ('Carnes','A'),
  ('Vegetales','A'),
  ('Lácteos','A'),
  ('Panes','A'),
  ('Bebidas','A')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- =====================
-- Ingredientes (trigger crea inventario=0)
-- =====================
INSERT INTO ingredientes(codigo,nombre,grupo_ingrediente_id,unidad,es_extra,precio_extra,stock_minimo,activo)
SELECT 'CARNE','Carne de res',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Carnes'),
       'kg','N',NULL,1.000,'S'
UNION ALL
SELECT 'QUESO','Queso cheddar',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Lácteos'),
       'kg','S',0.50,0.500,'S'
UNION ALL
SELECT 'CEBOLLA','Cebolla blanca',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Vegetales'),
       'kg','S',0.20,0.200,'S'
UNION ALL
SELECT 'SALSA','Salsa especial',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Bebidas'),
       'ml','N',NULL,500.000,'S'
UNION ALL
SELECT 'PAN','Pan brioche',
       (SELECT grupo_ingrediente_id FROM grupo_ingrediente WHERE nombre='Panes'),
       'un','N',NULL,30.000,'S'
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

select * from inventario;

-- =====================
-- Proveedores
-- =====================
INSERT INTO proveedores(nombre,ruc,telefono,email,estado) VALUES
  ('Proveedor Central','1099999999001','0999999999','compras@central.ec','A'),
  ('Frescos S.A.','1099999999002','022222222','ventas@frescos.ec','A')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- =====================
-- Grupos de platos y platos
-- =====================
INSERT INTO grupo_plato(nombre) VALUES ('Hamburguesas'),('Bebidas')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO platos(codigo,nombre,grupo_plato_id,precio_base,estado,en_promocion,descuento_pct)
SELECT 'HB-CLASS','Hamburguesa Clásica',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Hamburguesas'),
       5.00,'S','N',0.00
UNION ALL
SELECT 'HB-DOBLE','Hamburguesa Doble',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Hamburguesas'),
       7.50,'S','N',0.00
UNION ALL
SELECT 'COLA-500','Cola 500ml',
       (SELECT grupo_plato_id FROM grupo_plato WHERE nombre='Bebidas'),
       1.30,'S','N',0.00
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), precio_base=VALUES(precio_base);

-- =====================
-- Recetas (por 1 unidad de plato)
-- =====================
-- HB-CLASS: 150g carne, 20g queso, 15g cebolla, 20ml salsa, 1 pan
INSERT INTO receta_item(plato_id, ingrediente_id, cantidad)
SELECT p.plato_id, i.ingrediente_id, x.cant
FROM (
  SELECT 'CARNE' cod, 0.150 cant UNION ALL
  SELECT 'QUESO', 0.020 UNION ALL
  SELECT 'CEBOLLA',0.015 UNION ALL
  SELECT 'SALSA', 20.000 UNION ALL
  SELECT 'PAN',   1.000
) x
JOIN ingredientes i ON i.codigo = x.cod
JOIN platos p ON p.codigo = 'HB-CLASS'
ON DUPLICATE KEY UPDATE cantidad=VALUES(cantidad);

-- HB-DOBLE: 2 discos carne (300g), 30g queso, 20ml salsa, 1 pan
INSERT INTO receta_item(plato_id, ingrediente_id, cantidad)
SELECT p.plato_id, i.ingrediente_id, x.cant
FROM (
  SELECT 'CARNE' cod, 0.300 cant UNION ALL
  SELECT 'QUESO', 0.030 UNION ALL
  SELECT 'SALSA', 20.000 UNION ALL
  SELECT 'PAN',   1.000
) x
JOIN ingredientes i ON i.codigo = x.cod
JOIN platos p ON p.codigo = 'HB-DOBLE'
ON DUPLICATE KEY UPDATE cantidad=VALUES(cantidad);

-- =====================
-- Compra de stock (trigger suma inventario y registra kardex COMPRA)
-- =====================
INSERT INTO compra(proveedor_id, referencia, creado_por_sub, observaciones)
SELECT (SELECT proveedor_id FROM proveedores WHERE nombre='Proveedor Central'),
       'FAC-0001','sub-seed','Carga inicial'
ON DUPLICATE KEY UPDATE referencia=VALUES(referencia);

SET @compra_id := LAST_INSERT_ID();

INSERT INTO compra_item(compra_id, ingrediente_id, cantidad, costo_unitario)
SELECT @compra_id, i.ingrediente_id, x.cant, x.costo
FROM (
  SELECT 'CARNE' cod,   10.000 cant, 5.20 costo UNION ALL
  SELECT 'QUESO' cod,    5.000,      6.00       UNION ALL
  SELECT 'CEBOLLA' cod,  3.000,      0.80       UNION ALL
  SELECT 'SALSA' cod, 5000.000,      0.01       UNION ALL
  SELECT 'PAN' cod,     100.000,     0.15
) x
JOIN ingredientes i ON i.codigo=x.cod;

-- =====================
-- Promo activa (15% a HB-CLASS por 10 días)
-- =====================
INSERT INTO promo_programada(plato_id, fecha_inicio, fecha_fin, descuento_pct, estado, creado_por_sub)
SELECT p.plato_id, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 10 DAY, 15.00, 'A', 'sub-seed'
FROM platos p WHERE p.codigo='HB-CLASS';

-- Aplica la bandera (si tienes el EVENT encendido, no hace falta)
CALL sp_promos_aplicar(NOW());

-- =====================
-- Verificaciones rápidas (opcionales)
-- =====================
SELECT codigo, stock_actual FROM inventario inv JOIN ingredientes i USING(ingrediente_id);
SELECT * FROM inventario_mov ORDER BY fecha DESC LIMIT 10;
SELECT codigo, nombre, en_promocion, descuento_pct FROM platos ORDER BY codigo;
