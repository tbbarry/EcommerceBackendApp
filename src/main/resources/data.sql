-- ============================================================
-- USERS
-- ============================================================

INSERT INTO users
(id, created_at, updated_at, email, firstname, lastname, password, role, enabled)
VALUES
(1, NOW(), NOW(), 'e@gmail.com', 'Alice', 'Martin',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'ADMIN', true),

(2, NOW(), NOW(), 't@test.com', 'Karim', 'Benali',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'USER', true),

(3, NOW(), NOW(), 'sophie@test.com', 'Sophie', 'Durand',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'USER', true);

-- ============================================================
-- CATEGORIES
-- ============================================================

INSERT INTO categories
(id, category_id, created_at, updated_at, description, name)
VALUES

-- Niveau 1
(1,NULL,NOW(),NOW(),'Vêtements pour femme','Femme'),
(2,NULL,NOW(),NOW(),'Vêtements pour homme','Homme'),
(3,NULL,NOW(),NOW(),'Vêtements pour enfant','Enfant'),
(4,NULL,NOW(),NOW(),'Accessoires de mode','Accessoires'),

-- Femme
(10,1,NOW(),NOW(),'Vêtements femme','Vêtements'),
(11,1,NOW(),NOW(),'Chaussures femme','Chaussures'),
(12,1,NOW(),NOW(),'Accessoires femme','Accessoires'),

(20,10,NOW(),NOW(),'T-shirts femme','T-shirts'),
(21,10,NOW(),NOW(),'Sweats femme','Sweats'),
(22,10,NOW(),NOW(),'Robes femme','Robes'),
(23,10,NOW(),NOW(),'Pantalons femme','Pantalons'),
(24,11,NOW(),NOW(),'Sneakers femme','Sneakers'),

-- Homme
(30,2,NOW(),NOW(),'Vêtements homme','Vêtements'),
(31,2,NOW(),NOW(),'Chaussures homme','Chaussures'),
(32,2,NOW(),NOW(),'Accessoires homme','Accessoires'),

(40,30,NOW(),NOW(),'T-shirts homme','T-shirts'),
(41,30,NOW(),NOW(),'Sweats homme','Sweats'),
(42,30,NOW(),NOW(),'Chemises homme','Chemises'),
(43,30,NOW(),NOW(),'Pantalons homme','Pantalons'),
(44,31,NOW(),NOW(),'Sneakers homme','Sneakers'),

-- Enfant
(50,3,NOW(),NOW(),'Vêtements enfant','Vêtements'),
(51,3,NOW(),NOW(),'Chaussures enfant','Chaussures'),
(52,3,NOW(),NOW(),'Accessoires enfant','Accessoires'),

(60,50,NOW(),NOW(),'T-shirts enfant','T-shirts'),
(61,50,NOW(),NOW(),'Sweats enfant','Sweats'),
(62,50,NOW(),NOW(),'Pantalons enfant','Pantalons'),
(63,51,NOW(),NOW(),'Sneakers enfant','Sneakers'),

-- Accessoires
(70,4,NOW(),NOW(),'Sacs','Sacs'),
(71,4,NOW(),NOW(),'Ceintures','Ceintures'),
(72,4,NOW(),NOW(),'Bijoux','Bijoux'),
(73,4,NOW(),NOW(),'Chapeaux','Chapeaux');

-- ============================================================
-- PRODUCTS
-- ============================================================

INSERT INTO products
(id, category_id, price, created_at, updated_at, brand, description, name, slug)
VALUES
(1,20,29.99,NOW(),NOW(),'Nike',
'T-shirt Nike en coton confortable',
'T-shirt Nike Basic',
't-shirt-nike-basic'),

(2,44,79.99,NOW(),NOW(),'Adidas',
'Sneakers Adidas légères',
'Adidas Runner',
'adidas-runner'),

(3,41,59.99,NOW(),NOW(),'Puma',
'Sweat à capuche Puma',
'Hoodie Puma Classic',
'hoodie-puma-classic'),

(4,70,39.99,NOW(),NOW(),'Zara',
'Sac noir élégant',
'Sac Zara Noir',
'sac-zara-noir'),

(5,43,69.99,NOW(),NOW(),'Levis',
'Jean slim bleu',
'Jean Levis Slim',
'jean-levis-slim'),

(6,4,25.00,NOW(),NOW(),'NiceShop',
'Carte cadeau digitale',
'Carte Cadeau 25 EUR',
'carte-cadeau-25-eur'),

(7,71,44.99,NOW(),NOW(),'Mango',
'Ceinture cuir classique',
'Ceinture Cuir Classique',
'ceinture-cuir-classique'),

(8,73,19.99,NOW(),NOW(),'Uniqlo',
'Bonnet unisexe',
'Bonnet Minimal',
'bonnet-minimal');

-- ============================================================
-- ATTRIBUTES
-- ============================================================

INSERT INTO attributes
(id, created_at, updated_at, name)
VALUES
(1,NOW(),NOW(),'Couleur'),
(2,NOW(),NOW(),'Taille');

-- ============================================================
-- ATTRIBUTE VALUES
-- ============================================================

INSERT INTO attribute_values
(id, attribute_id, created_at, updated_at, value)
VALUES
(1,1,NOW(),NOW(),'Noir'),
(2,1,NOW(),NOW(),'Blanc'),
(3,1,NOW(),NOW(),'Gris'),
(4,1,NOW(),NOW(),'Bleu'),
(5,1,NOW(),NOW(),'Marron'),

