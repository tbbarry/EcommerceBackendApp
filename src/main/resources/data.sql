-- ============================================================
-- USERS
-- ============================================================

INSERT INTO users
(created_at, updated_at, email, firstname, lastname, password, role, enabled)
VALUES
(NOW(), NOW(), 'e@gmail.com', 'Alice', 'Martin',
 '$2a$10$hqgMf5mksavpAEjscqq83uPUu2kGIknuHaYGfZ35KeNFEHAnPHcRC',
 'ADMIN', true),

(NOW(), NOW(), 't@test.com', 'Karim', 'Benali',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'USER', true),

(NOW(), NOW(), 'sophie@test.com', 'Sophie', 'Durand',
 '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
 'USER', true)

ON CONFLICT (email) DO NOTHING;


-- ============================================================
-- CATEGORIES
--
-- Niveau 1
-- ├── Femme
-- ├── Homme
-- ├── Enfant
-- └── Accessoires
--
-- Niveau 2
-- Femme
-- ├── Vêtements
-- ├── Chaussures
-- └── Accessoires
--
-- Homme
-- ├── Vêtements
-- ├── Chaussures
-- └── Accessoires
--
-- Enfant
-- ├── Vêtements
-- ├── Chaussures
-- └── Accessoires
--
-- Accessoires
-- ├── Sacs
-- ├── Ceintures
-- ├── Bijoux
-- └── Chapeaux
--
-- Niveau 3
-- Vêtements
-- ├── T-shirts
-- ├── Sweats
-- ├── Chemises
-- ├── Robes
-- └── Pantalons
--
-- Chaussures
-- ├── Sneakers
-- ├── Baskets
-- └── Bottes
-- ============================================================

INSERT INTO categories
(id, category_id, created_at, updated_at, description, name)
VALUES

-- ============================================================
-- NIVEAU 1
-- ============================================================

(1, NULL, NOW(), NOW(),
 'Vêtements pour femme',
 'Femme'),

(2, NULL, NOW(), NOW(),
 'Vêtements et articles pour homme',
 'Homme'),

(3, NULL, NOW(), NOW(),
 'Vêtements et articles pour enfant',
 'Enfant'),

(4, NULL, NOW(), NOW(),
 'Accessoires de mode',
 'Accessoires'),


-- ============================================================
-- FEMME - NIVEAU 2
-- ============================================================

(10, 1, NOW(), NOW(),
 'Vêtements pour femme',
 'Vêtements'),

(11, 1, NOW(), NOW(),
 'Chaussures pour femme',
 'Chaussures'),

(12, 1, NOW(), NOW(),
 'Accessoires pour femme',
 'Accessoires'),


-- ============================================================
-- FEMME - NIVEAU 3
-- ============================================================

(20, 10, NOW(), NOW(),
 'T-shirts pour femme',
 'T-shirts'),

(21, 10, NOW(), NOW(),
 'Sweats et hoodies pour femme',
 'Sweats'),

(22, 10, NOW(), NOW(),
 'Robes pour femme',
 'Robes'),

(23, 10, NOW(), NOW(),
 'Pantalons pour femme',
 'Pantalons'),

(24, 11, NOW(), NOW(),
 'Sneakers pour femme',
 'Sneakers'),


-- ============================================================
-- HOMME - NIVEAU 2
-- ============================================================

(30, 2, NOW(), NOW(),
 'Vêtements pour homme',
 'Vêtements'),

(31, 2, NOW(), NOW(),
 'Chaussures pour homme',
 'Chaussures'),

(32, 2, NOW(), NOW(),
 'Accessoires pour homme',
 'Accessoires'),


-- ============================================================
-- HOMME - NIVEAU 3
-- ============================================================

(40, 30, NOW(), NOW(),
 'T-shirts pour homme',
 'T-shirts'),

(41, 30, NOW(), NOW(),
 'Sweats et hoodies pour homme',
 'Sweats'),

(42, 30, NOW(), NOW(),
 'Chemises pour homme',
 'Chemises'),

(43, 30, NOW(), NOW(),
 'Pantalons pour homme',
 'Pantalons'),

(44, 31, NOW(), NOW(),
 'Sneakers pour homme',
 'Sneakers'),


-- ============================================================
-- ENFANT - NIVEAU 2
-- ============================================================

(50, 3, NOW(), NOW(),
 'Vêtements pour enfant',
 'Vêtements'),

(51, 3, NOW(), NOW(),
 'Chaussures pour enfant',
 'Chaussures'),

(52, 3, NOW(), NOW(),
 'Accessoires pour enfant',
 'Accessoires'),


-- ============================================================
-- ENFANT - NIVEAU 3
-- ============================================================

(60, 50, NOW(), NOW(),
 'T-shirts pour enfant',
 'T-shirts'),

