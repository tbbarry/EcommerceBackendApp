# Script pour modifier les IDs dans data.sql
# Les IDs passent de 1-999 à 1000-1999

$filePath = "C:\Users\thierno.barry\Documents\Personnel\Apprentissage\EcommerceBackendApp\src\main\resources\data.sql"
$content = Get-Content $filePath -Raw

# Mapping des IDs anciens vers nouveaux (ajouter 1000 à chaque ID)
# Format : (old_id, new_id)
$mappings = @{
    # Users
    '(1,' = '(1001,'
    '(2,' = '(1002,'
    '(3,' = '(1003,'
    
    # Categories
    '(1,NULL' = '(1010,NULL'
    '(2,NULL' = '(1020,NULL'
    '(3,NULL' = '(1030,NULL'
    '(4,NULL' = '(1040,NULL'
    
    '(10,1' = '(1100,1010'
    '(11,1' = '(1110,1010'
    '(12,1' = '(1120,1010'
    
    '(20,10' = '(1200,1100'
    '(21,10' = '(1210,1100'
    '(22,10' = '(1220,1100'
    '(23,10' = '(1230,1100'
    '(24,11' = '(1240,1110'
    
    '(30,2' = '(1300,1020'
    '(31,2' = '(1310,1020'
    '(32,2' = '(1320,1020'
    
    '(40,30' = '(1400,1300'
    '(41,30' = '(1410,1300'
    '(42,30' = '(1420,1300'
    '(43,30' = '(1430,1300'
    '(44,31' = '(1440,1310'
    
    '(50,3' = '(1500,1030'
    '(51,3' = '(1510,1030'
    '(60,50' = '(1600,1500'
    '(61,50' = '(1610,1500'
    
    '(70,4' = '(1700,1040'
    '(71,4' = '(1710,1040'
}

# Appliquer tous les mappings
foreach ($old in $mappings.Keys) {
    $new = $mappings[$old]
    $content = $content -replace [regex]::Escape($old), $new
}

# Sauvegarder le fichier modifié
Set-Content -Path $filePath -Value $content -Encoding UTF8

Write-Host "✓ File updated: $filePath"
Write-Host "✓ All IDs shifted by 1000"
