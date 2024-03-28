package com.example.festiplandroid.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.festiplandroid.R;
import com.example.festiplandroid.componenthandler.ItemFestival;
import com.example.festiplandroid.componenthandler.ItemFestivalAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ListeFestivals extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /** URL pour récupérer les informations relatives à la connexion */
    private static final String URL_FESTIVALS = "http://10.0.2.2:8080/festiplan/api/festivals";

    public static final String EXTRA_LOGIN = "login" ;

    public static final String EXTRA_DETAILS = "details" ;

    /** URL pour récupérer les informations relatives à la connexion */
    private static final String NOM_CLE_AUTHENTIFICATION = "Key";

    /** File d'attente pour les requêtes Volley */
    private RequestQueue fileRequest;

    /**
     * Liste avec les données à afficher dans la liste des dépenses
     * Chaque élément de cette liste contient 2 informations : nature et montant
     * C'est la source des données à afficher
     */
    private ArrayList<ItemFestival> listeFestivalAAfficher;

    /** Adaptateur pour gérer la liste des dépenses à afficher */
    private ItemFestivalAdapter adaptateurFestival;

    /** Widget liste */
    private ListView listeAffichee;

    String information;

    private TextView withoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.festivals_list);

        listeAffichee = findViewById(R.id.list);
        withoutList = findViewById(R.id.withoutList);

        getTypeClientResultatFormate();

        Intent intention = getIntent();
        information = intention.getStringExtra(MainActivity.EXTRA_LOGIN);

        listeAffichee.setOnItemClickListener(this);
    }


    /**
     * Utilisation de la méthode GET pour consulter la liste de tous les types de clients
     * Cette requête nécessite un header contenant les données d'authentification
     * Avec cette version : le résultat de la requête est affiché formaté avec seulement
     * le nom des types de clients. Il est donc nécessaire d'extraire des données de
     * l'objet de type Jsonarray, résultat de la requête
     */
    private void getTypeClientResultatFormate() {
        final Context context = this;
        /*
         * Préparation de la requête Volley. La réponse attendue est de type JsonArray
         */
        JsonArrayRequest requeteVolley = new JsonArrayRequest(Request.Method.GET,
                URL_FESTIVALS, null,
                // Ecouteur pour la réception de la réponse de la requête
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray reponse) {
                        // la zone de résultat est renseignée après extraction des
                        // types de clients
                        if (reponse.length() == 0) {
                            withoutList.setText(R.string.no_festival);
                        }
                        /* initialisation de la liste source des données à afficher :
                         * liste qui contient des instances de type ItemDepense
                         */
                        listeFestivalAAfficher = new ArrayList<>();
                        try {
                            for (int i = 0; i < reponse.length(); i++) {
                                String dateDebutString = reponse.getJSONObject(i).getString("dateDebut");
                                String dateFinString = reponse.getJSONObject(i).getString("dateFin");

                                String formattedDate ="";
                                // Créer un objet SimpleDateFormat pour le format d'entrée
                                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

                                // Créer un objet SimpleDateFormat pour le format de sortie
                                DateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
                                try {
                                    // Convertir la chaîne de date en objet Date
                                    Date dateDebut = inputFormat.parse(dateDebutString);
                                    Date dateFin = inputFormat.parse(dateFinString);

                                    // Formater la date dans le style requis
                                    assert dateDebut != null;
                                    assert dateFin != null;
                                    formattedDate = "Du " + outputFormat.format(dateDebut) + " au " + outputFormat.format(dateFin);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                    listeFestivalAAfficher.add(new ItemFestival(reponse.getJSONObject(i).getString("titre"),
                                            formattedDate , reponse.getJSONObject(i).getInt("idFestival"), reponse.getJSONObject(i).getInt("favori")));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        // création de l'adaptateur
                        adaptateurFestival = new ItemFestivalAdapter(context,
                                R.layout.custom_list_element, listeFestivalAAfficher);

                        // on associe l'adaptateur au widget de type ListView
                        listeAffichee.setAdapter(adaptateurFestival);

                    }
                },
                // Ecouteur en cas d'erreur
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext() , R.string.messageToast3, Toast.LENGTH_LONG).show();
                    }
                })
                // on ajoute un header, contenant la clé d'authentification
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(NOM_CLE_AUTHENTIFICATION, information);
                return headers;
            }
        };
        // ajout de la requête dans la file d'attente Volley
        getFileRequest().add(requeteVolley);
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
            fileRequest = Volley.newRequestQueue(this);
        }
        // sinon
        return fileRequest;
    }

    /**
     * Méthode invoquée automatiquement si l'utilisateur sélectionne un élément
     * de la liste (méthode définie dans l'interface OnItemClickListener)
     *
     * @param parent Element ListView dans lequel le clic s'est produit
     * @param v La vue sur laquelle l'utilisateur a cliqué (un élément de la liste)
     * @param position La position de cette vue dans la liste
     * @param id L'identifiant de l'élément sur lequel le clic s'est produit
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // Obtenez l'élément de la liste sur lequel l'utilisateur a cliqué
        ItemFestival item = listeFestivalAAfficher.get(position);
        int idFestival = item.getId();
        // création d'une intention pour demander lancement de l'activité secondaire
        Intent intention = new Intent(ListeFestivals.this, DetailsFestival.class);
        intention.putExtra(EXTRA_DETAILS, idFestival);
        intention.putExtra(EXTRA_LOGIN, information);


        // lancement de l'activité secondaire via l'intention préalablement créée
        startActivity(intention);
    }

    /**
     * Récupère la clé d'authentification API.
     * @return La clé d'authentification API
     */
    public String getAuthenticationKey() {
        Intent intent = getIntent();
        return intent.getStringExtra(MainActivity.EXTRA_LOGIN);
    }
}