(61, 50, NOW(), NOW(),
 'Sweats pour enfant',
 'Sweats'),

(62, 50, NOW(), NOW(),
 'Pantalons pour enfant',
 'Pantalons'),

(63, 51, NOW(), NOW(),
 'Sneakers pour enfant',
 'Sneakers'),


-- ============================================================
-- ACCESSOIRES - NIVEAU 2
-- ============================================================

(70, 4, NOW(), NOW(),
 'Sacs et pochettes',
 'Sacs'),

(71, 4, NOW(), NOW(),
 'Ceintures',
 'Ceintures'),

(72, 4, NOW(), NOW(),
 'Bijoux',
 'Bijoux'),

(73, 4, NOW(), NOW(),
 'Chapeaux et bonnets',
 'Chapeaux')

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PRODUCTS
-- ============================================================

INSERT INTO products
(id, price, created_at, updated_at, brand, description, name, slug)
VALUES

(1, 29.99, NOW(), NOW(),
 'Nike',
 'T-shirt Nike en coton confortable',
 'T-shirt Nike Basic',
 't-shirt-nike-basic'),

(2, 79.99, NOW(), NOW(),
 'Adidas',
 'Sneakers Adidas légères pour tous les jours',
 'Adidas Runner',
 'adidas-runner'),

(3, 59.99, NOW(), NOW(),
 'Puma',
 'Sweat à capuche Puma doux et chaud',
 'Hoodie Puma Classic',
 'hoodie-puma-classic'),

(4, 39.99, NOW(), NOW(),
 'Zara',
 'Sac noir élégant',
 'Sac Zara Noir',
 'sac-zara-noir'),

(5, 69.99, NOW(), NOW(),
 'Levis',
 'Jean bleu coupe slim',
 'Jean Levis Slim',
 'jean-levis-slim'),

(6, 25.00, NOW(), NOW(),
 'NiceShop',
 'Carte cadeau digitale',
 'Carte Cadeau 25 EUR',
 'carte-cadeau-25-eur'),

(7, 44.99, NOW(), NOW(),
 'Mango',
 'Ceinture cuir classique',
 'Ceinture Cuir Classique',
 'ceinture-cuir-classique'),

(8, 19.99, NOW(), NOW(),
 'Uniqlo',
 'Bonnet unisexe',
 'Bonnet Minimal',
 'bonnet-minimal')

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PRODUCT CATEGORIES
--
-- IMPORTANT :
-- On rattache toujours le produit à la catégorie la plus
-- précise possible.
--
-- Produit 1 → Femme → Vêtements → T-shirts
-- Produit 2 → Homme → Chaussures → Sneakers
-- Produit 3 → Homme → Vêtements → Sweats
-- Produit 4 → Accessoires → Sacs
-- Produit 5 → Homme → Vêtements → Pantalons
-- Produit 6 → Accessoires
-- Produit 7 → Accessoires → Ceintures
-- Produit 8 → Accessoires → Chapeaux
-- ============================================================

INSERT INTO product_categories
(id, category_id, product_id, created_at, updated_at)
VALUES

-- T-shirt Nike
(1, 20, 1, NOW(), NOW()),

-- Adidas Runner
(2, 44, 2, NOW(), NOW()),

-- Hoodie Puma
(3, 41, 3, NOW(), NOW()),

-- Sac Zara
(4, 70, 4, NOW(), NOW()),

-- Jean Levis
(5, 43, 5, NOW(), NOW()),

-- Carte cadeau
(6, 4, 6, NOW(), NOW()),

-- Ceinture Mango
(7, 71, 7, NOW(), NOW()),

-- Bonnet Uniqlo
(8, 73, 8, NOW(), NOW())

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- ATTRIBUTES
-- ============================================================

INSERT INTO attributes
(id, created_at, updated_at, name)
VALUES

(1, NOW(), NOW(), 'Couleur'),
(2, NOW(), NOW(), 'Taille')

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- ATTRIBUTE VALUES
-- ============================================================

INSERT INTO attribute_values
(id, attribute_id, created_at, updated_at, value)
VALUES

-- Couleurs
(1, 1, NOW(), NOW(), 'Noir'),
(2, 1, NOW(), NOW(), 'Blanc'),
(3, 1, NOW(), NOW(), 'Gris'),
(4, 1, NOW(), NOW(), 'Bleu'),
(5, 1, NOW(), NOW(), 'Marron'),

-- Tailles
(6, 2, NOW(), NOW(), 'S'),
(7, 2, NOW(), NOW(), 'M'),
(8, 2, NOW(), NOW(), 'L'),
(9, 2, NOW(), NOW(), '38'),
(10, 2, NOW(), NOW(), '40'),
(11, 2, NOW(), NOW(), '42'),
(12, 2, NOW(), NOW(), '43'),
(13, 2, NOW(), NOW(), 'Unique')

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- VARIANTS
-- ============================================================

