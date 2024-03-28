package com.example.festiplandroid.model;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.festiplandroid.R;
import com.example.festiplandroid.controller.ListeFestivals;
import com.example.festiplandroid.controller.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestAPI extends AppCompatActivity {

    /** File d’attente pour les requêtes Volley */
    private RequestQueue fileRequest;
    
    private static final String AUTHENTICATION_KEY_NAME = "Key";

    /** URL pour récupérer les informations relatives à un festival précis  */
    private static final String URL_DETAILS_FESTIVAL = "http://10.0.2.2:8080/api/festival/%d";

    /** URL pour récupérer les informations relatives à la connexion */
    private static final String URL_CONNECTION = "http://10.0.2.2:8080/festiplan/api/login/%s/%s";

    /** URL pour récupérer tous les festivals (dont la date n’est pas encore passée) */
    private static final String URL_FESTIVALS = "http://10.0.2.2:8080/festiplan/api/festivals";

    private final Context context;

    public RequestAPI(Context context) {
        this.context = context;
    }

    public void connectionAPI(String id, String password) {
        try {
            // le titre saisi par l’utilisateur est récupéré et encodé en UTF-8
            id = URLEncoder.encode(id, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");
            // le titre du film est inséré dans l’URL de recherche du film
            String url = String.format(URL_CONNECTION, id, password);

            /*
             * on crée une requête GET, paramétrée par l’url préparée ci-dessus,
             * Le résultat de cette requête sera un objet JSon, donc la requête est de type
             * JsonObjectRequest
             */
            JsonObjectRequest requestVolley = new JsonObjectRequest(Request.Method.GET, url,
                    null,
                    // Ecouteur de la réponse renvoyée par la requête
                    response -> {
                        // Création d’une intention pour demander lancement de l’activité secondaire
                        if (context != null) {
                            Intent intention = new Intent(context, ListeFestivals.class);
                            try {
                                // On initialise l’EXTRA_LOGIN avec la clé API reçue en réponse
                                intention.putExtra(MainActivity.EXTRA_LOGIN, response.getString("cle"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            context.startActivity(intention);
                        }
                    },

                    // Ecouteur du retour de la requête si aucun résultat n’est renvoyé
                    error -> Toast.makeText(context.getApplicationContext(),
                            R.string.messageToast3, Toast.LENGTH_LONG).show());

            // La requête est placée dans la file d’attente des requêtes
            getFileRequest().add(requestVolley);
        } catch (UnsupportedEncodingException e) {
            // Problème lors de l’encodage de l’id ou du mot de passe
            Toast.makeText(this, R.string.messageToast2, Toast.LENGTH_LONG).show();
        }
    }

    public void getDetailsFestival(int festivalId, String authentication, final TextView title, final TextView chip, final TextView description, final TextView duration, final ListView listOrganizer, final ListView listShow) {
        // Construction de l'URL en utilisant l'identifiant du festival
        String url = String.format(URL_DETAILS_FESTIVAL, festivalId);

        // Création d'une requête GET avec Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                // Listener pour gérer la réponse réussie de la requête
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extraction des données du JSON
                            String festivalTitle = response.getString("titre");
                            String festivalCategory = response.getString("categorie");
                            String festivalDescription = response.getString("description");
                            String festivalStartDate = response.getString("dateDebut");
                            String festivalEndDate = response.getString("dateFin");

                            // Mise à jour des vues avec les données du festival
                            title.setText(festivalTitle);
                            chip.setText(festivalCategory);
                            description.setText(festivalDescription);

                            // Formatage des dates
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
                            DateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
                            Date startDate = inputFormat.parse(festivalStartDate);
                            Date endDate = inputFormat.parse(festivalEndDate);
                            assert startDate != null && endDate != null;
                            String formattedStartDate = outputFormat.format(startDate);
                            String formattedEndDate = outputFormat.format(endDate);

                            // Affichage de la durée du festival
                            String festivalDuration = String.format(context.getResources().getString(R.string.duration), formattedStartDate, formattedEndDate);
                            duration.setText(festivalDuration);

                            // Récupération des organisateurs du festival
                            ArrayList<String> organizerNames = new ArrayList<>();
                            JSONArray organizers = response.getJSONArray("organisateurs");
                            for (int i = 0; i < organizers.length(); i++) {
                                JSONObject organizer = organizers.getJSONObject(i);
                                String organizerName = organizer.getString("prenom") + " " + organizer.getString("nom");
                                organizerNames.add(organizerName);
                            }
                            // Mise à jour de la liste des organisateurs
                            if (context != null) {
                                ArrayAdapter<String> organizerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, organizerNames);
                                listOrganizer.setAdapter(organizerAdapter);
                            }

                            // Récupération des spectacles du festival
                            ArrayList<String> showTitles = new ArrayList<>();
                            JSONArray shows = response.getJSONArray("spectacles");
                            if (shows.length() == 0) {
                                showTitles.add("Aucun spectacle n'est programmé pour le moment !");
                            } else {
                                for (int i = 0; i < shows.length(); i++) {
                                    JSONObject show = shows.getJSONObject(i);
                                    String showTitle = show.getString("titre");
                                    showTitles.add(showTitle);
                                }
                            }

                            // Mise à jour de la liste des spectacles
                            if (context != null) {
                                ArrayAdapter<String> showAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, showTitles);
                                listShow.setAdapter(showAdapter);
                            }

                        } catch (JSONException | ParseException e) {
                            // Gestion des exceptions
                            e.printStackTrace();
                            // Affichage d'un message d'erreur
                            Toast.makeText(context, "Erreur lors du traitement des données.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                // Listener pour gérer les erreurs de la requête
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Affichage d'un message d'erreur en cas d'échec de la requête
                        Toast.makeText(context, "Erreur lors de la récupération des détails du festival.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Ajout du header d'authentification à la requête
                HashMap<String, String> headers = new HashMap<>();
                headers.put(AUTHENTICATION_KEY_NAME, authentication);
                return headers;
            }
        };

        // Ajout de la requête à la file d'attente de Volley
        getFileRequest().add(request);
    }


    /**
     * Renvoie la file d’attente pour les requêtes Web :
     * - si la file n’existe pas encore : elle est créée puis renvoyée
     * - si une file d’attente existe déjà : elle est renvoyée
     * On assure ainsi l’unicité de la file d’attente
     * @return RequestQueue une file d’attente pour les requêtes Volley
     */
    private RequestQueue getFileRequest() {
        if (fileRequest == null) {
            fileRequest = Volley.newRequestQueue(context.getApplicationContext());
        }
        // sinon
        return fileRequest;
    }
}
