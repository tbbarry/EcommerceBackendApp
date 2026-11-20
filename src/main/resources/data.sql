-- ============================================================
-- USERS
-- ============================================================

INSERT INTO users
(id, created_at, updated_at, email, firstname, lastname, password, role, enabled)
VALUES
(1001, NOW(), NOW(), 'e@gmail.com', 'Alice', 'Martin',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'ADMIN', true),

(1002, NOW(), NOW(), 't@test.com', 'Karim', 'Benali',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'USER', true),

(1003, NOW(), NOW(), 'sophie@test.com', 'Sophie', 'Durand',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'USER', true);

-- ============================================================
-- CATEGORIES
-- ============================================================

INSERT INTO categories
(id, category_id, created_at, updated_at, description, name)
VALUES

-- Niveau 1
(1001,NULL,NOW(),NOW(),'Vêtements pour femme','Femme'),
(1002,NULL,NOW(),NOW(),'Vêtements pour homme','Homme'),
(1003,NULL,NOW(),NOW(),'Vêtements pour enfant','Enfant'),
(1004,NULL,NOW(),NOW(),'Accessoires de mode','Accessoires'),

-- Femme
(1010,1001,NOW(),NOW(),'Vêtements femme','Vêtements'),
(1011,1001,NOW(),NOW(),'Chaussures femme','Chaussures'),
(1012,1001,NOW(),NOW(),'Accessoires femme','Accessoires'),

(1020,1010,NOW(),NOW(),'T-shirts femme','T-shirts'),
(1021,1010,NOW(),NOW(),'Sweats femme','Sweats'),
(1022,1010,NOW(),NOW(),'Robes femme','Robes'),
(1023,1010,NOW(),NOW(),'Pantalons femme','Pantalons'),
(1024,1011,NOW(),NOW(),'Sneakers femme','Sneakers'),

-- Homme
(1030,1002,NOW(),NOW(),'Vêtements homme','Vêtements'),
(1031,1002,NOW(),NOW(),'Chaussures homme','Chaussures'),
(1032,1002,NOW(),NOW(),'Accessoires homme','Accessoires'),

(1040,1030,NOW(),NOW(),'T-shirts homme','T-shirts'),
(1041,1030,NOW(),NOW(),'Sweats homme','Sweats'),
(1042,1030,NOW(),NOW(),'Chemises homme','Chemises'),
(1043,1030,NOW(),NOW(),'Pantalons homme','Pantalons'),
(1044,1031,NOW(),NOW(),'Sneakers homme','Sneakers'),

-- Enfant
(1050,1003,NOW(),NOW(),'Vêtements enfant','Vêtements'),
(1051,1003,NOW(),NOW(),'Chaussures enfant','Chaussures'),
(1052,1003,NOW(),NOW(),'Accessoires enfant','Accessoires'),

(1060,1050,NOW(),NOW(),'T-shirts enfant','T-shirts'),
(1061,1050,NOW(),NOW(),'Sweats enfant','Sweats'),
(1062,1050,NOW(),NOW(),'Pantalons enfant','Pantalons'),
(1063,1051,NOW(),NOW(),'Sneakers enfant','Sneakers'),

-- Accessoires
(1070,1004,NOW(),NOW(),'Sacs','Sacs'),
(1071,1004,NOW(),NOW(),'Ceintures','Ceintures'),
(1072,1004,NOW(),NOW(),'Bijoux','Bijoux'),
(1073,1004,NOW(),NOW(),'Chapeaux','Chapeaux');

-- ============================================================
-- PRODUCTS
-- ============================================================

INSERT INTO products
(id, category_id, price, created_at, updated_at, brand, description, name, slug)
VALUES
(1001,1020,29.99,NOW(),NOW(),'Nike',
'T-shirt Nike en coton confortable',
'T-shirt Nike Basic',
't-shirt-nike-basic'),

(1002,1044,79.99,NOW(),NOW(),'Adidas',
'Sneakers Adidas légères',
'Adidas Runner',
'adidas-runner'),

(1003,1041,59.99,NOW(),NOW(),'Puma',
'Sweat à capuche Puma',
'Hoodie Puma Classic',
'hoodie-puma-classic'),

(1004,1070,39.99,NOW(),NOW(),'Zara',
'Sac noir élégant',
'Sac Zara Noir',
'sac-zara-noir'),

(1005,1043,69.99,NOW(),NOW(),'Levis',
'Jean slim bleu',
'Jean Levis Slim',
'jean-levis-slim'),

(1006,1004,25.00,NOW(),NOW(),'NiceShop',
'Carte cadeau digitale',
'Carte Cadeau 25 EUR',
'carte-cadeau-25-eur'),

(1007,1071,44.99,NOW(),NOW(),'Mango',
'Ceinture cuir classique',
'Ceinture Cuir Classique',
'ceinture-cuir-classique'),

(1008,1073,19.99,NOW(),NOW(),'Uniqlo',
'Bonnet unisexe',
'Bonnet Minimal',
'bonnet-minimal');

-- ============================================================
-- ATTRIBUTES
-- ============================================================

INSERT INTO attributes
(id, created_at, updated_at, name)
VALUES
(1001,NOW(),NOW(),'Couleur'),
(1002,NOW(),NOW(),'Taille');

-- ============================================================
-- ATTRIBUTE VALUES
-- ============================================================

INSERT INTO attribute_values
(id, attribute_id, created_at, updated_at, value)
VALUES
(1001,1001,NOW(),NOW(),'Noir'),
(1002,1001,NOW(),NOW(),'Blanc'),
(1003,1001,NOW(),NOW(),'Gris'),
(1004,1001,NOW(),NOW(),'Bleu'),
(1005,1001,NOW(),NOW(),'Marron'),

