-- ============================================================
-- USERS
-- ============================================================

INSERT INTO users
(id, created_at, updated_at, email, firstname, lastname, password, role, enabled)
VALUES
(1, NOW(), NOW(), 'e@gmail.com', 'Alice', 'Martin',
 '$2a$10$hqgMf5mksavpAEjscqq83uPUu2kGIknuHaYGfZ35KeNFEHAnPHcRC',
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
-- CATALOG PRODUCT
-- ============================================================

INSERT INTO catalog_product
(id, product_id, created_at, updated_at, name, slug, price,
 brand, image_url, category_id, category_name)
VALUES

(1,1,NOW(),NOW(),
'T-shirt Nike Basic',
't-shirt-nike-basic',
29.99,
'Nike',
'/images/products/tshirt-nike-basic.jpg',
20,
'T-shirts'),

(2,2,NOW(),NOW(),
'Adidas Runner',
'adidas-runner',
79.99,
'Adidas',
'/images/products/adidas-runner.jpg',
44,
'Sneakers'),

(3,3,NOW(),NOW(),
'Hoodie Puma Classic',
'hoodie-puma-classic',
59.99,
'Puma',
'/images/products/hoodie-puma.jpg',
41,
'Sweats'),

(4,4,NOW(),NOW(),
'Sac Zara Noir',
'sac-zara-noir',
39.99,
'Zara',
'/images/products/sac-zara.jpg',
70,
'Sacs'),

(5,5,NOW(),NOW(),
'Jean Levis Slim',
'jean-levis-slim',
69.99,
'Levis',
'/images/products/jean-levis.jpg',
43,
'Pantalons'),

(6,6,NOW(),NOW(),
'Carte Cadeau 25 EUR',
'carte-cadeau-25-eur',
25.00,
'NiceShop',
'/images/products/gift-card.jpg',
4,
'Accessoires'),

(7,7,NOW(),NOW(),
'Ceinture Cuir Classique',
'ceinture-cuir-classique',
44.99,
'Mango',
'/images/products/ceinture-mango.jpg',
71,
'Ceintures'),

(8,8,NOW(),NOW(),
'Bonnet Minimal',
'bonnet-minimal',
19.99,
'Uniqlo',
'/images/products/bonnet-uniqlo.jpg',
73,
'Chapeaux');

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