INSERT INTO variants
(id, price, product_id, created_at, updated_at, sku)
VALUES

-- Produit 1 : T-shirt Nike
(1, 29.99, 1, NOW(), NOW(), 'NIKE-TS-BLK-S'),
(2, 29.99, 1, NOW(), NOW(), 'NIKE-TS-BLK-M'),
(3, 29.99, 1, NOW(), NOW(), 'NIKE-TS-WHT-M'),

-- Produit 2 : Adidas
(4, 79.99, 2, NOW(), NOW(), 'ADI-RUN-WHT-42'),
(5, 79.99, 2, NOW(), NOW(), 'ADI-RUN-BLK-43'),

-- Produit 3 : Puma
(6, 59.99, 3, NOW(), NOW(), 'PUMA-HOOD-GRY-M'),
(7, 59.99, 3, NOW(), NOW(), 'PUMA-HOOD-BLK-L'),

-- Produit 4 : Sac
(8, 39.99, 4, NOW(), NOW(), 'ZARA-BAG-BLK-U'),

-- Produit 5 : Jean
(9, 69.99, 5, NOW(), NOW(), 'LEVIS-JEAN-BLU-38'),
(10, 69.99, 5, NOW(), NOW(), 'LEVIS-JEAN-BLU-40'),

-- Produit 6 : Carte cadeau
(11, 25.00, 6, NOW(), NOW(), 'GIFT-CARD-25-001'),

-- Produit 7 : Ceinture
(12, 44.99, 7, NOW(), NOW(), 'MANGO-BELT-BRN-U'),

-- Produit 8 : Bonnet
(13, 19.99, 8, NOW(), NOW(), 'UNIQLO-BONNET-GRY-U')

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- VARIANT ATTRIBUTE VALUES
-- ============================================================

INSERT INTO variant_attribute_values
(id, variant_id, attribute_value_id, created_at, updated_at)
VALUES

-- ============================================================
-- Produit 1 : T-shirt Nike
-- Noir / S
-- Noir / M
-- Blanc / M
-- ============================================================

(1, 1, 1, NOW(), NOW()),
(2, 1, 6, NOW(), NOW()),

(3, 2, 1, NOW(), NOW()),
(4, 2, 7, NOW(), NOW()),

(5, 3, 2, NOW(), NOW()),
(6, 3, 7, NOW(), NOW()),


-- ============================================================
-- Produit 2 : Adidas
-- Blanc / 42
-- Noir / 43
-- ============================================================

(7, 4, 2, NOW(), NOW()),
(8, 4, 11, NOW(), NOW()),

(9, 5, 1, NOW(), NOW()),
(10, 5, 12, NOW(), NOW()),


-- ============================================================
-- Produit 3 : Puma
-- Gris / M
-- Noir / L
-- ============================================================

(11, 6, 3, NOW(), NOW()),
(12, 6, 7, NOW(), NOW()),

(13, 7, 1, NOW(), NOW()),
(14, 7, 8, NOW(), NOW()),


-- ============================================================
-- Produit 4 : Sac
-- Noir / Unique
-- ============================================================

(15, 8, 1, NOW(), NOW()),
(16, 8, 13, NOW(), NOW()),


-- ============================================================
-- Produit 5 : Jean
-- Bleu / 38
-- Bleu / 40
-- ============================================================

(17, 9, 4, NOW(), NOW()),
(18, 9, 9, NOW(), NOW()),

(19, 10, 4, NOW(), NOW()),
(20, 10, 10, NOW(), NOW()),


-- ============================================================
-- Produit 7 : Ceinture
-- Marron / Unique
-- ============================================================

(21, 12, 5, NOW(), NOW()),
(22, 12, 13, NOW(), NOW()),


-- ============================================================
-- Produit 8 : Bonnet
-- Gris / Unique
-- ============================================================

(23, 13, 3, NOW(), NOW()),
(24, 13, 13, NOW(), NOW())

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- CATALOG PRODUCT
-- ============================================================

INSERT INTO catalog_product
(id, product_id, created_at, updated_at, name, slug, price, brand, image_url)
VALUES

(1, 1, NOW(), NOW(),
 'T-shirt Nike Basic',
 't-shirt-nike-basic',
 29.99,
 'Nike',
 '/images/products/tshirt-nike-basic.jpg'),

(2, 2, NOW(), NOW(),
 'Adidas Runner',
 'adidas-runner',
 79.99,
 'Adidas',
 '/images/products/adidas-runner.jpg'),