(1006,1002,NOW(),NOW(),'S'),
(1007,1002,NOW(),NOW(),'M'),
(1008,1002,NOW(),NOW(),'L'),
(1009,1002,NOW(),NOW(),'38'),
(1010,1002,NOW(),NOW(),'40'),
(1011,1002,NOW(),NOW(),'42'),
(1012,1002,NOW(),NOW(),'43'),
(1013,1002,NOW(),NOW(),'Unique');

-- ============================================================
-- VARIANTS
-- ============================================================

INSERT INTO variants
(id, price, product_id, created_at, updated_at, sku)
VALUES
(1001,29.99,1001,NOW(),NOW(),'NIKE-TS-BLK-S'),
(1002,29.99,1001,NOW(),NOW(),'NIKE-TS-BLK-M'),
(1003,29.99,1001,NOW(),NOW(),'NIKE-TS-WHT-M'),

(1004,79.99,1002,NOW(),NOW(),'ADI-RUN-WHT-42'),
(1005,79.99,1002,NOW(),NOW(),'ADI-RUN-BLK-43'),

(1006,59.99,1003,NOW(),NOW(),'PUMA-HOOD-GRY-M'),
(1007,59.99,1003,NOW(),NOW(),'PUMA-HOOD-BLK-L'),

(1008,39.99,1004,NOW(),NOW(),'ZARA-BAG-BLK-U'),

(1009,69.99,1005,NOW(),NOW(),'LEVIS-JEAN-BLU-38'),
(1010,69.99,1005,NOW(),NOW(),'LEVIS-JEAN-BLU-40'),

(1011,25.00,1006,NOW(),NOW(),'GIFT-CARD-25-001'),

(1012,44.99,1007,NOW(),NOW(),'MANGO-BELT-BRN-U'),

(1013,19.99,1008,NOW(),NOW(),'UNIQLO-BONNET-GRY-U');

-- ============================================================
-- VARIANT ATTRIBUTE VALUES
-- ============================================================

INSERT INTO variant_attribute_values
(id, variant_id, attribute_value_id, created_at, updated_at)
VALUES
(1001,1001,1001,NOW(),NOW()),
(1002,1001,1006,NOW(),NOW()),

(1003,1002,1001,NOW(),NOW()),
(1004,1002,1007,NOW(),NOW()),

(1005,1003,1002,NOW(),NOW()),
(1006,1003,1007,NOW(),NOW()),

(1007,1004,1002,NOW(),NOW()),
(1008,1004,1011,NOW(),NOW()),

(1009,1005,1001,NOW(),NOW()),
(1010,1005,1012,NOW(),NOW()),

(1011,1006,1003,NOW(),NOW()),
(1012,1006,1007,NOW(),NOW()),

(1013,1007,1001,NOW(),NOW()),
(1014,1007,1008,NOW(),NOW()),

(1015,1008,1001,NOW(),NOW()),
(1016,1008,1013,NOW(),NOW()),

(1017,1009,1004,NOW(),NOW()),
(1018,1009,1009,NOW(),NOW()),

(1019,1010,1004,NOW(),NOW()),
(1020,1010,1010,NOW(),NOW()),

(1021,1012,1005,NOW(),NOW()),
(1022,1012,1013,NOW(),NOW()),

(1023,1013,1003,NOW(),NOW()),
(1024,1013,1013,NOW(),NOW());

-- ============================================================
-- STOCKS (DISPONIBLES POUR TEST)
-- ============================================================

INSERT INTO stocks
(id, quantity, reserved_quantity, variant_id, created_at, updated_at)
VALUES
(1001, 14, 0, 1001, NOW(), NOW()),
(1002, 11, 0, 1002, NOW(), NOW()),
(1003, 7, 0, 1003, NOW(), NOW()),
(1004, 8, 0, 1004, NOW(), NOW()),
(1005, 10, 0, 1005, NOW(), NOW()),
(1006, 9, 0, 1006, NOW(), NOW()),
(1007, 6, 0, 1007, NOW(), NOW()),
(1008, 5, 0, 1008, NOW(), NOW()),
(1009, 12, 0, 1009, NOW(), NOW()),
(1010, 9, 0, 1010, NOW(), NOW()),
(1011, 20, 0, 1011, NOW(), NOW()),
(1012, 13, 0, 1012, NOW(), NOW()),
(1013, 15, 0, 1013, NOW(), NOW());

-- ============================================================
-- PRODUCT IMAGES (URLS REELLES ACCESSIBLES)
-- ============================================================

INSERT INTO product_images
(id, url, object_key, alt, is_main, display_order, product_id, visual_attribute_value_id, created_at, updated_at)
VALUES
-- Produit 1 : T-shirt Nike Basic
(1001, 'https://picsum.photos/seed/tshirt-nike-main/1200/1200', null, 'T-shirt Nike Basic vue principale', true, 0, 1001, null, NOW(), NOW()),
(1002, 'https://picsum.photos/seed/tshirt-nike-noir/1200/1200', null, 'T-shirt Nike Basic noir', false, 1, 1001, 1001, NOW(), NOW()),
(1003, 'https://picsum.photos/seed/tshirt-nike-blanc/1200/1200', null, 'T-shirt Nike Basic blanc', false, 2, 1001, 1002, NOW(), NOW()),

-- Produit 2 : Adidas Runner
(1004, 'https://picsum.photos/seed/adidas-runner-main/1200/1200', null, 'Adidas Runner vue principale', true, 0, 1002, null, NOW(), NOW()),
(1005, 'https://picsum.photos/seed/adidas-runner-blanc/1200/1200', null, 'Adidas Runner blanc', false, 1, 1002, 1002, NOW(), NOW()),
(1006, 'https://picsum.photos/seed/adidas-runner-noir/1200/1200', null, 'Adidas Runner noir', false, 2, 1002, 1001, NOW(), NOW()),

