package com.backend.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageUploadService {
    
    /**
     * Upload une image et retourne le chemin/URL de sauvegarde
     * @param file Le fichier image (JPG, PNG, GIF, WEBP)
     * @param productId L'ID du produit (pour organiser les fichiers)
     * @return Le chemin relatif/URL où l'image a été sauvegardée
     * @throws IOException Si erreur d'écriture
     * @throws IllegalArgumentException Si le fichier n'est pas valide
     */
    String uploadProductImage(MultipartFile file, Integer productId) throws IOException;

    String storeProductImageData(String data, Integer productId);
    
    /**
     * Supprime une image du système de fichiers
     * @param imagePath Le chemin de l'image à supprimer
     */
    void deleteProductImage(String imagePath);
    
    /**
     * Obtient l'URL complète d'une image
     * @param imagePath Le chemin relatif de l'image
     * @return L'URL complète pour accéder à l'image
     */
    String getImageUrl(String imagePath);
}
