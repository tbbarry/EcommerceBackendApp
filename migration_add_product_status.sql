-- ════════════════════════════════════════════════════════════════════════════════
-- MIGRATION SQL: Ajout du champ STATUS aux entités Product et CatalogProduct
-- ════════════════════════════════════════════════════════════════════════════════
-- 
-- Cette migration ajoute le champ `status` aux tables products et catalog_product
-- pour gérer le cycle de vie des produits (ACTIVE, INACTIVE, ARCHIVED)
--
-- ════════════════════════════════════════════════════════════════════════════════

-- ✅ Table PRODUCTS
-- Ajouter la colonne status (par défaut ACTIVE)
ALTER TABLE products 
ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' 
AFTER brand;

-- Index pour optimiser les recherches par status
CREATE INDEX idx_products_status ON products(status);

-- ✅ Table CATALOG_PRODUCT
-- Ajouter la colonne status (par défaut ACTIVE)
ALTER TABLE catalog_product 
ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' 
AFTER category_id;

-- Index pour optimiser les recherches par status
CREATE INDEX idx_catalog_product_status ON catalog_product(status);

-- ════════════════════════════════════════════════════════════════════════════════
-- VERIFICATION
-- ════════════════════════════════════════════════════════════════════════════════

-- Vérifier que les colonnes ont été ajoutées
DESC products;
DESC catalog_product;

-- Vérifier les valeurs par défaut
SELECT id, name, status FROM products LIMIT 5;
SELECT id, name, status FROM catalog_product LIMIT 5;

-- ════════════════════════════════════════════════════════════════════════════════
-- OPTIONAL: UPDATE EXISTING RECORDS IF NEEDED
-- ════════════════════════════════════════════════════════════════════════════════

-- Optionnel: Mettre à jour les produits existants à un statut spécifique
-- UPDATE products SET status = 'ACTIVE' WHERE status IS NULL;
-- UPDATE catalog_product SET status = 'ACTIVE' WHERE status IS NULL;

-- ════════════════════════════════════════════════════════════════════════════════
-- ROLLBACK (si erreur)
-- ════════════════════════════════════════════════════════════════════════════════

-- ALTER TABLE products DROP COLUMN status;
-- ALTER TABLE catalog_product DROP COLUMN status;
-- DROP INDEX idx_products_status ON products;
-- DROP INDEX idx_catalog_product_status ON catalog_product;

-- ════════════════════════════════════════════════════════════════════════════════