-- Produit 3 : Hoodie Puma Classic
(1007, 'https://picsum.photos/seed/hoodie-puma-main/1200/1200', null, 'Hoodie Puma Classic vue principale', true, 0, 1003, null, NOW(), NOW()),
(1008, 'https://picsum.photos/seed/hoodie-puma-gris/1200/1200', null, 'Hoodie Puma Classic gris', false, 1, 1003, 1003, NOW(), NOW()),
(1009, 'https://picsum.photos/seed/hoodie-puma-noir/1200/1200', null, 'Hoodie Puma Classic noir', false, 2, 1003, 1001, NOW(), NOW()),

-- Produit 4 : Sac Zara Noir
(1010, 'https://picsum.photos/seed/sac-zara-main/1200/1200', null, 'Sac Zara Noir vue principale', true, 0, 1004, null, NOW(), NOW()),
(1011, 'https://picsum.photos/seed/sac-zara-noir/1200/1200', null, 'Sac Zara Noir détail', false, 1, 1004, 1001, NOW(), NOW()),

-- Produit 5 : Jean Levis Slim
(1012, 'https://picsum.photos/seed/jean-levis-main/1200/1200', null, 'Jean Levis Slim vue principale', true, 0, 1005, null, NOW(), NOW()),
(1013, 'https://picsum.photos/seed/jean-levis-bleu/1200/1200', null, 'Jean Levis Slim bleu', false, 1, 1005, 1004, NOW(), NOW()),

-- Produit 6 : Carte cadeau
(1014, 'https://picsum.photos/seed/gift-card-main/1200/1200', null, 'Carte cadeau 25 EUR', true, 0, 1006, null, NOW(), NOW()),

-- Produit 7 : Ceinture Mango
(1015, 'https://picsum.photos/seed/ceinture-mango-main/1200/1200', null, 'Ceinture Cuir Classique vue principale', true, 0, 1007, null, NOW(), NOW()),
(1016, 'https://picsum.photos/seed/ceinture-mango-marron/1200/1200', null, 'Ceinture Cuir Classique marron', false, 1, 1007, 1005, NOW(), NOW()),

-- Produit 8 : Bonnet Uniqlo
(1017, 'https://picsum.photos/seed/bonnet-uniqlo-main/1200/1200', null, 'Bonnet Minimal vue principale', true, 0, 1008, null, NOW(), NOW()),
(1018, 'https://picsum.photos/seed/bonnet-uniqlo-gris/1200/1200', null, 'Bonnet Minimal gris', false, 1, 1008, 1003, NOW(), NOW());

-- ============================================================
-- PROMOS (POUR TEST CATALOGUE)
-- ============================================================