(6,2,NOW(),NOW(),'S'),
(7,2,NOW(),NOW(),'M'),
(8,2,NOW(),NOW(),'L'),
(9,2,NOW(),NOW(),'38'),
(10,2,NOW(),NOW(),'40'),
(11,2,NOW(),NOW(),'42'),
(12,2,NOW(),NOW(),'43'),
(13,2,NOW(),NOW(),'Unique');

-- ============================================================
-- VARIANTS
-- ============================================================

INSERT INTO variants
(id, price, product_id, created_at, updated_at, sku)
VALUES
(1,29.99,1,NOW(),NOW(),'NIKE-TS-BLK-S'),
(2,29.99,1,NOW(),NOW(),'NIKE-TS-BLK-M'),
(3,29.99,1,NOW(),NOW(),'NIKE-TS-WHT-M'),

(4,79.99,2,NOW(),NOW(),'ADI-RUN-WHT-42'),
(5,79.99,2,NOW(),NOW(),'ADI-RUN-BLK-43'),

(6,59.99,3,NOW(),NOW(),'PUMA-HOOD-GRY-M'),
(7,59.99,3,NOW(),NOW(),'PUMA-HOOD-BLK-L'),

(8,39.99,4,NOW(),NOW(),'ZARA-BAG-BLK-U'),

(9,69.99,5,NOW(),NOW(),'LEVIS-JEAN-BLU-38'),
(10,69.99,5,NOW(),NOW(),'LEVIS-JEAN-BLU-40'),

(11,25.00,6,NOW(),NOW(),'GIFT-CARD-25-001'),

(12,44.99,7,NOW(),NOW(),'MANGO-BELT-BRN-U'),

(13,19.99,8,NOW(),NOW(),'UNIQLO-BONNET-GRY-U');

-- ============================================================
-- VARIANT ATTRIBUTE VALUES
-- ============================================================

INSERT INTO variant_attribute_values
(id, variant_id, attribute_value_id, created_at, updated_at)
VALUES
(1,1,1,NOW(),NOW()),
(2,1,6,NOW(),NOW()),

(3,2,1,NOW(),NOW()),
(4,2,7,NOW(),NOW()),

(5,3,2,NOW(),NOW()),
(6,3,7,NOW(),NOW()),

(7,4,2,NOW(),NOW()),
(8,4,11,NOW(),NOW()),

(9,5,1,NOW(),NOW()),
(10,5,12,NOW(),NOW()),

(11,6,3,NOW(),NOW()),
(12,6,7,NOW(),NOW()),

(13,7,1,NOW(),NOW()),
(14,7,8,NOW(),NOW()),

(15,8,1,NOW(),NOW()),
(16,8,13,NOW(),NOW()),

(17,9,4,NOW(),NOW()),
(18,9,9,NOW(),NOW()),

(19,10,4,NOW(),NOW()),
(20,10,10,NOW(),NOW()),

(21,12,5,NOW(),NOW()),
(22,12,13,NOW(),NOW()),

(23,13,3,NOW(),NOW()),
(24,13,13,NOW(),NOW());

-- ============================================================
-- STOCKS (DISPONIBLES POUR TEST)
-- ============================================================

INSERT INTO stocks
(id, quantity, reserved_quantity, variant_id, created_at, updated_at)
VALUES
(1, 14, 0, 1, NOW(), NOW()),
(2, 11, 0, 2, NOW(), NOW()),
(3, 7, 0, 3, NOW(), NOW()),
(4, 8, 0, 4, NOW(), NOW()),
(5, 10, 0, 5, NOW(), NOW()),
(6, 9, 0, 6, NOW(), NOW()),
(7, 6, 0, 7, NOW(), NOW()),
(8, 5, 0, 8, NOW(), NOW()),
(9, 12, 0, 9, NOW(), NOW()),
(10, 9, 0, 10, NOW(), NOW()),
(11, 20, 0, 11, NOW(), NOW()),
(12, 13, 0, 12, NOW(), NOW()),
(13, 15, 0, 13, NOW(), NOW());

-- ============================================================
-- PRODUCT IMAGES (URLS REELLES ACCESSIBLES)
-- ============================================================

INSERT INTO product_images
(id, url, object_key, alt, is_main, display_order, product_id, visual_attribute_value_id, created_at, updated_at)
VALUES
-- Produit 1 : T-shirt Nike Basic
(1, 'https://picsum.photos/seed/tshirt-nike-main/1200/1200', null, 'T-shirt Nike Basic vue principale', true, 0, 1, null, NOW(), NOW()),
(2, 'https://picsum.photos/seed/tshirt-nike-noir/1200/1200', null, 'T-shirt Nike Basic noir', false, 1, 1, 1, NOW(), NOW()),
(3, 'https://picsum.photos/seed/tshirt-nike-blanc/1200/1200', null, 'T-shirt Nike Basic blanc', false, 2, 1, 2, NOW(), NOW()),

-- Produit 2 : Adidas Runner
(4, 'https://picsum.photos/seed/adidas-runner-main/1200/1200', null, 'Adidas Runner vue principale', true, 0, 2, null, NOW(), NOW()),
(5, 'https://picsum.photos/seed/adidas-runner-blanc/1200/1200', null, 'Adidas Runner blanc', false, 1, 2, 2, NOW(), NOW()),
(6, 'https://picsum.photos/seed/adidas-runner-noir/1200/1200', null, 'Adidas Runner noir', false, 2, 2, 1, NOW(), NOW()),

