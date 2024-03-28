/*
 * Adaptateur spécifique pour afficher le bilan des dépenses, sous la forme d'une liste
 * ItemDepenseAdapter.java 02/24
 */
package com.example.festiplandroid.componenthandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.festiplandroid.controller.ListeFestivals;
import com.example.festiplandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

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

    /** File d'attente pour les requêtes Volley */
    private RequestQueue fileRequest;

    private Context contexte;
    /** Regroupe les 2 TextView présents sur la vue d'un item de la liste */
    static class SauvegardeTextView {
        TextView title;
        TextView subtitle;
        CheckBox isFavorite;
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
        contexte = this.contexte;
    }

    /**
     * Renvoie la file d'attente pour les requêtes Web :
     * - si la file n'existe pas encore : elle est créée puis renvoyée
     * - si une file d'attente existe déjà : elle est renvoyée
     * On assure ainsi l'unicité de la file d'attente
     * @return RequestQueue une file d'attente pour les requêtes Volley
     */
    private RequestQueue getFileRequest() {
        if (fileRequest == null) {
            fileRequest = Volley.newRequestQueue(getContext());
        }
        // sinon
        return fileRequest;
    }



    /**
     * Permet d'affecter à chaque item de la liste, les valeurs qui
     * doivent être affichées
     * @param position position de l'élément qui doit être affiché
     * (position au sein de la liste associée à l'adaptateur)
     * @param convertView contient soit la valeur null, soit une ancienne vue
     * pour l'élément à afficher. La méthode pourra alors se
     * contenter de réactualiser cette vue
     * @param parent vue parente à laquelle la vue à renvoyer peut être rattachée
     * @return une vue qui affichera les informations adéquates dans l'item de la
     * liste situé à la position p
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Opérations de récupération de la vue
        if (convertView == null) {
            convertView = inflater.inflate(identifiantVueItem, parent, false);
        }

        // Récupération des vues à l'intérieur de l'élément de la liste
        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView subtitleTextView = convertView.findViewById(R.id.subtitle);
        CheckBox favoriteCheckBox = convertView.findViewById(R.id.checkbox);

        // Récupération de l'objet ItemFestival correspondant à cette position
        final ItemFestival item = getItem(position);

        // Définition du texte des TextViews
        assert item != null;
        titleTextView.setText(item.getNom());
        subtitleTextView.setText(item.getDuree());

        if (item.getFavoriteState() == 1) {
            favoriteCheckBox.setChecked(true);
        } else if (item.getFavoriteState() == 0) {
            favoriteCheckBox.setChecked(false);
        }

        // Gestion du clic sur la checkbox
        final int finalPosition = position; // Déclaration de position comme final
        favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération de la clé API à partir du contexte parent
                String authentication = ((ListeFestivals) getContext()).getAuthenticationKey();
                ItemFestival clickedItem = getItem(finalPosition);
                if (clickedItem != null) {
                    // Convertir l'ID en chaîne de caractères avant de l'utiliser dans Toast.makeText()
                    int itemId = clickedItem.getId();
                    boolean toutOk;
                    /*
                     * préparation du nouveau client, à ajouter, en tant qu'objet Json
                     * Les informations le concernant sont renseignées avec des valeurs par défaut,
                     * sauf le nom du magasin qui est celui renseigné par l'utilisateur
                     */
                    toutOk = true;
                    JSONObject objetAEnvoyer = new JSONObject();
                    try {
                        objetAEnvoyer.put("idFestival", itemId);
                    } catch (JSONException e) {
                        // l'exception ne doit pas se produire
                        toutOk = false;
                    }
                    // Utilisation de itemId dans Toast.makeText()
                    if (((CheckBox) v).isChecked() && toutOk) {

                        /*
                         * Préparation de la requête Volley. La réponse attendue est de type
                         * JsonObject
                         * REMARQUE : bien noter la présence du 3ème argument du constructeur qui est
                         * l'objet Json à transmettre avec la méthode POST, en fait le body de la requête
                         */
                        JsonObjectRequest requeteVolley = new JsonObjectRequest(Request.Method.POST,
                                "http://10.0.2.2:8080/festiplan/api/ajoutFavori", objetAEnvoyer,
                                // Ecouteur pour la réception de la réponse de la requête
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject reponse) {
                                        // la zone de résultat est renseignée avec le résultat
                                        // de la requête
                                        Toast.makeText(getContext(), "Ajouté aux favoris !\n"
                                                + "JSON envoyé : " + objetAEnvoyer.toString()
                                                + "Clé API : " + authentication, Toast.LENGTH_SHORT).show();

                                        //itemId + "\nCLE : " + authentication
                                    }
                                },
                                // Ecouteur en cas d'erreur
                                new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getContext(), "Problème lors de l'ajout : reessayez ultérieurement", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                // on ajoute un header, contenant la clé d'authentification
                        {
                            @Override
                            public Map getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Key", authentication);
                                return headers;
                            }
                        };
                        // ajout de la requête dans la file d'attente Volley
                        getFileRequest().add(requeteVolley);
                    } else if (toutOk) {
                        /*
                         * Préparation de la requête Volley. La réponse attendue est de type JsonObject
                         */
                        JsonObjectRequest requeteVolley = new JsonObjectRequest(
                                Request.Method.DELETE,
                                "http://10.0.2.2:8080/festiplan/api/supprimerFavori", objetAEnvoyer,
                                // Ecouteur pour la réception de la réponse de la requête
                        new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject reponse) {
                                // la zone de résultat est renseignée avec le résultat de la requête
                                Toast.makeText(getContext(), "OUIII", Toast.LENGTH_LONG).show();
                            }
                        },
                                // Ecouteur en cas d'erreur
                                new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getContext(), "JSON envoyé : " + objetAEnvoyer.toString()
                                                                   + "Clé API : " + authentication, Toast.LENGTH_LONG).show();
                                    }
                                })
                        // on ajoute un header, contenant la clé d'authentification
                        {
                            @Override
                            public Map getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Key", authentication);
                            return headers;
                        }
                        };
                        // ajout de la requête dans la file d'attente Volley
                        getFileRequest().add(requeteVolley);
                    }
                }
            }
        });

        // Gestion du clic sur l'ensemble de l'élément de la liste
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenez la ListView parente à partir de la vue fournie par le convertView
                ListView listView = (ListView) parent;

                // Simuler un clic sur l'élément de la liste à la position donnée
                listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
            }
        });

        return convertView;
    }

}
