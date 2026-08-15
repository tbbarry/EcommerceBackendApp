-- ============================================================
-- USERS
-- Mot de passe BCrypt = password123
-- ============================================================
INSERT INTO users (
    created_at,
    updated_at,
    email,
    firstname,
    lastname,
    password,
    role,
    enabled
) VALUES
    (
        NOW(),
        NOW(),
        'e@gmail.com',
        'Alice',
        'Martin',
        '$2a$10$hqgMf5mksavpAEjscqq83uPUu2kGIknuHaYGfZ35KeNFEHAnPHcRC',
        'ADMIN',
        true
    ),
    (
        NOW(),
        NOW(),
        't@test.com',
        'Karim',
        'Benali',
        '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
        'USER',
        true
    ),
    (
        NOW(),
        NOW(),
        'sophie@test.com',
        'Sophie',
        'Durand',
        '$2a$10$MrwAn5oQQAXmqbPINmd0TO5UfRi76RfYPeVdNY3ysZMwwu3aaDh8K',
        'USER',
        true
    );


-- ============================================================
-- DELIVERY ADDRESSES
-- ============================================================
INSERT INTO delivery_addresses (
    user_id,
    created_at,
    updated_at,
    first_name,
    last_name,
    address,
    city,
    state,
    zipcode,
    phone,
    label,
    country,
    is_default,
    deleted
) VALUES
    (
        1,
        NOW(),
        NOW(),
        'Alice',
        'Martin',
        '12 Market Street',
        'San Francisco',
        'CA',
        '94105',
        '415-555-0101',
        'home',
        'US',
        true,
        false
    ),
    (
        2,
        NOW(),
        NOW(),
        'Karim',
        'Benali',
        '8 Madison Avenue',
        'New York',
        'NY',
        '10010',
        '212-555-0102',
        'work',
        'US',
        true,
        false
    ),
    (
        3,
        NOW(),
        NOW(),
        'Sophie',
        'Durand',
        '25 Lakeshore Drive',
        'Chicago',
        'IL',
        '60601',
        '312-555-0103',
        'home',
        'US',
        true,
        false
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- CARTS
-- ============================================================
INSERT INTO carts (
    id,
    user_id,
    created_at,
    updated_at
) VALUES
    (1, 1, NOW(), NOW()),
    (2, 2, NOW(), NOW()),
    (3, 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- CATEGORIES
-- ============================================================
INSERT INTO categories (
    id,
    category_id,
    created_at,
    updated_at,
    description,
    name
) VALUES
    (
        1,
        NULL,
        NOW(),
        NOW(),
        'Tous les vêtements',
        'Vêtements'
    ),
    (
        2,
        NULL,
        NOW(),
        NOW(),
        'Toutes les chaussures',
        'Chaussures'
    ),
    (
        3,
        NULL,
        NOW(),
        NOW(),
        'Accessoires de mode',
        'Accessoires'
    ),
    (
        4,
        1,
        NOW(),
        NOW(),
        'T-shirts homme et femme',
        'T-shirts'
    ),
    (
        5,
        2,
        NOW(),
        NOW(),
        'Chaussures sneakers',
        'Sneakers'
    ),
    (
        6,
        1,
        NOW(),
        NOW(),
        'Sweats et hoodies',
        'Sweats'
    ),
    (
        7,
        3,
        NOW(),
        NOW(),
        'Sacs et pochettes',
        'Sacs'
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PRODUCTS
-- ============================================================
INSERT INTO products (
    id,
    price,
    created_at,
    updated_at,
    brand,
    description,
    name,
    slug
) VALUES
    (
        1,
        29.99,
        NOW(),
        NOW(),
        'Nike',
        'T-shirt Nike en coton confortable',
        'T-shirt Nike Basic',
        't-shirt-nike-basic'
    ),
    (
        2,
        79.99,
        NOW(),
        NOW(),
        'Adidas',
        'Sneakers Adidas légères pour tous les jours',
        'Adidas Runner',
        'adidas-runner'
    ),
    (
        3,
        59.99,
        NOW(),
        NOW(),
        'Puma',
        'Sweat à capuche Puma doux et chaud',
        'Hoodie Puma Classic',
        'hoodie-puma-classic'
    ),
    (
        4,
        39.99,
        NOW(),
        NOW(),
        'Zara',
        'Sac noir élégant',
        'Sac Zara Noir',
        'sac-zara-noir'
    ),
    (
        5,
        69.99,
        NOW(),
        NOW(),
        'Levis',
        'Jean bleu coupe slim',
        'Jean Levis Slim',
        'jean-levis-slim'
    ),
    (
        6,
        25.00,
        NOW(),
        NOW(),
        'NiceShop',
        'Carte cadeau digitale, produit sans taille ni couleur',
        'Carte Cadeau 25 EUR',
        'carte-cadeau-25-eur'
    ),
    (
        7,
        44.99,
        NOW(),
        NOW(),
        'Mango',
        'Ceinture cuir classique, couleur unique sans taille',
        'Ceinture Cuir Classique',
        'ceinture-cuir-classique'
    ),
    (
        8,
        19.99,
        NOW(),
        NOW(),
        'Uniqlo',
        'Bonnet unisexe, taille unique sans couleur',
        'Bonnet Minimal',
        'bonnet-minimal'
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PRODUCT CATEGORIES
-- ============================================================
INSERT INTO product_categories (
    id,
    category_id,
    product_id,
    created_at,
    updated_at
) VALUES
    (1, 4, 1, NOW(), NOW()),
    (2, 5, 2, NOW(), NOW()),
    (3, 6, 3, NOW(), NOW()),
    (4, 7, 4, NOW(), NOW()),
    (5, 1, 5, NOW(), NOW()),
    (6, 3, 6, NOW(), NOW()),
    (7, 3, 7, NOW(), NOW()),
    (8, 3, 8, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PRODUCT COLORS
-- ============================================================
INSERT INTO product_colors (
    id,
    product_id,
    created_at,
    updated_at,
    name,
    hex_code
) VALUES
    (1, 1, NOW(), NOW(), 'Noir', '#000000'),
    (2, 1, NOW(), NOW(), 'Blanc', '#FFFFFF'),

    (3, 2, NOW(), NOW(), 'Blanc', '#FFFFFF'),
    (4, 2, NOW(), NOW(), 'Noir', '#000000'),

    (5, 3, NOW(), NOW(), 'Gris', '#808080'),
    (6, 3, NOW(), NOW(), 'Noir', '#000000'),

    (7, 4, NOW(), NOW(), 'Noir', '#000000'),

    (8, 5, NOW(), NOW(), 'Bleu', '#0000FF'),

    (9, 7, NOW(), NOW(), 'Marron', '#8B4513')
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- VARIANTS
--
-- product_color_id :
--   1 = Nike Noir
--   2 = Nike Blanc
--   3 = Adidas Blanc
--   4 = Adidas Noir
--   5 = Puma Gris
--   6 = Puma Noir
--   7 = Zara Noir
--   8 = Levis Bleu
--   9 = Mango Marron
--
-- NULL = produit sans couleur
-- ============================================================
INSERT INTO variants (
    id,
    price,
    product_id,
    product_color_id,
    created_at,
    updated_at,
    size,
    sku
) VALUES

    -- Nike
    (
        1,
        29.99,
        1,
        1,
        NOW(),
        NOW(),
        'S',
        'NIKE-TS-BLK-S'
    ),
    (
        2,
        29.99,
        1,
        1,
        NOW(),
        NOW(),
        'M',
        'NIKE-TS-BLK-M'
    ),
    (
        3,
        29.99,
        1,
        2,
        NOW(),
        NOW(),
        'M',
        'NIKE-TS-WHT-M'
    ),

    -- Adidas
    (
        4,
        79.99,
        2,
        3,
        NOW(),
        NOW(),
        '42',
        'ADI-RUN-WHT-42'
    ),
    (
        5,
        79.99,
        2,
        4,
        NOW(),
        NOW(),
        '43',
        'ADI-RUN-BLK-43'
    ),

    -- Puma
    (
        6,
        59.99,
        3,
        5,
        NOW(),
        NOW(),
        'M',
        'PUMA-HOOD-GRY-M'
    ),
    (
        7,
        59.99,
        3,
        6,
        NOW(),
        NOW(),
        'L',
        'PUMA-HOOD-BLK-L'
    ),

    -- Zara : couleur unique
    (
        8,
        39.99,
        4,
        7,
        NOW(),
        NOW(),
        'Unique',
        'ZARA-BAG-BLK-U'
    ),

    -- Levis
    (
        9,
        69.99,
        5,
        8,
        NOW(),
        NOW(),
        '38',
        'LEVIS-JEAN-BLU-38'
    ),
    (
        10,
        69.99,
        5,
        8,
        NOW(),
        NOW(),
        '40',
        'LEVIS-JEAN-BLU-40'
    ),

    -- Carte cadeau : aucune couleur, aucune taille
    (
        11,
        25.00,
        6,
        NULL,
        NOW(),
        NOW(),
        NULL,
        'GIFT-CARD-25-001'
    ),

    -- Ceinture : couleur mais pas de taille
    (
        12,
        44.99,
        7,
        9,
        NOW(),
        NOW(),
        NULL,
        'MANGO-BELT-BRN-NA'
    ),

    -- Bonnet : aucune couleur, taille unique
    (
        13,
        19.99,
        8,
        NULL,
        NOW(),
        NOW(),
        'Unique',
        'UNIQLO-BONNET-NA-U'
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PRODUCT IMAGES
--
-- Toutes les images sont maintenant gérées ici.
--
-- product_color_id = NULL
--     => image générale du produit
--
-- product_color_id renseigné
--     => image spécifique à une couleur
--
-- is_main = true
--     => image principale
-- ============================================================
INSERT INTO product_images (
    id,
    product_id,
    product_color_id,
    is_main,
    display_order,
    created_at,
    updated_at,
    alt,
    url,
    object_key
) VALUES

    -- ========================================================
    -- NIKE
    -- ========================================================

    -- Image générale
    (
        1,
        1,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'T-shirt Nike Basic - image principale',
        'https://images.pexels.com/photos/5698847/pexels-photo-5698847.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),

    -- Noir
    (
        2,
        1,
        1,
        true,
        0,
        NOW(),
        NOW(),
        'T-shirt Nike Basic Noir',
        'https://images.pexels.com/photos/9558598/pexels-photo-9558598.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        16,
        1,
        1,
        false,
        1,
        NOW(),
        NOW(),
        'T-shirt Nike Basic Noir - vue 2',
        'https://images.pexels.com/photos/1007018/pexels-photo-1007018.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),

    -- Blanc
    (
        3,
        1,
        2,
        true,
        0,
        NOW(),
        NOW(),
        'T-shirt Nike Basic Blanc',
        'https://images.pexels.com/photos/6311392/pexels-photo-6311392.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        17,
        1,
        2,
        false,
        1,
        NOW(),
        NOW(),
        'T-shirt Nike Basic Blanc - vue 2',
        'https://images.pexels.com/photos/5325887/pexels-photo-5325887.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),


    -- ========================================================
    -- ADIDAS
    -- ========================================================

    (
        4,
        2,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'Adidas Runner - image principale',
        'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),

    -- Blanc
    (
        5,
        2,
        3,
        true,
        0,
        NOW(),
        NOW(),
        'Adidas Runner Blanc',
        'https://images.pexels.com/photos/1456735/pexels-photo-1456735.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        18,
        2,
        3,
        false,
        1,
        NOW(),
        NOW(),
        'Adidas Runner Blanc - vue 2',
        'https://images.pexels.com/photos/2529157/pexels-photo-2529157.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),

    -- Noir
    (
        6,
        2,
        4,
        true,
        0,
        NOW(),
        NOW(),
        'Adidas Runner Noir',
        'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        19,
        2,
        4,
        false,
        1,
        NOW(),
        NOW(),
        'Adidas Runner Noir - vue 2',
        'https://images.pexels.com/photos/267301/pexels-photo-267301.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),


    -- ========================================================
    -- PUMA
    -- ========================================================

    (
        7,
        3,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'Hoodie Puma Classic - image principale',
        'https://images.pexels.com/photos/7679720/pexels-photo-7679720.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),

    -- Gris
    (
        8,
        3,
        5,
        true,
        0,
        NOW(),
        NOW(),
        'Hoodie Puma Classic Gris',
        'https://images.pexels.com/photos/6311602/pexels-photo-6311602.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        20,
        3,
        5,
        false,
        1,
        NOW(),
        NOW(),
        'Hoodie Puma Classic Gris - vue 2',
        'https://images.pexels.com/photos/6311608/pexels-photo-6311608.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),

    -- Noir
    (
        9,
        3,
        6,
        true,
        0,
        NOW(),
        NOW(),
        'Hoodie Puma Classic Noir',
        'https://images.pexels.com/photos/6311659/pexels-photo-6311659.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        21,
        3,
        6,
        false,
        1,
        NOW(),
        NOW(),
        'Hoodie Puma Classic Noir - vue 2',
        'https://images.pexels.com/photos/6311669/pexels-photo-6311669.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),


    -- ========================================================
    -- ZARA
    -- ========================================================

    (
        10,
        4,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'Sac Zara Noir - image principale',
        'https://images.pexels.com/photos/1152077/pexels-photo-1152077.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        22,
        4,
        7,
        false,
        1,
        NOW(),
        NOW(),
        'Sac Zara Noir - vue 1 couleur',
        'https://images.pexels.com/photos/904350/pexels-photo-904350.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        23,
        4,
        7,
        false,
        2,
        NOW(),
        NOW(),
        'Sac Zara Noir - vue 2 couleur',
        'https://images.pexels.com/photos/1936848/pexels-photo-1936848.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),


    -- ========================================================
    -- LEVIS
    -- ========================================================

    (
        11,
        5,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'Jean Levis Slim - image principale',
        'https://images.pexels.com/photos/1598507/pexels-photo-1598507.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        12,
        5,
        8,
        true,
        0,
        NOW(),
        NOW(),
        'Jean Levis Slim Bleu',
        'https://images.pexels.com/photos/1082529/pexels-photo-1082529.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        24,
        5,
        8,
        false,
        1,
        NOW(),
        NOW(),
        'Jean Levis Slim Bleu - vue 2',
        'https://images.pexels.com/photos/1598508/pexels-photo-1598508.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),


    -- ========================================================
    -- CARTE CADEAU
    -- Produit sans couleur et sans taille
    -- ========================================================

    (
        13,
        6,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'Carte Cadeau 25 EUR',
        'https://images.pexels.com/photos/5632402/pexels-photo-5632402.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),


    -- ========================================================
    -- CEINTURE
    -- Couleur marron, pas de taille
    -- ========================================================

    (
        14,
        7,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'Ceinture Cuir Classique - image principale',
        'https://images.pexels.com/photos/45055/pexels-photo-45055.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        25,
        7,
        9,
        false,
        1,
        NOW(),
        NOW(),
        'Ceinture Cuir Classique Marron - vue 1',
        'https://images.pexels.com/photos/2079249/pexels-photo-2079249.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),
    (
        26,
        7,
        9,
        false,
        2,
        NOW(),
        NOW(),
        'Ceinture Cuir Classique Marron - vue 2',
        'https://images.pexels.com/photos/374680/pexels-photo-374680.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    ),


    -- ========================================================
    -- BONNET
    -- Sans couleur, taille unique
    -- ========================================================

    (
        15,
        8,
        NULL,
        true,
        0,
        NOW(),
        NOW(),
        'Bonnet Minimal',
        'https://images.pexels.com/photos/634785/pexels-photo-634785.jpeg?auto=compress&cs=tinysrgb&w=1200',
        NULL
    )

ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- STOCKS
-- ============================================================
INSERT INTO stocks (
    id,
    quantity,
    reserved_quantity,
    variant_id,
    created_at,
    updated_at
) VALUES
    (1, 50, 5, 1, NOW(), NOW()),
    (2, 35, 3, 2, NOW(), NOW()),
    (3, 40, 4, 3, NOW(), NOW()),
    (4, 20, 2, 4, NOW(), NOW()),
    (5, 15, 1, 5, NOW(), NOW()),
    (6, 25, 5, 6, NOW(), NOW()),
    (7, 18, 3, 7, NOW(), NOW()),
    (8, 30, 4, 8, NOW(), NOW()),
    (9, 22, 2, 9, NOW(), NOW()),
    (10, 12, 1, 10, NOW(), NOW()),
    (11, 999, 5, 11, NOW(), NOW()),
    (12, 14, 3, 12, NOW(), NOW()),
    (13, 20, 4, 13, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- CART ITEMS
-- ============================================================
INSERT INTO cart_items (
    id,
    cart_id,
    quantity,
    variant_id,
    created_at,
    updated_at
) VALUES
    (1, 1, 2, 1, NOW(), NOW()),
    (2, 1, 1, 4, NOW(), NOW()),
    (3, 2, 1, 6, NOW(), NOW()),
    (4, 3, 3, 9, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PROMOS
-- ============================================================
INSERT INTO promos (
    id,
    discount_value,
    is_active,
    created_at,
    end_date,
    start_date,
    updated_at,
    code
) VALUES
    (
        1,
        10.00,
        true,
        NOW(),
        '2026-12-31',
        '2026-01-01',
        NOW(),
        'WELCOME10'
    ),
    (
        2,
        20.00,
        true,
        NOW(),
        '2026-08-31',
        '2026-06-01',
        NOW(),
        'SUMMER20'
    ),
    (
        3,
        15.00,
        false,
        NOW(),
        '2025-12-31',
        '2025-01-01',
        NOW(),
        'OLDPROMO'
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PRODUCT PROMOS
-- ============================================================
INSERT INTO product_promos (
    id,
    product_id,
    promo_id,
    created_at,
    updated_at
) VALUES
    (1, 1, 1, NOW(), NOW()),
    (2, 2, 2, NOW(), NOW()),
    (3, 3, 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- TAX SETTINGS
-- ============================================================
INSERT INTO tax_settings (
    id,
    rate,
    active,
    created_at,
    updated_at
) VALUES
    (1, 0.0850, true, NOW(), NOW()),
    (2, 0.0725, false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- SHIPPING METHODS
-- ============================================================
INSERT INTO shipping_methods (
    id,
    created_at,
    updated_at,
    active,
    code,
    name,
    delivery_type,
    description,
    min_delivery_days,
    max_delivery_days,
    price,
    free_shipping_threshold
) VALUES
    (
        1,
        NOW(),
        NOW(),
        true,
        'STANDARD_HOME',
        'Standard Home Delivery',
        'HOME',
        'Reliable delivery for standard orders',
        3,
        5,
        5.99,
        120.00
    ),
    (
        2,
        NOW(),
        NOW(),
        true,
        'EXPRESS_HAND',
        'Express Hand-to-Hand',
        'HAND_TO_HAND',
        'Fast delivery with in-person handoff',
        1,
        2,
        12.99,
        NULL
    ),
    (
        3,
        NOW(),
        NOW(),
        true,
        'LOCKER_PICKUP',
        'Locker Pickup',
        'LOCKER',
        'Pick up your order at a nearby secure locker',
        2,
        4,
        3.49,
        80.00
    ),
    (
        4,
        NOW(),
        NOW(),
        true,
        'PICKUP_POINT',
        'Pickup Point',
        'PICKUP_POINT',
        'Collect from a neighborhood pickup location',
        2,
        4,
        2.99,
        70.00
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- COUPONS
-- ============================================================
INSERT INTO coupons (
    id,
    created_at,
    updated_at,
    code,
    description,
    discount_type,
    discount_value,
    minimum_order_amount,
    start_date,
    end_date,
    active,
    usage_limit,
    used_count,
    one_time_per_user
) VALUES
    (
        1,
        NOW(),
        NOW(),
        'WELCOME10',
        '10% off on first eligible orders',
        'PERCENTAGE',
        10.00,
        50.00,
        '2026-01-01 00:00:00',
        '2027-12-31 23:59:59',
        true,
        10000,
        1,
        true
    ),
    (
        2,
        NOW(),
        NOW(),
        'SAVE20',
        'Flat 20 off for orders above 100',
        'FIXED_AMOUNT',
        20.00,
        100.00,
        '2026-01-01 00:00:00',
        '2027-12-31 23:59:59',
        true,
        5000,
        0,
        false
    ),
    (
        3,
        NOW(),
        NOW(),
        'FREESHIP',
        'Free shipping on eligible orders',
        'FREE_SHIPPING',
        0.00,
        40.00,
        '2026-01-01 00:00:00',
        '2027-12-31 23:59:59',
        true,
        NULL,
        0,
        false
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- ORDERS
-- ============================================================
INSERT INTO orders (
    id,
    delivery_address_id,
    shipping_order,
    shipping_cost,
    shipping_method_name,
    delivery_type,
    delivery_min_days,
    delivery_max_days,
    shipping_first_name,
    shipping_last_name,
    shipping_street,
    shipping_city,
    shipping_state,
    shipping_zip_code,
    shipping_country,
    shipping_phone,
    coupon_code_used,
    discount_amount,
    subtotal,
    tax_rate,
    tax_amount,
    total,
    user_id,
    created_at,
    date_order,
    updated_at,
    order_number,
    status
) VALUES
    (
        1,
        1,
        5.99,
        5.99,
        'Standard Home Delivery',
        'HOME',
        3,
        5,
        'Alice',
        'Martin',
        '12 Market Street',
        'San Francisco',
        'CA',
        '94105',
        'US',
        '415-555-0101',
        'WELCOME10',
        10.00,
        59.98,
        0.20,
        11.20,
        66.17,
        1,
        NOW(),
        '2026-05-10 14:00:00',
        NOW(),
        'ORD-2026-0001',
        'PAID'
    ),
    (
        2,
        2,
        4.99,
        4.99,
        'Pickup Point',
        'PICKUP_POINT',
        2,
        4,
        'Karim',
        'Benali',
        '8 Madison Avenue',
        'New York',
        'NY',
        '10010',
        'US',
        '212-555-0102',
        NULL,
        0.00,
        59.99,
        0.20,
        12.00,
        76.98,
        2,
        NOW(),
        '2026-05-11 10:30:00',
        NOW(),
        'ORD-2026-0002',
        'PENDING'
    ),
    (
        3,
        3,
        6.99,
        6.99,
        'Express Hand-to-Hand',
        'HAND_TO_HAND',
        1,
        2,
        'Sophie',
        'Durand',
        '25 Lakeshore Drive',
        'Chicago',
        'IL',
        '60601',
        'US',
        '312-555-0103',
        NULL,
        0.00,
        209.96,
        0.20,
        41.99,
        258.94,
        3,
        NOW(),
        '2026-05-12 16:45:00',
        NOW(),
        'ORD-2026-0003',
        'SHIPPED'
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- ORDER ITEMS
--
-- IMPORTANT :
-- Les snapshots permettent de conserver l'état historique
-- du produit au moment de la commande.
-- ============================================================
INSERT INTO order_items (
    id,
    order_id,
    variant_id,
    quantity,
    unit_price,
    total_price,
    product_name_snapshot,
    variant_sku_snapshot,
    color_snapshot,
    image_url_snapshot,
    product_description_snapshot,
    size_snapshot,
    created_at,
    updated_at
) VALUES

    -- ========================================================
    -- ORDER 1 - Alice
    -- 2 x Nike Noir S
    -- ========================================================
    (
        1,
        1,
        1,
        2,
        29.99,
        59.98,
        'T-shirt Nike Basic',
        'NIKE-TS-BLK-S',
        'Noir / S',
        'https://images.pexels.com/photos/9558598/pexels-photo-9558598.jpeg?auto=compress&cs=tinysrgb&w=1200',
        'T-shirt Nike en coton confortable',
        'S',
        NOW(),
        NOW()
    ),

    -- ========================================================
    -- ORDER 2 - Karim
    -- 1 x Adidas Runner Blanc 42
    -- ========================================================
    (
        2,
        2,
        4,
        1,
        79.99,
        79.99,
        'Adidas Runner',
        'ADI-RUN-WHT-42',
        'Blanc / 42',
        'https://images.pexels.com/photos/1456735/pexels-photo-1456735.jpeg?auto=compress&cs=tinysrgb&w=1200',
        'Sneakers Adidas légères pour tous les jours',
        '42',
        NOW(),
        NOW()
    ),

    -- ========================================================
    -- ORDER 3 - Sophie
    -- 2 x Puma Gris M
    -- 2 x Puma Noir L
    -- ========================================================
    (
        3,
        3,
        6,
        2,
        59.99,
        119.98,
        'Hoodie Puma Classic',
        'PUMA-HOOD-GRY-M',
        'Gris / M',
        'https://images.pexels.com/photos/6311602/pexels-photo-6311602.jpeg?auto=compress&cs=tinysrgb&w=1200',
        'Sweat à capuche Puma doux et chaud',
        'M',
        NOW(),
        NOW()
    ),
    (
        4,
        3,
        7,
        1,
        59.99,
        59.99,
        'Hoodie Puma Classic',
        'PUMA-HOOD-BLK-L',
        'Noir / L',
        'https://images.pexels.com/photos/6311659/pexels-photo-6311659.jpeg?auto=compress&cs=tinysrgb&w=1200',
        'Sweat à capuche Puma doux et chaud',
        'L',
        NOW(),
        NOW()
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- COUPON USAGES
-- ============================================================
INSERT INTO coupon_usages (
    id,
    coupon_id,
    user_id,
    order_id,
    used_at,
    created_at,
    updated_at
) VALUES
    (
        1,
        1,
        1,
        1,
        '2026-05-10 14:00:00',
        NOW(),
        NOW()
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- DELIVERY PREFERENCES
-- ============================================================
INSERT INTO delivery_preferences (
    id,
    order_id,
    leave_at_door,
    require_signature,
    delivery_note,
    created_at,
    updated_at
) VALUES
    (
        1,
        1,
        true,
        false,
        'Leave package with concierge if absent',
        NOW(),
        NOW()
    ),
    (
        2,
        2,
        false,
        true,
        'Signature required at front desk',
        NOW(),
        NOW()
    ),
    (
        3,
        3,
        false,
        false,
        'Call 10 minutes before arrival',
        NOW(),
        NOW()
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- PAYMENTS
-- ============================================================
INSERT INTO payments (
    id,
    amount,
    order_id,
    created_at,
    paid_at,
    updated_at,
    payment_method,
    status,
    stripe_payment_intent_id
) VALUES
    (
        1,
        66.17,
        1,
        NOW(),
        '2026-05-10 14:30:00',
        NOW(),
        'CARD',
        'PAID',
        'pi_1'
    ),
    (
        2,
        76.98,
        2,
        NOW(),
        NULL,
        NOW(),
        'PAYPAL',
        'PENDING',
        'pi_2'
    ),
    (
        3,
        258.94,
        3,
        NOW(),
        '2026-05-12 17:15:00',
        NOW(),
        'CARD',
        'PAID',
        'pi_3'
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- INVOICES
-- ============================================================
INSERT INTO invoices (
    id,
    order_id,
    total,
    created_at,
    date_invoice,
    updated_at,
    invoice_number
) VALUES
    (
        1,
        1,
        66.17,
        NOW(),
        '2026-05-10',
        NOW(),
        'INV-2026-0001'
    ),
    (
        2,
        2,
        76.98,
        NOW(),
        '2026-05-11',
        NOW(),
        'INV-2026-0002'
    ),
    (
        3,
        3,
        258.94,
        NOW(),
        '2026-05-12',
        NOW(),
        'INV-2026-0003'
    )
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- RESET SEQUENCES
-- ============================================================

SELECT setval(
    pg_get_serial_sequence('users', 'id'),
    COALESCE((SELECT MAX(id) FROM users), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('delivery_addresses', 'id'),
    COALESCE((SELECT MAX(id) FROM delivery_addresses), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('carts', 'id'),
    COALESCE((SELECT MAX(id) FROM carts), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('categories', 'id'),
    COALESCE((SELECT MAX(id) FROM categories), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('products', 'id'),
    COALESCE((SELECT MAX(id) FROM products), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('product_colors', 'id'),
    COALESCE((SELECT MAX(id) FROM product_colors), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('product_categories', 'id'),
    COALESCE((SELECT MAX(id) FROM product_categories), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('product_images', 'id'),
    COALESCE((SELECT MAX(id) FROM product_images), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('variants', 'id'),
    COALESCE((SELECT MAX(id) FROM variants), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('stocks', 'id'),
    COALESCE((SELECT MAX(id) FROM stocks), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('cart_items', 'id'),
    COALESCE((SELECT MAX(id) FROM cart_items), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('promos', 'id'),
    COALESCE((SELECT MAX(id) FROM promos), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('product_promos', 'id'),
    COALESCE((SELECT MAX(id) FROM product_promos), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('shipping_methods', 'id'),
    COALESCE((SELECT MAX(id) FROM shipping_methods), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('coupons', 'id'),
    COALESCE((SELECT MAX(id) FROM coupons), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('orders', 'id'),
    COALESCE((SELECT MAX(id) FROM orders), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('delivery_preferences', 'id'),
    COALESCE((SELECT MAX(id) FROM delivery_preferences), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('coupon_usages', 'id'),
    COALESCE((SELECT MAX(id) FROM coupon_usages), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('order_items', 'id'),
    COALESCE((SELECT MAX(id) FROM order_items), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('payments', 'id'),
    COALESCE((SELECT MAX(id) FROM payments), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('invoices', 'id'),
    COALESCE((SELECT MAX(id) FROM invoices), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('tax_settings', 'id'),
    COALESCE((SELECT MAX(id) FROM tax_settings), 0) + 1,
    false
);