-- Produit 3 : Hoodie Puma Classic
(7, 'https://picsum.photos/seed/hoodie-puma-main/1200/1200', null, 'Hoodie Puma Classic vue principale', true, 0, 3, null, NOW(), NOW()),
(8, 'https://picsum.photos/seed/hoodie-puma-gris/1200/1200', null, 'Hoodie Puma Classic gris', false, 1, 3, 3, NOW(), NOW()),
(9, 'https://picsum.photos/seed/hoodie-puma-noir/1200/1200', null, 'Hoodie Puma Classic noir', false, 2, 3, 1, NOW(), NOW()),

-- Produit 4 : Sac Zara Noir
(10, 'https://picsum.photos/seed/sac-zara-main/1200/1200', null, 'Sac Zara Noir vue principale', true, 0, 4, null, NOW(), NOW()),
(11, 'https://picsum.photos/seed/sac-zara-noir/1200/1200', null, 'Sac Zara Noir détail', false, 1, 4, 1, NOW(), NOW()),

-- Produit 5 : Jean Levis Slim
(12, 'https://picsum.photos/seed/jean-levis-main/1200/1200', null, 'Jean Levis Slim vue principale', true, 0, 5, null, NOW(), NOW()),
(13, 'https://picsum.photos/seed/jean-levis-bleu/1200/1200', null, 'Jean Levis Slim bleu', false, 1, 5, 4, NOW(), NOW()),

-- Produit 6 : Carte cadeau
(14, 'https://picsum.photos/seed/gift-card-main/1200/1200', null, 'Carte cadeau 25 EUR', true, 0, 6, null, NOW(), NOW()),

-- Produit 7 : Ceinture Mango
(15, 'https://picsum.photos/seed/ceinture-mango-main/1200/1200', null, 'Ceinture Cuir Classique vue principale', true, 0, 7, null, NOW(), NOW()),
(16, 'https://picsum.photos/seed/ceinture-mango-marron/1200/1200', null, 'Ceinture Cuir Classique marron', false, 1, 7, 5, NOW(), NOW()),

-- Produit 8 : Bonnet Uniqlo
(17, 'https://picsum.photos/seed/bonnet-uniqlo-main/1200/1200', null, 'Bonnet Minimal vue principale', true, 0, 8, null, NOW(), NOW()),
(18, 'https://picsum.photos/seed/bonnet-uniqlo-gris/1200/1200', null, 'Bonnet Minimal gris', false, 1, 8, 3, NOW(), NOW());

-- ============================================================
-- PROMOS (POUR TEST CATALOGUE)
-- ============================================================

