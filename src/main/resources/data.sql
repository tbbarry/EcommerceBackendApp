-- =========================
-- USERS
-- Mot de passe BCrypt = password123
-- =========================
INSERT INTO users (created_at, updated_at, email, firstname, lastname, password, phone, role, enabled) VALUES
                                                                                                (NOW(), NOW(), 'alice@test.com', 'Alice', 'Martin', '$2a$10$hqgMf5mksavpAEjscqq83uPUu2kGIknuHaYGfZ35KeNFEHAnPHcRC', '0600000001', 'ADMIN', true),
                                                                                                (NOW(), NOW(), 'karim@test.com', 'Karim', 'Benali', '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K', '0600000002', 'USER', true),
                                                                                                (NOW(), NOW(), 'sophie@test.com', 'Sophie', 'Durand', '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K', '0600000003', 'USER', true);

-- =========================
-- DELIVERY ADDRESSES
-- =========================
INSERT INTO delivery_addresses (user_id, created_at, updated_at, address, city, state, zipcode) VALUES
                                                                                                        ( 1, NOW(), NOW(), '12 Rue de Paris', 'Paris', 'Île-de-France', '75001'),
                                                                                                        ( 2, NOW(), NOW(), '8 Avenue Victor Hugo', 'Lyon', 'Auvergne-Rhône-Alpes', '69002'),
                                                                                                        (3, NOW(), NOW(), '25 Rue Nationale', 'Marseille', 'Provence-Alpes-Côte d’Azur', '13001')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- CARTS
