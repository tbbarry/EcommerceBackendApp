package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacetFilter {

    /**
     * ID de la facette.
     * Exemple :
     * 1 = Couleur
     * 2 = Taille
     */
    private Long facetId;

    /**
     * IDs des valeurs sélectionnées.
     * Exemple :
     * Couleur : [1, 2]
     * Taille  : [6, 7]
     */
    private List<Long> valueIds = new ArrayList<>();
}