(3, 3, NOW(), NOW(),
 'Hoodie Puma Classic',
 'hoodie-puma-classic',
 59.99,
 'Puma',
 '/images/products/hoodie-puma.jpg'),

(4, 4, NOW(), NOW(),
 'Sac Zara Noir',
 'sac-zara-noir',
 39.99,
 'Zara',
 '/images/products/sac-zara.jpg'),

(5, 5, NOW(), NOW(),
 'Jean Levis Slim',
 'jean-levis-slim',
 69.99,
 'Levis',
 '/images/products/jean-levis.jpg'),

(6, 6, NOW(), NOW(),
 'Carte Cadeau 25 EUR',
 'carte-cadeau-25-eur',
 25.00,
 'NiceShop',
 '/images/products/gift-card.jpg'),

(7, 7, NOW(), NOW(),
 'Ceinture Cuir Classique',
 'ceinture-cuir-classique',
 44.99,
 'Mango',
 '/images/products/ceinture-mango.jpg'),

(8, 8, NOW(), NOW(),
 'Bonnet Minimal',
 'bonnet-minimal',
 19.99,
 'Uniqlo',
 '/images/products/bonnet-uniqlo.jpg')

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- CATALOG PRODUCT FACET
--
-- facet_id 1 = color
-- facet_id 2 = size
-- facet_id 3 = brand
--
-- ============================================================

INSERT INTO catalog_product_facet
(
    id,
    product_id,
    facet_id,
    facet_code,
    facet_value_id,
    value_code,
    value_label,
    created_at,
    updated_at
)
VALUES

-- ============================================================
-- Produit 1 : Nike T-shirt
-- ============================================================

(1, 1, 1, 'color', 1, '1', 'Noir', NOW(), NOW()),
(2, 1, 1, 'color', 2, '2', 'Blanc', NOW(), NOW()),
(3, 1, 2, 'size', 6, '6', 'S', NOW(), NOW()),
(4, 1, 2, 'size', 7, '7', 'M', NOW(), NOW()),
(5, 1, 3, 'brand', 0, 'nike', 'Nike', NOW(), NOW()),


-- ============================================================
-- Produit 2 : Adidas Runner
-- ============================================================

(6, 2, 1, 'color', 2, '2', 'Blanc', NOW(), NOW()),
(7, 2, 1, 'color', 1, '1', 'Noir', NOW(), NOW()),
(8, 2, 2, 'size', 11, '11', '42', NOW(), NOW()),
(9, 2, 2, 'size', 12, '12', '43', NOW(), NOW()),
(10, 2, 3, 'brand', 0, 'adidas', 'Adidas', NOW(), NOW()),


-- ============================================================
-- Produit 3 : Puma Hoodie
-- ============================================================

(11, 3, 1, 'color', 3, '3', 'Gris', NOW(), NOW()),
(12, 3, 1, 'color', 1, '1', 'Noir', NOW(), NOW()),
(13, 3, 2, 'size', 7, '7', 'M', NOW(), NOW()),
(14, 3, 2, 'size', 8, '8', 'L', NOW(), NOW()),
(15, 3, 3, 'brand', 0, 'puma', 'Puma', NOW(), NOW()),


-- ============================================================
-- Produit 4 : Sac Zara
-- ============================================================

(16, 4, 1, 'color', 1, '1', 'Noir', NOW(), NOW()),
(17, 4, 2, 'size', 13, '13', 'Unique', NOW(), NOW()),
(18, 4, 3, 'brand', 0, 'zara', 'Zara', NOW(), NOW()),


-- ============================================================
-- Produit 5 : Jean Levis
-- ============================================================

(19, 5, 1, 'color', 4, '4', 'Bleu', NOW(), NOW()),
(20, 5, 2, 'size', 9, '9', '38', NOW(), NOW()),
(21, 5, 2, 'size', 10, '10', '40', NOW(), NOW()),
(22, 5, 3, 'brand', 0, 'levis', 'Levis', NOW(), NOW()),


-- ============================================================
-- Produit 7 : Ceinture Mango
-- ============================================================

(23, 7, 1, 'color', 5, '5', 'Marron', NOW(), NOW()),
(24, 7, 2, 'size', 13, '13', 'Unique', NOW(), NOW()),
(25, 7, 3, 'brand', 0, 'mango', 'Mango', NOW(), NOW()),


-- ============================================================
-- Produit 8 : Bonnet Uniqlo
-- ============================================================

(26, 8, 1, 'color', 3, '3', 'Gris', NOW(), NOW()),
(27, 8, 2, 'size', 13, '13', 'Unique', NOW(), NOW()),
(28, 8, 3, 'brand', 0, 'uniqlo', 'Uniqlo', NOW(), NOW())

ON CONFLICT (id) DO NOTHING;