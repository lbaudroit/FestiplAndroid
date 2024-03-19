/*
 * Adaptateur spécifique pour afficher le bilan des dépenses, sous la forme d'une liste
 * ItemDepenseAdapter.java 02/24
 */
package com.example.festiplandroid;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Classe qui représente un adaptateur pour afficher une liste.
 * Plus précisément, cet adaptateur servira à afficher la liste contenant
 * un bilan des dépenses pour une année précise.
 * Les items de cette liste contiennent 2 informations : la nature de la
 * dépense et le montant (instance de type ItemDepense).
 * La classe hérite de ArrayAdapter. Rappel : la classe ArrayAdapter permet
 * de gérer une liste dont chaque item est constitué d'une seule valeur.
 * La classe ItemDepenseAdapter permettra quant à elle de gérer une liste
 * dont chaque item est constitué de 2 valeurs.
 * @author C. Servières
 * @version 1.0
 */

public class ItemFestivalAdapter extends ArrayAdapter<ItemFestival> {

    /** Identifiant de la vue permettant d'afficher chaque item de la liste */
    private int identifiantVueItem;
    /**
     * Objet utilitaire permettant de désérialiser une vue
     */
    private LayoutInflater inflater;
    /** Regroupe les 2 TextView présents sur la vue d'un item de la liste */
    static class SauvegardeTextView {
        TextView natureDepense;
        TextView montantDepense;
    }
    /**
     * Constructeur de l'adaptateur
     * @param contexte contexte de création de l'adaptateur
     * @param vueItem identifiant de la vue permettant d'afficher chaque
     * item de la liste
     * @param lesItems Liste de items à afficher
     */
    public ItemFestivalAdapter(Context contexte, int vueItem,
                                      List<ItemFestival> lesItems) {
        super(contexte, vueItem, lesItems);
        this.identifiantVueItem = vueItem;
        inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Permet d'affecter à chaque item de la liste, les valeurs qui
     * doivent être affichées
     * @param position position de l'élément qui doit être affiché
     * (position au sein de la liste associée à l'adaptateur)
     * @param uneVue contient soit la valeur null, soit une ancienne vue
     * pour l'élément à afficher. La méthode pourra alors se
     * contenter de réactualiser cette vue
     * @param parent vue parente à laquelle la vue à renvoyer peut être rattachée
     * @return une vue qui affichera les informations adéquates dans l'item de la
     * liste situé à la position p
     */
    @NonNull
    @Override
    public View getView(int position, View uneVue, @NonNull ViewGroup parent) {
        // on récupère la valeur de l'item à afficher, via sa position
        ItemFestival ligneBilan = getItem(position);
        SauvegardeTextView sauve; // regroupe les 2 TextView présents sur la vue
        // destinée à afficher l'item
        if (uneVue == null) {
            /*
             * la vue décrivant chaque item de la liste n'est pas encore créée
             * Il faut désérialiser le layout correspondant à cette vue.
             */
            uneVue = inflater.inflate(identifiantVueItem, parent, false);
            // on récupère un accès sur les 2 TextView qu'il faudra renseigner
            sauve = new SauvegardeTextView();
            sauve.natureDepense = uneVue.findViewById(R.id.title);
            sauve.montantDepense = uneVue.findViewById(R.id.subtitle);
            // on stocke les identifiants de 2 TextView dans la vue elle-même
            uneVue.setTag(sauve);
        } else {
            // on récupère les identifiants des 2 TextView stockés dans la vue
            sauve = (SauvegardeTextView) uneVue.getTag();
        }
        // on place dans les 2 TextView les valeurs de l'item à afficher
        assert ligneBilan != null;
        sauve.natureDepense.setText(ligneBilan.getNom());
        sauve.montantDepense.setText(ligneBilan.getDuree());
        return uneVue;
    }

}
