-- =========================
-- USERS
-- =========================
INSERT INTO users (id, created_at, updated_at, email, firstname, lastname, password, phone) VALUES
                                                                                                (1, NOW(), NOW(), 'alice@test.com', 'Alice', 'Martin', 'password123', '0600000001'),
                                                                                                (2, NOW(), NOW(), 'karim@test.com', 'Karim', 'Benali', 'password123', '0600000002'),
                                                                                                (3, NOW(), NOW(), 'sophie@test.com', 'Sophie', 'Durand', 'password123', '0600000003');

-- =========================
-- DELIVERY ADDRESSES
-- =========================
INSERT INTO delivery_addresses (id, created_at, updated_at, address, city, state, zipcode, user_id) VALUES
                                                                                                        (1, NOW(), NOW(), '12 Rue de Paris', 'Paris', 'Île-de-France', '75001', 1),
                                                                                                        (2, NOW(), NOW(), '8 Avenue Victor Hugo', 'Lyon', 'Auvergne-Rhône-Alpes', '69002', 2),
                                                                                                        (3, NOW(), NOW(), '25 Rue Nationale', 'Marseille', 'Provence-Alpes-Côte d’Azur', '13001', 3);

-- =========================
-- CARTS
-- =========================
INSERT INTO carts (id, created_at, updated_at, user_id) VALUES
                                                            (1, NOW(), NOW(), 1),
                                                            (2, NOW(), NOW(), 2),
                                                            (3, NOW(), NOW(), 3);

-- =========================
-- CATEGORIES
-- =========================
INSERT INTO categories (id, created_at, updated_at, name, description, category_id) VALUES
                                                                                        (1, NOW(), NOW(), 'Vêtements', 'Tous les vêtements', NULL),
                                                                                        (2, NOW(), NOW(), 'Chaussures', 'Toutes les chaussures', NULL),
                                                                                        (3, NOW(), NOW(), 'Accessoires', 'Accessoires de mode', NULL),
                                                                                        (4, NOW(), NOW(), 'T-shirts', 'T-shirts homme et femme', 1),
                                                                                        (5, NOW(), NOW(), 'Sneakers', 'Chaussures sneakers', 2),
                                                                                        (6, NOW(), NOW(), 'Sacs', 'Sacs et pochettes', 3);

-- =========================
-- PRODUCTS
-- =========================
INSERT INTO products (id, created_at, updated_at, brand, description, name, price, slug) VALUES
                                                                                             (1, NOW(), NOW(), 'Nike', 'T-shirt Nike en coton confortable', 'T-shirt Nike Basic', 29.99, 't-shirt-nike-basic'),
                                                                                             (2, NOW(), NOW(), 'Adidas', 'Sneakers Adidas légères pour tous les jours', 'Adidas Runner', 79.99, 'adidas-runner'),
                                                                                             (3, NOW(), NOW(), 'Puma', 'Sweat à capuche Puma doux et chaud', 'Hoodie Puma Classic', 59.99, 'hoodie-puma-classic'),
                                                                                             (4, NOW(), NOW(), 'Zara', 'Sac noir élégant pour femme', 'Sac Zara Noir', 39.99, 'sac-zara-noir'),
                                                                                             (5, NOW(), NOW(), 'Levis', 'Jean bleu coupe slim', 'Jean Levis Slim', 69.99, 'jean-levis-slim');

-- =========================
-- PRODUCT CATEGORIES
-- =========================
INSERT INTO product_categories (id, created_at, updated_at, product_id, category_id) VALUES
                                                                                         (1, NOW(), NOW(), 1, 4),
                                                                                         (2, NOW(), NOW(), 2, 5),
                                                                                         (3, NOW(), NOW(), 3, 1),
                                                                                         (4, NOW(), NOW(), 4, 6),
                                                                                         (5, NOW(), NOW(), 5, 1);

