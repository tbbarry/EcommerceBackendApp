#!/bin/bash
# Test Script pour les 10 endpoints d'écriture admin
# Remplacer TOKEN et BASE_URL par vos valeurs

BASE_URL="http://localhost:8080"
TOKEN="YOUR_ADMIN_JWT_TOKEN"
PRODUCT_ID=1

echo "╔════════════════════════════════════════════════════╗"
echo "║   Test des 10 endpoints d'écriture ADMIN PRODUITS  ║"
echo "╚════════════════════════════════════════════════════╝"

# 1. PATCH /api/admin/products/{productId}
echo -e "\n✅ TEST 1: Modifier infos produit"
curl -X PATCH "$BASE_URL/api/admin/products/$PRODUCT_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Produit Modifié",
    "description": "Description mise à jour",
    "price": 29.99
  }' \
  -w "\nStatus: %{http_code}\n"

# 2. POST /api/admin/products/{productId}/variants
echo -e "\n✅ TEST 2: Ajouter variante"
curl -X POST "$BASE_URL/api/admin/products/$PRODUCT_ID/variants" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "TEST-SKU-001",
    "price": 29.99,
    "attributeValueIds": [1, 2],
    "quantity": 50
  }' \
  -w "\nStatus: %{http_code}\n"

# 3. PATCH /api/admin/products/{productId}/variants/{variantId}
echo -e "\n✅ TEST 3: Modifier variante"
curl -X PATCH "$BASE_URL/api/admin/products/$PRODUCT_ID/variants/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "TEST-SKU-001-UPDATED",
    "price": 34.99
  }' \
  -w "\nStatus: %{http_code}\n"

# 4. PATCH /api/admin/products/{productId}/variants/{variantId}/stock
echo -e "\n✅ TEST 4: Modifier stock variante"
curl -X PATCH "$BASE_URL/api/admin/products/$PRODUCT_ID/variants/1/stock" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 100
  }' \
  -w "\nStatus: %{http_code}\n"

# 5. POST /api/admin/products/{productId}/images
echo -e "\n✅ TEST 5: Ajouter image"
curl -X POST "$BASE_URL/api/admin/products/$PRODUCT_ID/images" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/image.jpg",
    "alt": "Image produit",
    "isMain": true,
    "displayOrder": 1
  }' \
  -w "\nStatus: %{http_code}\n"

# 6. PATCH /api/admin/products/{productId}/images/{imageId}
echo -e "\n✅ TEST 6: Modifier image"
curl -X PATCH "$BASE_URL/api/admin/products/$PRODUCT_ID/images/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "alt": "Image produit modifiée",
    "displayOrder": 2
  }' \
  -w "\nStatus: %{http_code}\n"

# 7. POST /api/admin/products/{productId}/status
echo -e "\n✅ TEST 7: Changer statut produit"
curl -X PATCH "$BASE_URL/api/admin/products/$PRODUCT_ID/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACTIVE"
  }' \
  -w "\nStatus: %{http_code}\n"

# 8. Test sans authentification (401)
echo -e "\n✅ TEST 8: Test sans token (401 Expected)"
curl -X PATCH "$BASE_URL/api/admin/products/$PRODUCT_ID" \
  -H "Content-Type: application/json" \
  -d '{"name": "Test"}' \
  -w "\nStatus: %{http_code}\n"

# 9. Test modification stock avec réservation (400 Expected)
echo -e "\n✅ TEST 9: Test stock < réservé (400 Expected)"
curl -X PATCH "$BASE_URL/api/admin/products/$PRODUCT_ID/variants/1/stock" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": -10
  }' \
  -w "\nStatus: %{http_code}\n"

# 10. GET pour vérifier les modifications
echo -e "\n✅ TEST 10: Vérifier modifications (GET detail)"
curl -X GET "$BASE_URL/api/admin/products/$PRODUCT_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n"

echo -e "\n════════════════════════════════════════════════════"
echo "Tests terminés!"
echo "════════════════════════════════════════════════════"
