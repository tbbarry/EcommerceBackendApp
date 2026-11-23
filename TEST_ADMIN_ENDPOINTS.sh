#!/bin/bash
# Script de test des endpoints Admin Produits
# Utilisez-le pour tester les endpoints après le déploiement

API_URL="http://localhost:8080/api/admin/products"
ADMIN_TOKEN="YOUR_ADMIN_JWT_TOKEN_HERE"

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   Test des endpoints Admin Produits                            ║"
echo "║   API: $API_URL                      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Vérifier que le token est fourni
if [ "$ADMIN_TOKEN" == "YOUR_ADMIN_JWT_TOKEN_HERE" ]; then
    echo "⚠️  ATTENTION: Remplacez YOUR_ADMIN_JWT_TOKEN_HERE par votre token JWT"
    echo ""
    echo "Pour obtenir un token:"
    echo "  1. Appelez POST /api/auth/login avec vos identifiants ADMIN"
    echo "  2. Copiez le token reçu"
    echo "  3. Remplacez la valeur de ADMIN_TOKEN"
    echo ""
    exit 1
fi

echo "✅ Token d'authentification détecté"
echo ""

# TEST 1: Récupérer la liste des produits (page 0, 10 éléments)
echo "════════════════════════════════════════════════════════════════"
echo "TEST 1: Récupérer la liste paginée des produits"
echo "════════════════════════════════════════════════════════════════"
echo "GET /api/admin/products?page=0&size=10"
echo ""

curl -X GET "${API_URL}?page=0&size=10" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -s | jq .

echo ""
echo ""

# TEST 2: Récupérer les détails du produit ID=1
echo "════════════════════════════════════════════════════════════════"
echo "TEST 2: Récupérer les détails complets du produit ID=1"
echo "════════════════════════════════════════════════════════════════"
echo "GET /api/admin/products/1"
echo ""

curl -X GET "${API_URL}/1" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -s | jq .

echo ""
echo ""

# TEST 3: Test sans authentification (devrait retourner 401)
echo "════════════════════════════════════════════════════════════════"
echo "TEST 3: Test SANS authentification (devrait retourner 401)"
echo "════════════════════════════════════════════════════════════════"
echo "GET /api/admin/products (sans token)"
echo ""

curl -X GET "${API_URL}" \
  -H "Content-Type: application/json" \
  -s -w "\nHTTP Status: %{http_code}\n" | jq .

echo ""
echo ""

# TEST 4: Test avec pagination (page 1, 20 éléments)
echo "════════════════════════════════════════════════════════════════"
echo "TEST 4: Test pagination - Page 1, 20 éléments par page"
echo "════════════════════════════════════════════════════════════════"
echo "GET /api/admin/products?page=1&size=20"
echo ""

curl -X GET "${API_URL}?page=1&size=20" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -s | jq .

echo ""
echo ""

# TEST 5: Test avec un ID inexistant (devrait retourner 404)
echo "════════════════════════════════════════════════════════════════"
echo "TEST 5: Test avec un ID inexistant (devrait retourner 404)"
echo "════════════════════════════════════════════════════════════════"
echo "GET /api/admin/products/99999"
echo ""

curl -X GET "${API_URL}/99999" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -s -w "\nHTTP Status: %{http_code}\n"

echo ""
echo ""

echo "════════════════════════════════════════════════════════════════"
echo "✅ Tests terminés!"
echo "════════════════════════════════════════════════════════════════"
echo ""
echo "Résultats attendus:"
echo "  TEST 1: 200 OK - Array de ProductAdminListResponse"
echo "  TEST 2: 200 OK - ProductAdminDetailResponse complet"
echo "  TEST 3: 401 Unauthorized"
echo "  TEST 4: 200 OK - Page 1 avec 20 éléments"
echo "  TEST 5: 404 Not Found"