INSERT INTO promos
(id, created_at, updated_at, code, discount_value, start_date, end_date, is_active)
VALUES
(1, NOW(), NOW(), 'GLOBAL-TSHIRT-20', 20.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(2, NOW(), NOW(), 'GLOBAL-LEVIS-15', 15.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(3, NOW(), NOW(), 'VAR-ADIDAS-10', 10.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(4, NOW(), NOW(), 'VAR-ADIDAS-35', 35.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(5, NOW(), NOW(), 'VAR-LEVIS-30', 30.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true);

-- ============================================================
-- PRODUIT <-> PROMO (REMISE GLOBALE)
-- ============================================================

INSERT INTO product_promos
(id, created_at, updated_at, product_id, promo_id)
VALUES
(1, NOW(), NOW(), 1, 1),
(2, NOW(), NOW(), 5, 2);

-- ============================================================
-- VARIANTE <-> PROMO (REMISE SPECIFIQUE)
-- ============================================================

INSERT INTO variant_promos
(id, created_at, updated_at, variant_id, promo_id)
VALUES
(1, NOW(), NOW(), 4, 3),
(2, NOW(), NOW(), 5, 4),
(3, NOW(), NOW(), 9, 5);

-- ============================================================
-- CATALOG PRODUCT
-- ============================================================

INSERT INTO catalog_product
(id, product_id, created_at, updated_at, name, slug, price,
 brand, image_url, category_id, category_name,
 base_price_min, base_price_max, final_price_min, final_price_max,
 display_price, discount_percent_global, discount_percent_min,
 discount_percent_max, has_discount, promo_scope)
VALUES

(1,1,NOW(),NOW(),
'T-shirt Nike Basic',
't-shirt-nike-basic',
23.99,
'Nike',
'https://picsum.photos/seed/tshirt-nike-main/1200/1200',
20,
'T-shirts',
29.99, 29.99, 23.99, 23.99,
23.99, 20.00, 20.00,
20.00, true, 'GLOBAL'),

(2,2,NOW(),NOW(),
'Adidas Runner',
'adidas-runner',
51.99,
'Adidas',
'https://picsum.photos/seed/adidas-runner-main/1200/1200',
44,
'Sneakers',
79.99, 79.99, 51.99, 71.99,
51.99, 0.00, 10.00,
35.00, true, 'VARIANT'),

(3,3,NOW(),NOW(),
'Hoodie Puma Classic',
'hoodie-puma-classic',
59.99,
'Puma',
'https://picsum.photos/seed/hoodie-puma-main/1200/1200',
41,
'Sweats',
59.99, 59.99, 59.99, 59.99,
59.99, 0.00, 0.00,
0.00, false, 'NONE'),

(4,4,NOW(),NOW(),
'Sac Zara Noir',
'sac-zara-noir',
39.99,
'Zara',
'https://picsum.photos/seed/sac-zara-main/1200/1200',
70,
'Sacs',
39.99, 39.99, 39.99, 39.99,
39.99, 0.00, 0.00,
0.00, false, 'NONE'),

(5,5,NOW(),NOW(),
'Jean Levis Slim',
'jean-levis-slim',
48.99,
'Levis',
'https://picsum.photos/seed/jean-levis-main/1200/1200',
43,
'Pantalons',
69.99, 69.99, 48.99, 59.49,
48.99, 15.00, 15.00,
30.00, true, 'VARIANT'),

(6,6,NOW(),NOW(),
'Carte Cadeau 25 EUR',
'carte-cadeau-25-eur',
25.00,
'NiceShop',
'https://picsum.photos/seed/gift-card-main/1200/1200',
4,
'Accessoires',
25.00, 25.00, 25.00, 25.00,
25.00, 0.00, 0.00,
0.00, false, 'NONE'),

(7,7,NOW(),NOW(),
'Ceinture Cuir Classique',
'ceinture-cuir-classique',
44.99,
'Mango',
'https://picsum.photos/seed/ceinture-mango-main/1200/1200',
71,
'Ceintures',
44.99, 44.99, 44.99, 44.99,
44.99, 0.00, 0.00,
0.00, false, 'NONE'),

(8,8,NOW(),NOW(),
'Bonnet Minimal',
'bonnet-minimal',
19.99,
'Uniqlo',
'https://picsum.photos/seed/bonnet-uniqlo-main/1200/1200',
73,
'Chapeaux',
19.99, 19.99, 19.99, 19.99,
19.99, 0.00, 0.00,
0.00, false, 'NONE');

-- ============================================================
-- CATALOG PRODUCT FACET
-- ============================================================

INSERT INTO catalog_product_facet
(id, product_id, facet_id, facet_code,
 facet_value_id, value_code, value_label,
 created_at, updated_at)
VALUES

-- Produit 1
(1,1,1,'color',1,'1','Noir',NOW(),NOW()),
(2,1,1,'color',2,'2','Blanc',NOW(),NOW()),
(3,1,2,'size',6,'6','S',NOW(),NOW()),
(4,1,2,'size',7,'7','M',NOW(),NOW()),
(5,1,3,'brand',0,'nike','Nike',NOW(),NOW()),

-- Produit 2
(6,2,1,'color',2,'2','Blanc',NOW(),NOW()),
(7,2,1,'color',1,'1','Noir',NOW(),NOW()),
(8,2,2,'size',11,'11','42',NOW(),NOW()),
(9,2,2,'size',12,'12','43',NOW(),NOW()),
(10,2,3,'brand',0,'adidas','Adidas',NOW(),NOW()),

-- Produit 3
(11,3,1,'color',3,'3','Gris',NOW(),NOW()),
(12,3,1,'color',1,'1','Noir',NOW(),NOW()),
(13,3,2,'size',7,'7','M',NOW(),NOW()),
(14,3,2,'size',8,'8','L',NOW(),NOW()),
(15,3,3,'brand',0,'puma','Puma',NOW(),NOW()),

-- Produit 4
(16,4,1,'color',1,'1','Noir',NOW(),NOW()),
(17,4,2,'size',13,'13','Unique',NOW(),NOW()),
(18,4,3,'brand',0,'zara','Zara',NOW(),NOW()),

-- Produit 5
(19,5,1,'color',4,'4','Bleu',NOW(),NOW()),
(20,5,2,'size',9,'9','38',NOW(),NOW()),
(21,5,2,'size',10,'10','40',NOW(),NOW()),
(22,5,3,'brand',0,'levis','Levis',NOW(),NOW()),

-- Produit 7
(23,7,1,'color',5,'5','Marron',NOW(),NOW()),
(24,7,2,'size',13,'13','Unique',NOW(),NOW()),
(25,7,3,'brand',0,'mango','Mango',NOW(),NOW()),

-- Produit 8
(26,8,1,'color',3,'3','Gris',NOW(),NOW()),
(27,8,2,'size',13,'13','Unique',NOW(),NOW()),
(28,8,3,'brand',0,'uniqlo','Uniqlo',NOW(),NOW());

-- ============================================================
-- EXTRA PRODUCTS (RICHER SEED DATASET)
-- ============================================================

INSERT INTO products
(id, category_id, price, created_at, updated_at, brand, description, name, slug)
VALUES
(101,22,49.99,NOW(),NOW(),'Mango','Robe legere en lin coupe midi','Robe Linen Breeze','robe-linen-breeze'),
(102,42,54.99,NOW(),NOW(),'Celio','Chemise oxford ajustee facile a porter','Chemise Oxford Modern','chemise-oxford-modern'),
(103,21,64.99,NOW(),NOW(),'Puma','Sweat crop doux pour un style casual','Sweat Crop Soft Touch','sweat-crop-soft-touch'),
(104,24,89.99,NOW(),NOW(),'Adidas','Sneakers femme semelle confortable','Sneakers Urban Pulse Femme','sneakers-urban-pulse-femme'),
(105,40,24.99,NOW(),NOW(),'Uniqlo','T-shirt homme basique coton premium','T-shirt Essential Homme','t-shirt-essential-homme'),
(106,62,34.99,NOW(),NOW(),'Zara Kids','Jogger enfant souple et resistant','Pantalon Jogger Kids','pantalon-jogger-kids'),
(107,63,59.99,NOW(),NOW(),'Nike Kids','Sneakers enfant legeres pour tous les jours','Sneakers Kids Sprint','sneakers-kids-sprint'),
(108,70,69.99,NOW(),NOW(),'Zara','Sac cabas grand format usage quotidien','Sac Cabas City','sac-cabas-city'),
(109,72,29.99,NOW(),NOW(),'Mango','Collier minimal finition doree','Collier Minimal Dore','collier-minimal-dore'),
(110,73,22.99,NOW(),NOW(),'Uniqlo','Bob coton unisexe leger','Bob Coton Weekend','bob-coton-weekend'),
(111,71,39.99,NOW(),NOW(),'Levis','Ceinture cuir boucle metal classique','Ceinture Classique Homme','ceinture-classique-homme'),
(112,23,74.99,NOW(),NOW(),'Levis','Jean femme mom fit taille haute','Jean Mom Fit Femme','jean-mom-fit-femme');

INSERT INTO variants
(id, price, product_id, created_at, updated_at, sku)
VALUES
(1001,49.99,101,NOW(),NOW(),'MNG-ROBE-BLU-S'),
(1002,49.99,101,NOW(),NOW(),'MNG-ROBE-BLU-M'),
(1003,54.99,101,NOW(),NOW(),'MNG-ROBE-BLK-L'),

(1004,54.99,102,NOW(),NOW(),'CELIO-OXF-WHT-M'),
(1005,54.99,102,NOW(),NOW(),'CELIO-OXF-WHT-L'),
(1006,59.99,102,NOW(),NOW(),'CELIO-OXF-BLU-L'),

(1007,64.99,103,NOW(),NOW(),'PUMA-CROP-GRY-S'),
(1008,64.99,103,NOW(),NOW(),'PUMA-CROP-GRY-M'),
(1009,69.99,103,NOW(),NOW(),'PUMA-CROP-BLK-L'),

(1010,89.99,104,NOW(),NOW(),'ADI-UPF-WHT-42'),
(1011,89.99,104,NOW(),NOW(),'ADI-UPF-WHT-43'),
(1012,94.99,104,NOW(),NOW(),'ADI-UPF-BLK-43'),

(1013,24.99,105,NOW(),NOW(),'UNIQ-TSH-WHT-S'),
(1014,24.99,105,NOW(),NOW(),'UNIQ-TSH-WHT-M'),
(1015,26.99,105,NOW(),NOW(),'UNIQ-TSH-BLK-L'),

(1016,34.99,106,NOW(),NOW(),'ZKID-JOG-GRY-38'),
(1017,34.99,106,NOW(),NOW(),'ZKID-JOG-GRY-40'),
(1018,36.99,106,NOW(),NOW(),'ZKID-JOG-BLU-40'),

(1019,59.99,107,NOW(),NOW(),'NKID-SPR-WHT-42'),
(1020,59.99,107,NOW(),NOW(),'NKID-SPR-WHT-43'),
(1021,62.99,107,NOW(),NOW(),'NKID-SPR-BLK-43'),

(1022,69.99,108,NOW(),NOW(),'ZARA-CAB-BRN-U'),
(1023,72.99,108,NOW(),NOW(),'ZARA-CAB-BLK-U'),

(1024,29.99,109,NOW(),NOW(),'MNG-COLL-GOLD-U'),
(1025,32.99,109,NOW(),NOW(),'MNG-COLL-SILV-U'),

(1026,22.99,110,NOW(),NOW(),'UNIQ-BOB-BEI-U'),
(1027,22.99,110,NOW(),NOW(),'UNIQ-BOB-BLK-U'),

(1028,39.99,111,NOW(),NOW(),'LEVIS-BEL-BRN-U'),
(1029,39.99,111,NOW(),NOW(),'LEVIS-BEL-BLK-U'),

(1030,74.99,112,NOW(),NOW(),'LEVIS-MOM-BLU-38'),
(1031,74.99,112,NOW(),NOW(),'LEVIS-MOM-BLU-40'),
(1032,79.99,112,NOW(),NOW(),'LEVIS-MOM-BLK-40');

INSERT INTO variant_attribute_values
(id, variant_id, attribute_value_id, created_at, updated_at)
VALUES
(5001,1001,4,NOW(),NOW()),(5002,1001,6,NOW(),NOW()),
(5003,1002,4,NOW(),NOW()),(5004,1002,7,NOW(),NOW()),
(5005,1003,1,NOW(),NOW()),(5006,1003,8,NOW(),NOW()),

(5007,1004,2,NOW(),NOW()),(5008,1004,7,NOW(),NOW()),
(5009,1005,2,NOW(),NOW()),(5010,1005,8,NOW(),NOW()),
(5011,1006,4,NOW(),NOW()),(5012,1006,8,NOW(),NOW()),

(5013,1007,3,NOW(),NOW()),(5014,1007,6,NOW(),NOW()),
(5015,1008,3,NOW(),NOW()),(5016,1008,7,NOW(),NOW()),
(5017,1009,1,NOW(),NOW()),(5018,1009,8,NOW(),NOW()),

(5019,1010,2,NOW(),NOW()),(5020,1010,11,NOW(),NOW()),
(5021,1011,2,NOW(),NOW()),(5022,1011,12,NOW(),NOW()),
(5023,1012,1,NOW(),NOW()),(5024,1012,12,NOW(),NOW()),

(5025,1013,2,NOW(),NOW()),(5026,1013,6,NOW(),NOW()),
(5027,1014,2,NOW(),NOW()),(5028,1014,7,NOW(),NOW()),
(5029,1015,1,NOW(),NOW()),(5030,1015,8,NOW(),NOW()),

(5031,1016,3,NOW(),NOW()),(5032,1016,9,NOW(),NOW()),
(5033,1017,3,NOW(),NOW()),(5034,1017,10,NOW(),NOW()),
(5035,1018,4,NOW(),NOW()),(5036,1018,10,NOW(),NOW()),

(5037,1019,2,NOW(),NOW()),(5038,1019,11,NOW(),NOW()),
(5039,1020,2,NOW(),NOW()),(5040,1020,12,NOW(),NOW()),
(5041,1021,1,NOW(),NOW()),(5042,1021,12,NOW(),NOW()),

(5043,1022,5,NOW(),NOW()),(5044,1022,13,NOW(),NOW()),
(5045,1023,1,NOW(),NOW()),(5046,1023,13,NOW(),NOW()),

(5047,1024,5,NOW(),NOW()),(5048,1024,13,NOW(),NOW()),
(5049,1025,3,NOW(),NOW()),(5050,1025,13,NOW(),NOW()),

(5051,1026,2,NOW(),NOW()),(5052,1026,13,NOW(),NOW()),
(5053,1027,1,NOW(),NOW()),(5054,1027,13,NOW(),NOW()),

(5055,1028,5,NOW(),NOW()),(5056,1028,13,NOW(),NOW()),
(5057,1029,1,NOW(),NOW()),(5058,1029,13,NOW(),NOW()),

(5059,1030,4,NOW(),NOW()),(5060,1030,9,NOW(),NOW()),
(5061,1031,4,NOW(),NOW()),(5062,1031,10,NOW(),NOW()),
(5063,1032,1,NOW(),NOW()),(5064,1032,10,NOW(),NOW());

INSERT INTO stocks
(id, quantity, reserved_quantity, variant_id, created_at, updated_at)
VALUES
(7001,18,0,1001,NOW(),NOW()),(7002,15,0,1002,NOW(),NOW()),(7003,9,0,1003,NOW(),NOW()),
(7004,16,0,1004,NOW(),NOW()),(7005,11,0,1005,NOW(),NOW()),(7006,7,0,1006,NOW(),NOW()),
(7007,13,0,1007,NOW(),NOW()),(7008,12,0,1008,NOW(),NOW()),(7009,8,0,1009,NOW(),NOW()),
(7010,14,0,1010,NOW(),NOW()),(7011,10,0,1011,NOW(),NOW()),(7012,6,0,1012,NOW(),NOW()),
(7013,20,0,1013,NOW(),NOW()),(7014,17,0,1014,NOW(),NOW()),(7015,9,0,1015,NOW(),NOW()),
(7016,19,0,1016,NOW(),NOW()),(7017,16,0,1017,NOW(),NOW()),(7018,10,0,1018,NOW(),NOW()),
(7019,13,0,1019,NOW(),NOW()),(7020,11,0,1020,NOW(),NOW()),(7021,7,0,1021,NOW(),NOW()),
(7022,12,0,1022,NOW(),NOW()),(7023,8,0,1023,NOW(),NOW()),
(7024,22,0,1024,NOW(),NOW()),(7025,14,0,1025,NOW(),NOW()),
(7026,15,0,1026,NOW(),NOW()),(7027,10,0,1027,NOW(),NOW()),
(7028,18,0,1028,NOW(),NOW()),(7029,13,0,1029,NOW(),NOW()),
(7030,12,0,1030,NOW(),NOW()),(7031,10,0,1031,NOW(),NOW()),(7032,6,0,1032,NOW(),NOW());

INSERT INTO product_images
(id, url, object_key, alt, is_main, display_order, product_id, visual_attribute_value_id, created_at, updated_at)
VALUES
(9001,'https://picsum.photos/seed/fashion-101/1200/1200',null,'Robe Linen Breeze vue principale',true,0,101,null,NOW(),NOW()),
(9002,'https://picsum.photos/seed/fashion-102/1200/1200',null,'Robe Linen Breeze bleu',false,1,101,4,NOW(),NOW()),
(9003,'https://picsum.photos/seed/fashion-103/1200/1200',null,'Robe Linen Breeze noir',false,2,101,1,NOW(),NOW()),

(9004,'https://picsum.photos/seed/fashion-104/1200/1200',null,'Chemise Oxford Modern vue principale',true,0,102,null,NOW(),NOW()),
(9005,'https://picsum.photos/seed/fashion-105/1200/1200',null,'Chemise Oxford Modern blanc',false,1,102,2,NOW(),NOW()),
(9006,'https://picsum.photos/seed/fashion-106/1200/1200',null,'Chemise Oxford Modern bleu',false,2,102,4,NOW(),NOW()),

(9007,'https://picsum.photos/seed/fashion-107/1200/1200',null,'Sweat Crop Soft Touch vue principale',true,0,103,null,NOW(),NOW()),
(9008,'https://picsum.photos/seed/fashion-108/1200/1200',null,'Sweat Crop Soft Touch gris',false,1,103,3,NOW(),NOW()),
(9009,'https://picsum.photos/seed/fashion-109/1200/1200',null,'Sweat Crop Soft Touch noir',false,2,103,1,NOW(),NOW()),

(9010,'https://picsum.photos/seed/fashion-110/1200/1200',null,'Sneakers Urban Pulse Femme vue principale',true,0,104,null,NOW(),NOW()),
(9011,'https://picsum.photos/seed/fashion-111/1200/1200',null,'Sneakers Urban Pulse Femme blanc',false,1,104,2,NOW(),NOW()),
(9012,'https://picsum.photos/seed/fashion-112/1200/1200',null,'Sneakers Urban Pulse Femme noir',false,2,104,1,NOW(),NOW()),

(9013,'https://picsum.photos/seed/fashion-113/1200/1200',null,'T-shirt Essential Homme vue principale',true,0,105,null,NOW(),NOW()),
(9014,'https://picsum.photos/seed/fashion-114/1200/1200',null,'T-shirt Essential Homme blanc',false,1,105,2,NOW(),NOW()),
(9015,'https://picsum.photos/seed/fashion-115/1200/1200',null,'T-shirt Essential Homme noir',false,2,105,1,NOW(),NOW()),

(9016,'https://picsum.photos/seed/fashion-116/1200/1200',null,'Pantalon Jogger Kids vue principale',true,0,106,null,NOW(),NOW()),
(9017,'https://picsum.photos/seed/fashion-117/1200/1200',null,'Pantalon Jogger Kids gris',false,1,106,3,NOW(),NOW()),
(9018,'https://picsum.photos/seed/fashion-118/1200/1200',null,'Pantalon Jogger Kids bleu',false,2,106,4,NOW(),NOW()),

(9019,'https://picsum.photos/seed/fashion-119/1200/1200',null,'Sneakers Kids Sprint vue principale',true,0,107,null,NOW(),NOW()),
(9020,'https://picsum.photos/seed/fashion-120/1200/1200',null,'Sneakers Kids Sprint blanc',false,1,107,2,NOW(),NOW()),
(9021,'https://picsum.photos/seed/fashion-121/1200/1200',null,'Sneakers Kids Sprint noir',false,2,107,1,NOW(),NOW()),

(9022,'https://picsum.photos/seed/fashion-122/1200/1200',null,'Sac Cabas City vue principale',true,0,108,null,NOW(),NOW()),
(9023,'https://picsum.photos/seed/fashion-123/1200/1200',null,'Sac Cabas City marron',false,1,108,5,NOW(),NOW()),
(9024,'https://picsum.photos/seed/fashion-124/1200/1200',null,'Sac Cabas City noir',false,2,108,1,NOW(),NOW()),

(9025,'https://picsum.photos/seed/fashion-125/1200/1200',null,'Collier Minimal Dore vue principale',true,0,109,null,NOW(),NOW()),
(9026,'https://picsum.photos/seed/fashion-126/1200/1200',null,'Collier Minimal Dore dore',false,1,109,5,NOW(),NOW()),
(9027,'https://picsum.photos/seed/fashion-127/1200/1200',null,'Collier Minimal Dore argent',false,2,109,3,NOW(),NOW()),

(9028,'https://picsum.photos/seed/fashion-128/1200/1200',null,'Bob Coton Weekend vue principale',true,0,110,null,NOW(),NOW()),
(9029,'https://picsum.photos/seed/fashion-129/1200/1200',null,'Bob Coton Weekend beige',false,1,110,2,NOW(),NOW()),
(9030,'https://picsum.photos/seed/fashion-130/1200/1200',null,'Bob Coton Weekend noir',false,2,110,1,NOW(),NOW()),

(9031,'https://picsum.photos/seed/fashion-131/1200/1200',null,'Ceinture Classique Homme vue principale',true,0,111,null,NOW(),NOW()),
(9032,'https://picsum.photos/seed/fashion-132/1200/1200',null,'Ceinture Classique Homme marron',false,1,111,5,NOW(),NOW()),
(9033,'https://picsum.photos/seed/fashion-133/1200/1200',null,'Ceinture Classique Homme noir',false,2,111,1,NOW(),NOW()),

(9034,'https://picsum.photos/seed/fashion-134/1200/1200',null,'Jean Mom Fit Femme vue principale',true,0,112,null,NOW(),NOW()),
(9035,'https://picsum.photos/seed/fashion-135/1200/1200',null,'Jean Mom Fit Femme bleu',false,1,112,4,NOW(),NOW()),
(9036,'https://picsum.photos/seed/fashion-136/1200/1200',null,'Jean Mom Fit Femme noir',false,2,112,1,NOW(),NOW());

INSERT INTO catalog_product
(id, product_id, created_at, updated_at, name, slug, price,
 brand, image_url, category_id, category_name,
 base_price_min, base_price_max, final_price_min, final_price_max,
 display_price, discount_percent_global, discount_percent_min,
 discount_percent_max, has_discount, promo_scope)
VALUES
(1101,101,NOW(),NOW(),'Robe Linen Breeze','robe-linen-breeze',49.99,'Mango','https://picsum.photos/seed/fashion-101/1200/1200',22,'Robes',49.99,54.99,49.99,54.99,49.99,0.00,0.00,0.00,false,'NONE'),
(1102,102,NOW(),NOW(),'Chemise Oxford Modern','chemise-oxford-modern',54.99,'Celio','https://picsum.photos/seed/fashion-104/1200/1200',42,'Chemises',54.99,59.99,54.99,59.99,54.99,0.00,0.00,0.00,false,'NONE'),
(1103,103,NOW(),NOW(),'Sweat Crop Soft Touch','sweat-crop-soft-touch',64.99,'Puma','https://picsum.photos/seed/fashion-107/1200/1200',21,'Sweats',64.99,69.99,64.99,69.99,64.99,0.00,0.00,0.00,false,'NONE'),
(1104,104,NOW(),NOW(),'Sneakers Urban Pulse Femme','sneakers-urban-pulse-femme',89.99,'Adidas','https://picsum.photos/seed/fashion-110/1200/1200',24,'Sneakers',89.99,94.99,89.99,94.99,89.99,0.00,0.00,0.00,false,'NONE'),
(1105,105,NOW(),NOW(),'T-shirt Essential Homme','t-shirt-essential-homme',24.99,'Uniqlo','https://picsum.photos/seed/fashion-113/1200/1200',40,'T-shirts',24.99,26.99,24.99,26.99,24.99,0.00,0.00,0.00,false,'NONE'),
(1106,106,NOW(),NOW(),'Pantalon Jogger Kids','pantalon-jogger-kids',34.99,'Zara Kids','https://picsum.photos/seed/fashion-116/1200/1200',62,'Pantalons',34.99,36.99,34.99,36.99,34.99,0.00,0.00,0.00,false,'NONE'),
(1107,107,NOW(),NOW(),'Sneakers Kids Sprint','sneakers-kids-sprint',59.99,'Nike Kids','https://picsum.photos/seed/fashion-119/1200/1200',63,'Sneakers',59.99,62.99,59.99,62.99,59.99,0.00,0.00,0.00,false,'NONE'),
(1108,108,NOW(),NOW(),'Sac Cabas City','sac-cabas-city',69.99,'Zara','https://picsum.photos/seed/fashion-122/1200/1200',70,'Sacs',69.99,72.99,69.99,72.99,69.99,0.00,0.00,0.00,false,'NONE'),
(1109,109,NOW(),NOW(),'Collier Minimal Dore','collier-minimal-dore',29.99,'Mango','https://picsum.photos/seed/fashion-125/1200/1200',72,'Bijoux',29.99,32.99,29.99,32.99,29.99,0.00,0.00,0.00,false,'NONE'),
(1110,110,NOW(),NOW(),'Bob Coton Weekend','bob-coton-weekend',22.99,'Uniqlo','https://picsum.photos/seed/fashion-128/1200/1200',73,'Chapeaux',22.99,22.99,22.99,22.99,22.99,0.00,0.00,0.00,false,'NONE'),
(1111,111,NOW(),NOW(),'Ceinture Classique Homme','ceinture-classique-homme',39.99,'Levis','https://picsum.photos/seed/fashion-131/1200/1200',71,'Ceintures',39.99,39.99,39.99,39.99,39.99,0.00,0.00,0.00,false,'NONE'),
(1112,112,NOW(),NOW(),'Jean Mom Fit Femme','jean-mom-fit-femme',74.99,'Levis','https://picsum.photos/seed/fashion-134/1200/1200',23,'Pantalons',74.99,79.99,74.99,79.99,74.99,0.00,0.00,0.00,false,'NONE');

INSERT INTO catalog_product_facet
(id, product_id, facet_id, facet_code, facet_value_id, value_code, value_label, created_at, updated_at)
VALUES
(13001,101,1,'color',4,'4','Bleu',NOW(),NOW()),(13002,101,1,'color',1,'1','Noir',NOW(),NOW()),(13003,101,2,'size',7,'7','M',NOW(),NOW()),(13004,101,3,'brand',0,'mango','Mango',NOW(),NOW()),
(13005,102,1,'color',2,'2','Blanc',NOW(),NOW()),(13006,102,1,'color',4,'4','Bleu',NOW(),NOW()),(13007,102,2,'size',8,'8','L',NOW(),NOW()),(13008,102,3,'brand',0,'celio','Celio',NOW(),NOW()),
(13009,103,1,'color',3,'3','Gris',NOW(),NOW()),(13010,103,1,'color',1,'1','Noir',NOW(),NOW()),(13011,103,2,'size',7,'7','M',NOW(),NOW()),(13012,103,3,'brand',0,'puma','Puma',NOW(),NOW()),
(13013,104,1,'color',2,'2','Blanc',NOW(),NOW()),(13014,104,1,'color',1,'1','Noir',NOW(),NOW()),(13015,104,2,'size',12,'12','43',NOW(),NOW()),(13016,104,3,'brand',0,'adidas','Adidas',NOW(),NOW()),
(13017,105,1,'color',2,'2','Blanc',NOW(),NOW()),(13018,105,1,'color',1,'1','Noir',NOW(),NOW()),(13019,105,2,'size',7,'7','M',NOW(),NOW()),(13020,105,3,'brand',0,'uniqlo','Uniqlo',NOW(),NOW()),
(13021,106,1,'color',3,'3','Gris',NOW(),NOW()),(13022,106,1,'color',4,'4','Bleu',NOW(),NOW()),(13023,106,2,'size',10,'10','40',NOW(),NOW()),(13024,106,3,'brand',0,'zara-kids','Zara Kids',NOW(),NOW()),
(13025,107,1,'color',2,'2','Blanc',NOW(),NOW()),(13026,107,1,'color',1,'1','Noir',NOW(),NOW()),(13027,107,2,'size',12,'12','43',NOW(),NOW()),(13028,107,3,'brand',0,'nike-kids','Nike Kids',NOW(),NOW()),
(13029,108,1,'color',5,'5','Marron',NOW(),NOW()),(13030,108,1,'color',1,'1','Noir',NOW(),NOW()),(13031,108,2,'size',13,'13','Unique',NOW(),NOW()),(13032,108,3,'brand',0,'zara','Zara',NOW(),NOW()),
(13033,109,1,'color',5,'5','Marron',NOW(),NOW()),(13034,109,1,'color',3,'3','Gris',NOW(),NOW()),(13035,109,2,'size',13,'13','Unique',NOW(),NOW()),(13036,109,3,'brand',0,'mango','Mango',NOW(),NOW()),
(13037,110,1,'color',2,'2','Blanc',NOW(),NOW()),(13038,110,1,'color',1,'1','Noir',NOW(),NOW()),(13039,110,2,'size',13,'13','Unique',NOW(),NOW()),(13040,110,3,'brand',0,'uniqlo','Uniqlo',NOW(),NOW()),
(13041,111,1,'color',5,'5','Marron',NOW(),NOW()),(13042,111,1,'color',1,'1','Noir',NOW(),NOW()),(13043,111,2,'size',13,'13','Unique',NOW(),NOW()),(13044,111,3,'brand',0,'levis','Levis',NOW(),NOW()),
(13045,112,1,'color',4,'4','Bleu',NOW(),NOW()),(13046,112,1,'color',1,'1','Noir',NOW(),NOW()),(13047,112,2,'size',10,'10','40',NOW(),NOW()),(13048,112,3,'brand',0,'levis','Levis',NOW(),NOW());