-- =========================
-- IMAGES
-- =========================
INSERT INTO images (id, created_at, updated_at, alt, url, product_id) VALUES
                                                                          (1, NOW(), NOW(), 'T-shirt Nike Basic', 'https://example.com/images/tshirt-nike.jpg', 1),
                                                                          (2, NOW(), NOW(), 'Adidas Runner', 'https://example.com/images/adidas-runner.jpg', 2),
                                                                          (3, NOW(), NOW(), 'Hoodie Puma Classic', 'https://example.com/images/hoodie-puma.jpg', 3),
                                                                          (4, NOW(), NOW(), 'Sac Zara Noir', 'https://example.com/images/sac-zara.jpg', 4),
                                                                          (5, NOW(), NOW(), 'Jean Levis Slim', 'https://example.com/images/jean-levis.jpg', 5);

-- =========================
-- VARIANTS
-- =========================
INSERT INTO variants (id, created_at, updated_at, color, size, sku, price, product_id) VALUES
                                                                                           (1, NOW(), NOW(), 'Noir', 'S', 'NIKE-TS-BLK-S', 29.99, 1),
                                                                                           (2, NOW(), NOW(), 'Noir', 'M', 'NIKE-TS-BLK-M', 29.99, 1),
                                                                                           (3, NOW(), NOW(), 'Blanc', 'M', 'NIKE-TS-WHT-M', 29.99, 1),

                                                                                           (4, NOW(), NOW(), 'Blanc', '42', 'ADI-RUN-WHT-42', 79.99, 2),
                                                                                           (5, NOW(), NOW(), 'Noir', '43', 'ADI-RUN-BLK-43', 79.99, 2),

                                                                                           (6, NOW(), NOW(), 'Gris', 'M', 'PUMA-HOOD-GRY-M', 59.99, 3),
                                                                                           (7, NOW(), NOW(), 'Noir', 'L', 'PUMA-HOOD-BLK-L', 59.99, 3),

                                                                                           (8, NOW(), NOW(), 'Noir', 'Unique', 'ZARA-BAG-BLK-U', 39.99, 4),

                                                                                           (9, NOW(), NOW(), 'Bleu', '38', 'LEVIS-JEAN-BLU-38', 69.99, 5),
                                                                                           (10, NOW(), NOW(), 'Bleu', '40', 'LEVIS-JEAN-BLU-40', 69.99, 5);

-- =========================
-- STOCKS
-- =========================
INSERT INTO stocks (id, created_at, updated_at, quantity, variant_id) VALUES
                                                                          (1, NOW(), NOW(), 50, 1),
                                                                          (2, NOW(), NOW(), 35, 2),
                                                                          (3, NOW(), NOW(), 40, 3),
                                                                          (4, NOW(), NOW(), 20, 4),
                                                                          (5, NOW(), NOW(), 15, 5),
                                                                          (6, NOW(), NOW(), 25, 6),
                                                                          (7, NOW(), NOW(), 18, 7),
                                                                          (8, NOW(), NOW(), 30, 8),
                                                                          (9, NOW(), NOW(), 22, 9),
                                                                          (10, NOW(), NOW(), 12, 10);

-- =========================
-- CART ITEMS
-- =========================
INSERT INTO cart_items (id, created_at, updated_at, quantity, cart_id, variant_id) VALUES
                                                                                       (1, NOW(), NOW(), 2, 1, 1),
                                                                                       (2, NOW(), NOW(), 1, 1, 4),
                                                                                       (3, NOW(), NOW(), 1, 2, 6),
                                                                                       (4, NOW(), NOW(), 3, 3, 9);

-- =========================
-- PROMOS
-- =========================
INSERT INTO promos (id, created_at, updated_at, code, discount_value, start_date, end_date, is_active) VALUES
                                                                                                           (1, NOW(), NOW(), 'WELCOME10', 10.00, '2026-01-01', '2026-12-31', true),
                                                                                                           (2, NOW(), NOW(), 'SUMMER20', 20.00, '2026-06-01', '2026-08-31', true),
                                                                                                           (3, NOW(), NOW(), 'OLDPROMO', 15.00, '2025-01-01', '2025-12-31', false);