INSERT INTO promos
(id, created_at, updated_at, code, discount_value, start_date, end_date, is_active)
VALUES
(1001, NOW(), NOW(), 'GLOBAL-TSHIRT-20', 20.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(1002, NOW(), NOW(), 'GLOBAL-LEVIS-15', 15.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(1003, NOW(), NOW(), 'VAR-ADIDAS-10', 10.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(1004, NOW(), NOW(), 'VAR-ADIDAS-35', 35.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true),
(1005, NOW(), NOW(), 'VAR-LEVIS-30', 30.00, NOW() - INTERVAL '7 day', NOW() + INTERVAL '21 day', true);

-- ============================================================
-- PRODUIT <-> PROMO (REMISE GLOBALE)
-- ============================================================

INSERT INTO product_promos
(id, created_at, updated_at, product_id, promo_id)
VALUES
(1001, NOW(), NOW(), 1001, 1001),
(1002, NOW(), NOW(), 1005, 1002);

-- ============================================================
-- VARIANTE <-> PROMO (REMISE SPECIFIQUE)
-- ============================================================

INSERT INTO variant_promos
(id, created_at, updated_at, variant_id, promo_id)
VALUES
(1001, NOW(), NOW(), 1004, 1003),
(1002, NOW(), NOW(), 1005, 1004),
(1003, NOW(), NOW(), 1009, 1005);

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

(1001,1001,NOW(),NOW(),
'T-shirt Nike Basic',
't-shirt-nike-basic',
23.99,
'Nike',
'https://picsum.photos/seed/tshirt-nike-main/1200/1200',
1020,
'T-shirts',
29.99, 29.99, 23.99, 23.99,
23.99, 20.00, 20.00,
20.00, true, 'GLOBAL'),

(1002,1002,NOW(),NOW(),
'Adidas Runner',
'adidas-runner',
51.99,
'Adidas',
'https://picsum.photos/seed/adidas-runner-main/1200/1200',
1044,
'Sneakers',
79.99, 79.99, 51.99, 71.99,
51.99, 0.00, 10.00,
35.00, true, 'VARIANT'),

(1003,1003,NOW(),NOW(),
'Hoodie Puma Classic',
'hoodie-puma-classic',
59.99,
'Puma',
'https://picsum.photos/seed/hoodie-puma-main/1200/1200',
1041,
'Sweats',
59.99, 59.99, 59.99, 59.99,
59.99, 0.00, 0.00,
0.00, false, 'NONE'),

(1004,1004,NOW(),NOW(),
'Sac Zara Noir',
'sac-zara-noir',
39.99,
'Zara',
'https://picsum.photos/seed/sac-zara-main/1200/1200',
1070,
'Sacs',
39.99, 39.99, 39.99, 39.99,
39.99, 0.00, 0.00,
0.00, false, 'NONE'),

(1005,1005,NOW(),NOW(),
'Jean Levis Slim',
'jean-levis-slim',
48.99,
'Levis',
'https://picsum.photos/seed/jean-levis-main/1200/1200',
1043,
'Pantalons',
69.99, 69.99, 48.99, 59.49,
48.99, 15.00, 15.00,
30.00, true, 'VARIANT'),

(1006,1006,NOW(),NOW(),
'Carte Cadeau 25 EUR',
'carte-cadeau-25-eur',
25.00,
'NiceShop',
'https://picsum.photos/seed/gift-card-main/1200/1200',
1004,
'Accessoires',
25.00, 25.00, 25.00, 25.00,
25.00, 0.00, 0.00,
0.00, false, 'NONE'),

(1007,1007,NOW(),NOW(),
'Ceinture Cuir Classique',
'ceinture-cuir-classique',
44.99,
'Mango',
'https://picsum.photos/seed/ceinture-mango-main/1200/1200',
1071,
'Ceintures',
44.99, 44.99, 44.99, 44.99,
44.99, 0.00, 0.00,
0.00, false, 'NONE'),

(1008,1008,NOW(),NOW(),
'Bonnet Minimal',
'bonnet-minimal',
19.99,
'Uniqlo',
'https://picsum.photos/seed/bonnet-uniqlo-main/1200/1200',
1073,
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
(1001,1001,1,'color',1001,'1','Noir',NOW(),NOW()),
(1002,1001,1,'color',1002,'2','Blanc',NOW(),NOW()),
(1003,1001,2,'size',1006,'6','S',NOW(),NOW()),
(1004,1001,2,'size',1007,'7','M',NOW(),NOW()),
(1005,1001,3,'brand',0,'nike','Nike',NOW(),NOW()),

-- Produit 2
(1006,1002,1,'color',1002,'2','Blanc',NOW(),NOW()),
(1007,1002,1,'color',1001,'1','Noir',NOW(),NOW()),
(1008,1002,2,'size',1011,'11','42',NOW(),NOW()),
(1009,1002,2,'size',1012,'12','43',NOW(),NOW()),
(1010,1002,3,'brand',0,'adidas','Adidas',NOW(),NOW()),

-- Produit 3
(1011,1003,1,'color',1003,'3','Gris',NOW(),NOW()),
(1012,1003,1,'color',1001,'1','Noir',NOW(),NOW()),
(1013,1003,2,'size',1007,'7','M',NOW(),NOW()),
(1014,1003,2,'size',1008,'8','L',NOW(),NOW()),
(1015,1003,3,'brand',0,'puma','Puma',NOW(),NOW()),

-- Produit 4
(1016,1004,1,'color',1001,'1','Noir',NOW(),NOW()),
(1017,1004,2,'size',1013,'13','Unique',NOW(),NOW()),
(1018,1004,3,'brand',0,'zara','Zara',NOW(),NOW()),

-- Produit 5
(1019,1005,1,'color',1004,'4','Bleu',NOW(),NOW()),
(1020,1005,2,'size',1009,'9','38',NOW(),NOW()),
(1021,1005,2,'size',1010,'10','40',NOW(),NOW()),
(1022,1005,3,'brand',0,'levis','Levis',NOW(),NOW()),

-- Produit 7
(1023,1007,1,'color',1005,'5','Marron',NOW(),NOW()),
(1024,1007,2,'size',1013,'13','Unique',NOW(),NOW()),
(1025,1007,3,'brand',0,'mango','Mango',NOW(),NOW()),

-- Produit 8
(1026,1008,1,'color',1003,'3','Gris',NOW(),NOW()),
(1027,1008,2,'size',1013,'13','Unique',NOW(),NOW()),
(1028,1008,3,'brand',0,'uniqlo','Uniqlo',NOW(),NOW());

-- ============================================================
-- EXTRA PRODUCTS (RICHER SEED DATASET)
-- ============================================================

INSERT INTO products
(id, category_id, price, created_at, updated_at, brand, description, name, slug)
VALUES
(1101,1022,49.99,NOW(),NOW(),'Mango','Robe legere en lin coupe midi','Robe Linen Breeze','robe-linen-breeze'),
(1102,1042,54.99,NOW(),NOW(),'Celio','Chemise oxford ajustee facile a porter','Chemise Oxford Modern','chemise-oxford-modern'),
(1103,1021,64.99,NOW(),NOW(),'Puma','Sweat crop doux pour un style casual','Sweat Crop Soft Touch','sweat-crop-soft-touch'),
(1104,1024,89.99,NOW(),NOW(),'Adidas','Sneakers femme semelle confortable','Sneakers Urban Pulse Femme','sneakers-urban-pulse-femme'),
(1105,1040,24.99,NOW(),NOW(),'Uniqlo','T-shirt homme basique coton premium','T-shirt Essential Homme','t-shirt-essential-homme'),
(1106,1062,34.99,NOW(),NOW(),'Zara Kids','Jogger enfant souple et resistant','Pantalon Jogger Kids','pantalon-jogger-kids'),
(1107,1063,59.99,NOW(),NOW(),'Nike Kids','Sneakers enfant legeres pour tous les jours','Sneakers Kids Sprint','sneakers-kids-sprint'),
(1108,1070,69.99,NOW(),NOW(),'Zara','Sac cabas grand format usage quotidien','Sac Cabas City','sac-cabas-city'),
(1109,1072,29.99,NOW(),NOW(),'Mango','Collier minimal finition doree','Collier Minimal Dore','collier-minimal-dore'),
(1110,1073,22.99,NOW(),NOW(),'Uniqlo','Bob coton unisexe leger','Bob Coton Weekend','bob-coton-weekend'),
(1111,1071,39.99,NOW(),NOW(),'Levis','Ceinture cuir boucle metal classique','Ceinture Classique Homme','ceinture-classique-homme'),
(1112,1023,74.99,NOW(),NOW(),'Levis','Jean femme mom fit taille haute','Jean Mom Fit Femme','jean-mom-fit-femme');

INSERT INTO variants
(id, price, product_id, created_at, updated_at, sku)
VALUES
(1101,49.99,1101,NOW(),NOW(),'MNG-ROBE-BLU-S'),
(1102,49.99,1101,NOW(),NOW(),'MNG-ROBE-BLU-M'),
(1103,54.99,1101,NOW(),NOW(),'MNG-ROBE-BLK-L'),

(1104,54.99,1102,NOW(),NOW(),'CELIO-OXF-WHT-M'),
(1105,54.99,1102,NOW(),NOW(),'CELIO-OXF-WHT-L'),
(1106,59.99,1102,NOW(),NOW(),'CELIO-OXF-BLU-L'),

(1107,64.99,1103,NOW(),NOW(),'PUMA-CROP-GRY-S'),
(1108,64.99,1103,NOW(),NOW(),'PUMA-CROP-GRY-M'),
(1109,69.99,1103,NOW(),NOW(),'PUMA-CROP-BLK-L'),

(1110,89.99,1104,NOW(),NOW(),'ADI-UPF-WHT-42'),
(1111,89.99,1104,NOW(),NOW(),'ADI-UPF-WHT-43'),
(1112,94.99,1104,NOW(),NOW(),'ADI-UPF-BLK-43'),

(1113,24.99,1105,NOW(),NOW(),'UNIQ-TSH-WHT-S'),
(1114,24.99,1105,NOW(),NOW(),'UNIQ-TSH-WHT-M'),
(1115,26.99,1105,NOW(),NOW(),'UNIQ-TSH-BLK-L'),

(1116,34.99,1106,NOW(),NOW(),'ZKID-JOG-GRY-38'),
(1117,34.99,1106,NOW(),NOW(),'ZKID-JOG-GRY-40'),
(1118,36.99,1106,NOW(),NOW(),'ZKID-JOG-BLU-40'),

(1119,59.99,1107,NOW(),NOW(),'NKID-SPR-WHT-42'),
(1120,59.99,1107,NOW(),NOW(),'NKID-SPR-WHT-43'),
(1121,62.99,1107,NOW(),NOW(),'NKID-SPR-BLK-43'),

(1122,69.99,1108,NOW(),NOW(),'ZARA-CAB-BRN-U'),
(1123,72.99,1108,NOW(),NOW(),'ZARA-CAB-BLK-U'),

(1124,29.99,1109,NOW(),NOW(),'MNG-COLL-GOLD-U'),
(1125,32.99,1109,NOW(),NOW(),'MNG-COLL-SILV-U'),

(1126,22.99,1110,NOW(),NOW(),'UNIQ-BOB-BEI-U'),
(1127,22.99,1110,NOW(),NOW(),'UNIQ-BOB-BLK-U'),

(1128,39.99,1111,NOW(),NOW(),'LEVIS-BEL-BRN-U'),
(1129,39.99,1111,NOW(),NOW(),'LEVIS-BEL-BLK-U'),

(1130,74.99,1112,NOW(),NOW(),'LEVIS-MOM-BLU-38'),
(1131,74.99,1112,NOW(),NOW(),'LEVIS-MOM-BLU-40'),
(1132,79.99,1112,NOW(),NOW(),'LEVIS-MOM-BLK-40');

INSERT INTO variant_attribute_values
(id, variant_id, attribute_value_id, created_at, updated_at)
VALUES
(5001,1101,1004,NOW(),NOW()),(5002,1101,1006,NOW(),NOW()),
(5003,1102,1004,NOW(),NOW()),(5004,1102,1007,NOW(),NOW()),
(5005,1103,1001,NOW(),NOW()),(5006,1103,1008,NOW(),NOW()),

(5007,1104,1002,NOW(),NOW()),(5008,1104,1007,NOW(),NOW()),
(5009,1105,1002,NOW(),NOW()),(5010,1105,1008,NOW(),NOW()),
(5011,1106,1004,NOW(),NOW()),(5012,1106,1008,NOW(),NOW()),

(5013,1107,1003,NOW(),NOW()),(5014,1107,1006,NOW(),NOW()),
(5015,1108,1003,NOW(),NOW()),(5016,1108,1007,NOW(),NOW()),
(5017,1109,1001,NOW(),NOW()),(5018,1109,1008,NOW(),NOW()),

(5019,1110,1002,NOW(),NOW()),(5020,1110,1011,NOW(),NOW()),
(5021,1111,1002,NOW(),NOW()),(5022,1111,1012,NOW(),NOW()),
(5023,1112,1001,NOW(),NOW()),(5024,1112,1012,NOW(),NOW()),

(5025,1113,1002,NOW(),NOW()),(5026,1113,1006,NOW(),NOW()),
(5027,1114,1002,NOW(),NOW()),(5028,1114,1007,NOW(),NOW()),
(5029,1115,1001,NOW(),NOW()),(5030,1115,1008,NOW(),NOW()),

(5031,1116,1003,NOW(),NOW()),(5032,1116,1009,NOW(),NOW()),
(5033,1117,1003,NOW(),NOW()),(5034,1117,1010,NOW(),NOW()),
(5035,1118,1004,NOW(),NOW()),(5036,1118,1010,NOW(),NOW()),

(5037,1119,1002,NOW(),NOW()),(5038,1119,1011,NOW(),NOW()),
(5039,1120,1002,NOW(),NOW()),(5040,1120,1012,NOW(),NOW()),
(5041,1121,1001,NOW(),NOW()),(5042,1121,1012,NOW(),NOW()),

(5043,1122,1005,NOW(),NOW()),(5044,1122,1013,NOW(),NOW()),
(5045,1123,1001,NOW(),NOW()),(5046,1123,1013,NOW(),NOW()),

(5047,1124,1005,NOW(),NOW()),(5048,1124,1013,NOW(),NOW()),
(5049,1125,1003,NOW(),NOW()),(5050,1125,1013,NOW(),NOW()),

(5051,1126,1002,NOW(),NOW()),(5052,1126,1013,NOW(),NOW()),
(5053,1127,1001,NOW(),NOW()),(5054,1127,1013,NOW(),NOW()),

(5055,1128,1005,NOW(),NOW()),(5056,1128,1013,NOW(),NOW()),
(5057,1129,1001,NOW(),NOW()),(5058,1129,1013,NOW(),NOW()),

(5059,1130,1004,NOW(),NOW()),(5060,1130,1009,NOW(),NOW()),
(5061,1131,1004,NOW(),NOW()),(5062,1131,1010,NOW(),NOW()),
(5063,1132,1001,NOW(),NOW()),(5064,1132,1010,NOW(),NOW());

INSERT INTO stocks
(id, quantity, reserved_quantity, variant_id, created_at, updated_at)
VALUES
(7001,18,0,1101,NOW(),NOW()),(7002,15,0,1102,NOW(),NOW()),(7003,9,0,1103,NOW(),NOW()),
(7004,16,0,1104,NOW(),NOW()),(7005,11,0,1105,NOW(),NOW()),(7006,7,0,1106,NOW(),NOW()),
(7007,13,0,1107,NOW(),NOW()),(7008,12,0,1108,NOW(),NOW()),(7009,8,0,1109,NOW(),NOW()),
(7010,14,0,1110,NOW(),NOW()),(7011,10,0,1111,NOW(),NOW()),(7012,6,0,1112,NOW(),NOW()),
(7013,20,0,1113,NOW(),NOW()),(7014,17,0,1114,NOW(),NOW()),(7015,9,0,1115,NOW(),NOW()),
(7016,19,0,1116,NOW(),NOW()),(7017,16,0,1117,NOW(),NOW()),(7018,10,0,1118,NOW(),NOW()),
(7019,13,0,1119,NOW(),NOW()),(7020,11,0,1120,NOW(),NOW()),(7021,7,0,1121,NOW(),NOW()),
(7022,12,0,1122,NOW(),NOW()),(7023,8,0,1123,NOW(),NOW()),
(7024,22,0,1124,NOW(),NOW()),(7025,14,0,1125,NOW(),NOW()),
(7026,15,0,1126,NOW(),NOW()),(7027,10,0,1127,NOW(),NOW()),
(7028,18,0,1128,NOW(),NOW()),(7029,13,0,1129,NOW(),NOW()),
(7030,12,0,1130,NOW(),NOW()),(7031,10,0,1131,NOW(),NOW()),(7032,6,0,1132,NOW(),NOW());

INSERT INTO product_images
(id, url, object_key, alt, is_main, display_order, product_id, visual_attribute_value_id, created_at, updated_at)
VALUES
(9001,'https://picsum.photos/seed/fashion-101/1200/1200',null,'Robe Linen Breeze vue principale',true,0,1101,null,NOW(),NOW()),
(9002,'https://picsum.photos/seed/fashion-102/1200/1200',null,'Robe Linen Breeze bleu',false,1,1101,1004,NOW(),NOW()),
(9003,'https://picsum.photos/seed/fashion-103/1200/1200',null,'Robe Linen Breeze noir',false,2,1101,1001,NOW(),NOW()),

(9004,'https://picsum.photos/seed/fashion-104/1200/1200',null,'Chemise Oxford Modern vue principale',true,0,1102,null,NOW(),NOW()),
(9005,'https://picsum.photos/seed/fashion-105/1200/1200',null,'Chemise Oxford Modern blanc',false,1,1102,1002,NOW(),NOW()),
(9006,'https://picsum.photos/seed/fashion-106/1200/1200',null,'Chemise Oxford Modern bleu',false,2,1102,1004,NOW(),NOW()),

(9007,'https://picsum.photos/seed/fashion-107/1200/1200',null,'Sweat Crop Soft Touch vue principale',true,0,1103,null,NOW(),NOW()),
(9008,'https://picsum.photos/seed/fashion-108/1200/1200',null,'Sweat Crop Soft Touch gris',false,1,1103,1003,NOW(),NOW()),
(9009,'https://picsum.photos/seed/fashion-109/1200/1200',null,'Sweat Crop Soft Touch noir',false,2,1103,1001,NOW(),NOW()),

(9010,'https://picsum.photos/seed/fashion-110/1200/1200',null,'Sneakers Urban Pulse Femme vue principale',true,0,1104,null,NOW(),NOW()),
(9011,'https://picsum.photos/seed/fashion-111/1200/1200',null,'Sneakers Urban Pulse Femme blanc',false,1,1104,1002,NOW(),NOW()),
(9012,'https://picsum.photos/seed/fashion-112/1200/1200',null,'Sneakers Urban Pulse Femme noir',false,2,1104,1001,NOW(),NOW()),

(9013,'https://picsum.photos/seed/fashion-113/1200/1200',null,'T-shirt Essential Homme vue principale',true,0,1105,null,NOW(),NOW()),
(9014,'https://picsum.photos/seed/fashion-114/1200/1200',null,'T-shirt Essential Homme blanc',false,1,1105,1002,NOW(),NOW()),
(9015,'https://picsum.photos/seed/fashion-115/1200/1200',null,'T-shirt Essential Homme noir',false,2,1105,1001,NOW(),NOW()),

(9016,'https://picsum.photos/seed/fashion-116/1200/1200',null,'Pantalon Jogger Kids vue principale',true,0,1106,null,NOW(),NOW()),
(9017,'https://picsum.photos/seed/fashion-117/1200/1200',null,'Pantalon Jogger Kids gris',false,1,1106,1003,NOW(),NOW()),
(9018,'https://picsum.photos/seed/fashion-118/1200/1200',null,'Pantalon Jogger Kids bleu',false,2,1106,1004,NOW(),NOW()),

(9019,'https://picsum.photos/seed/fashion-119/1200/1200',null,'Sneakers Kids Sprint vue principale',true,0,1107,null,NOW(),NOW()),
(9020,'https://picsum.photos/seed/fashion-120/1200/1200',null,'Sneakers Kids Sprint blanc',false,1,1107,1002,NOW(),NOW()),
(9021,'https://picsum.photos/seed/fashion-121/1200/1200',null,'Sneakers Kids Sprint noir',false,2,1107,1001,NOW(),NOW()),

(9022,'https://picsum.photos/seed/fashion-122/1200/1200',null,'Sac Cabas City vue principale',true,0,1108,null,NOW(),NOW()),
(9023,'https://picsum.photos/seed/fashion-123/1200/1200',null,'Sac Cabas City marron',false,1,1108,1005,NOW(),NOW()),
(9024,'https://picsum.photos/seed/fashion-124/1200/1200',null,'Sac Cabas City noir',false,2,1108,1001,NOW(),NOW()),

(9025,'https://picsum.photos/seed/fashion-125/1200/1200',null,'Collier Minimal Dore vue principale',true,0,1109,null,NOW(),NOW()),
(9026,'https://picsum.photos/seed/fashion-126/1200/1200',null,'Collier Minimal Dore dore',false,1,1109,1005,NOW(),NOW()),
(9027,'https://picsum.photos/seed/fashion-127/1200/1200',null,'Collier Minimal Dore argent',false,2,1109,1003,NOW(),NOW()),

(9028,'https://picsum.photos/seed/fashion-128/1200/1200',null,'Bob Coton Weekend vue principale',true,0,1110,null,NOW(),NOW()),
(9029,'https://picsum.photos/seed/fashion-129/1200/1200',null,'Bob Coton Weekend beige',false,1,1110,1002,NOW(),NOW()),
(9030,'https://picsum.photos/seed/fashion-130/1200/1200',null,'Bob Coton Weekend noir',false,2,1110,1001,NOW(),NOW()),

(9031,'https://picsum.photos/seed/fashion-131/1200/1200',null,'Ceinture Classique Homme vue principale',true,0,1111,null,NOW(),NOW()),
(9032,'https://picsum.photos/seed/fashion-132/1200/1200',null,'Ceinture Classique Homme marron',false,1,1111,1005,NOW(),NOW()),
(9033,'https://picsum.photos/seed/fashion-133/1200/1200',null,'Ceinture Classique Homme noir',false,2,1111,1001,NOW(),NOW()),

(9034,'https://picsum.photos/seed/fashion-134/1200/1200',null,'Jean Mom Fit Femme vue principale',true,0,1112,null,NOW(),NOW()),
(9035,'https://picsum.photos/seed/fashion-135/1200/1200',null,'Jean Mom Fit Femme bleu',false,1,1112,1004,NOW(),NOW()),
(9036,'https://picsum.photos/seed/fashion-136/1200/1200',null,'Jean Mom Fit Femme noir',false,2,1112,1001,NOW(),NOW());

INSERT INTO catalog_product
(id, product_id, created_at, updated_at, name, slug, price,
 brand, image_url, category_id, category_name,
 base_price_min, base_price_max, final_price_min, final_price_max,
 display_price, discount_percent_global, discount_percent_min,
 discount_percent_max, has_discount, promo_scope)
VALUES
(1101,1101,NOW(),NOW(),'Robe Linen Breeze','robe-linen-breeze',49.99,'Mango','https://picsum.photos/seed/fashion-101/1200/1200',1022,'Robes',49.99,54.99,49.99,54.99,49.99,0.00,0.00,0.00,false,'NONE'),
(1102,1102,NOW(),NOW(),'Chemise Oxford Modern','chemise-oxford-modern',54.99,'Celio','https://picsum.photos/seed/fashion-104/1200/1200',1042,'Chemises',54.99,59.99,54.99,59.99,54.99,0.00,0.00,0.00,false,'NONE'),
(1103,1103,NOW(),NOW(),'Sweat Crop Soft Touch','sweat-crop-soft-touch',64.99,'Puma','https://picsum.photos/seed/fashion-107/1200/1200',1021,'Sweats',64.99,69.99,64.99,69.99,64.99,0.00,0.00,0.00,false,'NONE'),
(1104,1104,NOW(),NOW(),'Sneakers Urban Pulse Femme','sneakers-urban-pulse-femme',89.99,'Adidas','https://picsum.photos/seed/fashion-110/1200/1200',1024,'Sneakers',89.99,94.99,89.99,94.99,89.99,0.00,0.00,0.00,false,'NONE'),
(1105,1105,NOW(),NOW(),'T-shirt Essential Homme','t-shirt-essential-homme',24.99,'Uniqlo','https://picsum.photos/seed/fashion-113/1200/1200',1040,'T-shirts',24.99,26.99,24.99,26.99,24.99,0.00,0.00,0.00,false,'NONE'),
(1106,1106,NOW(),NOW(),'Pantalon Jogger Kids','pantalon-jogger-kids',34.99,'Zara Kids','https://picsum.photos/seed/fashion-116/1200/1200',1062,'Pantalons',34.99,36.99,34.99,36.99,34.99,0.00,0.00,0.00,false,'NONE'),
(1107,1107,NOW(),NOW(),'Sneakers Kids Sprint','sneakers-kids-sprint',59.99,'Nike Kids','https://picsum.photos/seed/fashion-119/1200/1200',1063,'Sneakers',59.99,62.99,59.99,62.99,59.99,0.00,0.00,0.00,false,'NONE'),
(1108,1108,NOW(),NOW(),'Sac Cabas City','sac-cabas-city',69.99,'Zara','https://picsum.photos/seed/fashion-122/1200/1200',1070,'Sacs',69.99,72.99,69.99,72.99,69.99,0.00,0.00,0.00,false,'NONE'),
(1109,1109,NOW(),NOW(),'Collier Minimal Dore','collier-minimal-dore',29.99,'Mango','https://picsum.photos/seed/fashion-125/1200/1200',1072,'Bijoux',29.99,32.99,29.99,32.99,29.99,0.00,0.00,0.00,false,'NONE'),
(1110,1110,NOW(),NOW(),'Bob Coton Weekend','bob-coton-weekend',22.99,'Uniqlo','https://picsum.photos/seed/fashion-128/1200/1200',1073,'Chapeaux',22.99,22.99,22.99,22.99,22.99,0.00,0.00,0.00,false,'NONE'),
(1111,1111,NOW(),NOW(),'Ceinture Classique Homme','ceinture-classique-homme',39.99,'Levis','https://picsum.photos/seed/fashion-131/1200/1200',1071,'Ceintures',39.99,39.99,39.99,39.99,39.99,0.00,0.00,0.00,false,'NONE'),
(1112,1112,NOW(),NOW(),'Jean Mom Fit Femme','jean-mom-fit-femme',74.99,'Levis','https://picsum.photos/seed/fashion-134/1200/1200',1023,'Pantalons',74.99,79.99,74.99,79.99,74.99,0.00,0.00,0.00,false,'NONE');

INSERT INTO catalog_product_facet
(id, product_id, facet_id, facet_code, facet_value_id, value_code, value_label, created_at, updated_at)
VALUES
(13001,1101,1,'color',1004,'4','Bleu',NOW(),NOW()),(13002,1101,1,'color',1001,'1','Noir',NOW(),NOW()),(13003,1101,2,'size',1007,'7','M',NOW(),NOW()),(13004,1101,3,'brand',0,'mango','Mango',NOW(),NOW()),
(13005,1102,1,'color',1002,'2','Blanc',NOW(),NOW()),(13006,1102,1,'color',1004,'4','Bleu',NOW(),NOW()),(13007,1102,2,'size',1008,'8','L',NOW(),NOW()),(13008,1102,3,'brand',0,'celio','Celio',NOW(),NOW()),
(13009,1103,1,'color',1003,'3','Gris',NOW(),NOW()),(13010,1103,1,'color',1001,'1','Noir',NOW(),NOW()),(13011,1103,2,'size',1007,'7','M',NOW(),NOW()),(13012,1103,3,'brand',0,'puma','Puma',NOW(),NOW()),
(13013,1104,1,'color',1002,'2','Blanc',NOW(),NOW()),(13014,1104,1,'color',1001,'1','Noir',NOW(),NOW()),(13015,1104,2,'size',1012,'12','43',NOW(),NOW()),(13016,1104,3,'brand',0,'adidas','Adidas',NOW(),NOW()),
(13017,1105,1,'color',1002,'2','Blanc',NOW(),NOW()),(13018,1105,1,'color',1001,'1','Noir',NOW(),NOW()),(13019,1105,2,'size',1007,'7','M',NOW(),NOW()),(13020,1105,3,'brand',0,'uniqlo','Uniqlo',NOW(),NOW()),
(13021,1106,1,'color',1003,'3','Gris',NOW(),NOW()),(13022,1106,1,'color',1004,'4','Bleu',NOW(),NOW()),(13023,1106,2,'size',1010,'10','40',NOW(),NOW()),(13024,1106,3,'brand',0,'zara-kids','Zara Kids',NOW(),NOW()),
(13025,1107,1,'color',1002,'2','Blanc',NOW(),NOW()),(13026,1107,1,'color',1001,'1','Noir',NOW(),NOW()),(13027,1107,2,'size',1012,'12','43',NOW(),NOW()),(13028,1107,3,'brand',0,'nike-kids','Nike Kids',NOW(),NOW()),
(13029,1108,1,'color',1005,'5','Marron',NOW(),NOW()),(13030,1108,1,'color',1001,'1','Noir',NOW(),NOW()),(13031,1108,2,'size',1013,'13','Unique',NOW(),NOW()),(13032,1108,3,'brand',0,'zara','Zara',NOW(),NOW()),
(13033,1109,1,'color',1005,'5','Marron',NOW(),NOW()),(13034,1109,1,'color',1003,'3','Gris',NOW(),NOW()),(13035,1109,2,'size',1013,'13','Unique',NOW(),NOW()),(13036,1109,3,'brand',0,'mango','Mango',NOW(),NOW()),
(13037,1110,1,'color',1002,'2','Blanc',NOW(),NOW()),(13038,1110,1,'color',1001,'1','Noir',NOW(),NOW()),(13039,1110,2,'size',1013,'13','Unique',NOW(),NOW()),(13040,1110,3,'brand',0,'uniqlo','Uniqlo',NOW(),NOW()),
(13041,1111,1,'color',1005,'5','Marron',NOW(),NOW()),(13042,1111,1,'color',1001,'1','Noir',NOW(),NOW()),(13043,1111,2,'size',1013,'13','Unique',NOW(),NOW()),(13044,1111,3,'brand',0,'levis','Levis',NOW(),NOW()),
(13045,1112,1,'color',1004,'4','Bleu',NOW(),NOW()),(13046,1112,1,'color',1001,'1','Noir',NOW(),NOW()),(13047,1112,2,'size',1010,'10','40',NOW(),NOW()),(13048,1112,3,'brand',0,'levis','Levis',NOW(),NOW());