/*
 * Item décrivant un festival affiché via une liste
 * ItemFestival.java                          02/24
 */

package com.example.festiplandroid;

/**
 * Cette classe rassemble les 3 informations qui apparaîtront en tant
 * qu'élément de la liste des festivals affichés qui ne sont pas encore passé.
 * Les 3 informations sont :
 *     - Le nom du festival
 *     - La date de début et de fin du festival
 *     - Si le festival en question fait partie des favoris ou non
 * @author INFO2
 * @version 1.0
 */
public class ItemFestival {

    /** Nom du festival */
    private final String nom;

    /** Durée du festival */
    private final String duree;


    /**
     * Constructeur avec en argument les valeurs des attributs
     * @param nom nom du festival
     * @param duree durée du festival
     */
    public ItemFestival(String nom, String duree) {
        this.nom = nom;
        this.duree = duree;
    }
    /**
     * Accesseur du nom du festival
     * @return une chaîne contenant le nom du festival
     */
    public String getNom() {
        return nom;
    }

    /**
     * Accesseur sur la durée du festival
     * @return une chaîne contenant la date de début et la date de fin du festival
     */
    public String getDuree() {
        return duree;
    }
}