-- =========================
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
                                                            (1, 1, NOW(), NOW()),
                                                            (2, 2, NOW(), NOW()),
                                                            (3, 3, NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- CATEGORIES
-- =========================
INSERT INTO categories (id, category_id, created_at, updated_at, description, name) VALUES
                                                                                        (1, NULL, NOW(), NOW(), 'Tous les vêtements', 'Vêtements'),
                                                                                        (2, NULL, NOW(), NOW(), 'Toutes les chaussures', 'Chaussures'),
                                                                                        (3, NULL, NOW(), NOW(), 'Accessoires de mode', 'Accessoires'),
                                                                                        (4, 1, NOW(), NOW(), 'T-shirts homme et femme', 'T-shirts'),
                                                                                        (5, 2, NOW(), NOW(), 'Chaussures sneakers', 'Sneakers'),
                                                                                        (6, 1, NOW(), NOW(), 'Sweats et hoodies', 'Sweats'),
                                                                                        (7, 3, NOW(), NOW(), 'Sacs et pochettes', 'Sacs')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- PRODUCTS
-- =========================
INSERT INTO products (id, price, created_at, updated_at, brand, description, name, slug) VALUES
                                                                                             (1, 29.99, NOW(), NOW(), 'Nike', 'T-shirt Nike en coton confortable', 'T-shirt Nike Basic', 't-shirt-nike-basic'),
                                                                                             (2, 79.99, NOW(), NOW(), 'Adidas', 'Sneakers Adidas légères pour tous les jours', 'Adidas Runner', 'adidas-runner'),
                                                                                             (3, 59.99, NOW(), NOW(), 'Puma', 'Sweat à capuche Puma doux et chaud', 'Hoodie Puma Classic', 'hoodie-puma-classic'),
                                                                                             (4, 39.99, NOW(), NOW(), 'Zara', 'Sac noir élégant', 'Sac Zara Noir', 'sac-zara-noir'),
                                                                                             (5, 69.99, NOW(), NOW(), 'Levis', 'Jean bleu coupe slim', 'Jean Levis Slim', 'jean-levis-slim')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- PRODUCT CATEGORIES
-- =========================
INSERT INTO product_categories (id, category_id, product_id, created_at, updated_at) VALUES
                                                                                         (1, 4, 1, NOW(), NOW()),
                                                                                         (2, 5, 2, NOW(), NOW()),
                                                                                         (3, 6, 3, NOW(), NOW()),
                                                                                         (4, 7, 4, NOW(), NOW()),
                                                                                         (5, 1, 5, NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- VARIANTS
-- =========================
INSERT INTO variants (id, price, product_id, created_at, updated_at, color, size, sku) VALUES
                                                                                           (1, 29.99, 1, NOW(), NOW(), 'Noir', 'S', 'NIKE-TS-BLK-S'),
                                                                                           (2, 29.99, 1, NOW(), NOW(), 'Noir', 'M', 'NIKE-TS-BLK-M'),
                                                                                           (3, 29.99, 1, NOW(), NOW(), 'Blanc', 'M', 'NIKE-TS-WHT-M'),

                                                                                           (4, 79.99, 2, NOW(), NOW(), 'Blanc', '42', 'ADI-RUN-WHT-42'),
                                                                                           (5, 79.99, 2, NOW(), NOW(), 'Noir', '43', 'ADI-RUN-BLK-43'),

                                                                                           (6, 59.99, 3, NOW(), NOW(), 'Gris', 'M', 'PUMA-HOOD-GRY-M'),
                                                                                           (7, 59.99, 3, NOW(), NOW(), 'Noir', 'L', 'PUMA-HOOD-BLK-L'),

                                                                                           (8, 39.99, 4, NOW(), NOW(), 'Noir', 'Unique', 'ZARA-BAG-BLK-U'),

                                                                                           (9, 69.99, 5, NOW(), NOW(), 'Bleu', '38', 'LEVIS-JEAN-BLU-38'),
                                                                                           (10, 69.99, 5, NOW(), NOW(), 'Bleu', '40', 'LEVIS-JEAN-BLU-40')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- IMAGES
-- Maintenant images est liée à variants avec variant_id
-- =========================
INSERT INTO images (id, variant_id, created_at, updated_at, alt, url) VALUES
                                                                          (1, 1, NOW(), NOW(), 'T-shirt Nike Basic Noir S', 'https://example.com/images/nike-tshirt-noir-s.jpg'),
                                                                          (2, 2, NOW(), NOW(), 'T-shirt Nike Basic Noir M', 'https://example.com/images/nike-tshirt-noir-m.jpg'),
                                                                          (3, 3, NOW(), NOW(), 'T-shirt Nike Basic Blanc M', 'https://example.com/images/nike-tshirt-blanc-m.jpg'),

                                                                          (4, 4, NOW(), NOW(), 'Adidas Runner Blanc 42', 'https://example.com/images/adidas-runner-blanc-42.jpg'),
                                                                          (5, 5, NOW(), NOW(), 'Adidas Runner Noir 43', 'https://example.com/images/adidas-runner-noir-43.jpg'),

                                                                          (6, 6, NOW(), NOW(), 'Hoodie Puma Classic Gris M', 'https://example.com/images/puma-hoodie-gris-m.jpg'),
                                                                          (7, 7, NOW(), NOW(), 'Hoodie Puma Classic Noir L', 'https://example.com/images/puma-hoodie-noir-l.jpg'),

                                                                          (8, 8, NOW(), NOW(), 'Sac Zara Noir', 'https://example.com/images/zara-sac-noir.jpg'),

                                                                          (9, 9, NOW(), NOW(), 'Jean Levis Slim Bleu 38', 'https://example.com/images/levis-jean-bleu-38.jpg'),
                                                                          (10, 10, NOW(), NOW(), 'Jean Levis Slim Bleu 40', 'https://example.com/images/levis-jean-bleu-40.jpg')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- STOCKS
-- =========================
INSERT INTO stocks (id, quantity, variant_id, created_at, updated_at) VALUES
                                                                          (1, 50, 1, NOW(), NOW()),
                                                                          (2, 35, 2, NOW(), NOW()),
                                                                          (3, 40, 3, NOW(), NOW()),
                                                                          (4, 20, 4, NOW(), NOW()),
                                                                          (5, 15, 5, NOW(), NOW()),
                                                                          (6, 25, 6, NOW(), NOW()),
                                                                          (7, 18, 7, NOW(), NOW()),
                                                                          (8, 30, 8, NOW(), NOW()),
                                                                          (9, 22, 9, NOW(), NOW()),
                                                                          (10, 12, 10, NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- CART ITEMS
-- =========================
INSERT INTO cart_items (id, cart_id, quantity, variant_id, created_at, updated_at) VALUES
                                                                                       (1, 1, 2, 1, NOW(), NOW()),
                                                                                       (2, 1, 1, 4, NOW(), NOW()),
                                                                                       (3, 2, 1, 6, NOW(), NOW()),
                                                                                       (4, 3, 3, 9, NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- PROMOS
-- =========================
INSERT INTO promos (id, discount_value, is_active, created_at, end_date, start_date, updated_at, code) VALUES
                                                                                                           (1, 10.00, true, NOW(), '2026-12-31', '2026-01-01', NOW(), 'WELCOME10'),
                                                                                                           (2, 20.00, true, NOW(), '2026-08-31', '2026-06-01', NOW(), 'SUMMER20'),
                                                                                                           (3, 15.00, false, NOW(), '2025-12-31', '2025-01-01', NOW(), 'OLDPROMO')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- PRODUCT PROMOS
-- =========================
INSERT INTO product_promos (id, product_id, promo_id, created_at, updated_at) VALUES
                                                                                  (1, 1, 1, NOW(), NOW()),
                                                                                  (2, 2, 2, NOW(), NOW()),
                                                                                  (3, 3, 1, NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- TAX SETTINGS
-- =========================
INSERT INTO tax_settings (
    id, rate, active, created_at, updated_at
) VALUES
    (1, 0.20, true, NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- ORDERS
-- =========================
INSERT INTO orders (
    id, delivery_address_id, shipping_order, subtotal, tax_rate, tax_amount, total,
    user_id, created_at, date_order, updated_at, order_number, status
) VALUES
      (1, 1, 5.99, 109.98, 0.20, 22.00, 137.97, 1, NOW(), '2026-05-10 14:00:00', NOW(), 'ORD-2026-0001', 'PAID'),
      (2, 2, 4.99, 59.99, 0.20, 12.00, 76.98, 2, NOW(), '2026-05-11 10:30:00', NOW(), 'ORD-2026-0002', 'PENDING'),
      (3, 3, 6.99, 209.96, 0.20, 41.99, 258.94, 3, NOW(), '2026-05-12 16:45:00', NOW(), 'ORD-2026-0003', 'SHIPPED')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- ORDER ITEMS
-- =========================
INSERT INTO order_items (
    id, order_id, quantity, total_price, unit_price, variant_id, created_at, updated_at
) VALUES
      (1, 1, 1, 29.99, 29.99, 1, NOW(), NOW()),
      (2, 1, 1, 79.99, 79.99, 4, NOW(), NOW()),

      (3, 2, 1, 59.99, 59.99, 6, NOW(), NOW()),

      (4, 3, 2, 139.98, 69.99, 9, NOW(), NOW()),
      (5, 3, 1, 39.99, 39.99, 8, NOW(), NOW()),
      (6, 3, 1, 29.99, 29.99, 2, NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- PAYMENTS
-- =========================
INSERT INTO payments (
    id, amount, order_id, created_at, paid_at, updated_at, payment_method, status
) VALUES
      (1, 137.96, 1, NOW(), '2026-05-10 14:30:00', NOW(), 'CARD', 'PAID'),
      (2, 76.98, 2, NOW(), NULL, NOW(), 'PAYPAL', 'PENDING'),
      (3, 258.95, 3, NOW(), '2026-05-12 17:15:00', NOW(), 'CARD', 'PAID')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- INVOICES
-- =========================
INSERT INTO invoices (
    id, order_id, total, created_at, date_invoice, updated_at, invoice_number
) VALUES
      (1, 1, 137.96, NOW(), '2026-05-10', NOW(), 'INV-2026-0001'),
      (2, 2, 76.98, NOW(), '2026-05-11', NOW(), 'INV-2026-0002'),
      (3, 3, 258.95, NOW(), '2026-05-12', NOW(), 'INV-2026-0003')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- RESET SEQUENCES
-- =========================
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('delivery_addresses', 'id'), COALESCE((SELECT MAX(id) FROM delivery_addresses), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('carts', 'id'), COALESCE((SELECT MAX(id) FROM carts), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('categories', 'id'), COALESCE((SELECT MAX(id) FROM categories), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('products', 'id'), COALESCE((SELECT MAX(id) FROM products), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('product_categories', 'id'), COALESCE((SELECT MAX(id) FROM product_categories), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('variants', 'id'), COALESCE((SELECT MAX(id) FROM variants), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('images', 'id'), COALESCE((SELECT MAX(id) FROM images), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('stocks', 'id'), COALESCE((SELECT MAX(id) FROM stocks), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('cart_items', 'id'), COALESCE((SELECT MAX(id) FROM cart_items), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('promos', 'id'), COALESCE((SELECT MAX(id) FROM promos), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('product_promos', 'id'), COALESCE((SELECT MAX(id) FROM product_promos), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('orders', 'id'), COALESCE((SELECT MAX(id) FROM orders), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('order_items', 'id'), COALESCE((SELECT MAX(id) FROM order_items), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('payments', 'id'), COALESCE((SELECT MAX(id) FROM payments), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('invoices', 'id'), COALESCE((SELECT MAX(id) FROM invoices), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('tax_settings', 'id'), COALESCE((SELECT MAX(id) FROM tax_settings), 0) + 1, false);