-- =========================
-- PRODUCT PROMOS
-- =========================
INSERT INTO product_promos (id, created_at, updated_at, product_id, promo_id) VALUES
                                                                                  (1, NOW(), NOW(), 1, 1),
                                                                                  (2, NOW(), NOW(), 2, 2),
                                                                                  (3, NOW(), NOW(), 3, 1);

-- =========================
-- ORDERS
-- =========================
INSERT INTO orders (
    id, created_at, updated_at, date_order, order_number,
    shipping_order, status, subtotal, tax_amount, total,
    delivery_address_id, user_id
) VALUES
      (1, NOW(), NOW(), '2026-05-10', 'ORD-2026-0001', 5.99, 'PAID', 109.98, 21.99, 137.96, 1, 1),
      (2, NOW(), NOW(), '2026-05-11', 'ORD-2026-0002', 4.99, 'PENDING', 59.99, 12.00, 76.98, 2, 2),
      (3, NOW(), NOW(), '2026-05-12', 'ORD-2026-0003', 6.99, 'SHIPPED', 209.97, 41.99, 258.95, 3, 3);

-- =========================
-- ORDER ITEMS
-- =========================
INSERT INTO order_items (
    id, created_at, updated_at, quantity, unit_price, total_price,
    order_id, variant_id
) VALUES
      (1, NOW(), NOW(), 1, 29.99, 29.99, 1, 1),
      (2, NOW(), NOW(), 1, 79.99, 79.99, 1, 4),

      (3, NOW(), NOW(), 1, 59.99, 59.99, 2, 6),

      (4, NOW(), NOW(), 2, 69.99, 139.98, 3, 9),
      (5, NOW(), NOW(), 1, 39.99, 39.99, 3, 8),
      (6, NOW(), NOW(), 1, 29.99, 29.99, 3, 2);

-- =========================
-- PAYMENTS
-- =========================
INSERT INTO payments (
    id, created_at, updated_at, amount, paid_at,
    payment_method, status, order_id
) VALUES
      (1, NOW(), NOW(), 137.96, '2026-05-10 14:30:00', 'CARD', 'PAID', 1),
      (2, NOW(), NOW(), 76.98, NULL, 'PAYPAL', 'PENDING', 2),
      (3, NOW(), NOW(), 258.95, '2026-05-12 10:15:00', 'CARD', 'PAID', 3);

-- =========================
-- INVOICES
-- =========================
INSERT INTO invoices (
    id, created_at, updated_at, date_invoice,
    invoice_number, total, order_id
) VALUES
      (1, NOW(), NOW(), '2026-05-10', 'INV-2026-0001', 137.96, 1),
      (2, NOW(), NOW(), '2026-05-11', 'INV-2026-0002', 76.98, 2),
      (3, NOW(), NOW(), '2026-05-12', 'INV-2026-0003', 258.95, 3);


SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('delivery_addresses_id_seq', (SELECT MAX(id) FROM delivery_addresses));
SELECT setval('carts_id_seq', (SELECT MAX(id) FROM carts));
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
SELECT setval('product_categories_id_seq', (SELECT MAX(id) FROM product_categories));
SELECT setval('images_id_seq', (SELECT MAX(id) FROM images));
SELECT setval('variants_id_seq', (SELECT MAX(id) FROM variants));
SELECT setval('stocks_id_seq', (SELECT MAX(id) FROM stocks));
SELECT setval('cart_items_id_seq', (SELECT MAX(id) FROM cart_items));
SELECT setval('promos_id_seq', (SELECT MAX(id) FROM promos));
SELECT setval('product_promos_id_seq', (SELECT MAX(id) FROM product_promos));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_items_id_seq', (SELECT MAX(id) FROM order_items));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
SELECT setval('invoices_id_seq', (SELECT MAX(id) FROM invoices));