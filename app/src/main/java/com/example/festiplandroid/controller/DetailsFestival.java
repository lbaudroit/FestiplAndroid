package com.example.festiplandroid.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.festiplandroid.model.RequestAPI;
import com.google.android.material.chip.Chip;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailsFestival extends AppCompatActivity {

    private TextView description;

    private TextView title;

    private TextView duration;

    private ListView listOrganizer;

    private ListView listShow;

    private Chip chip;

    /** File d'attente pour les requêtes Volley */
    private RequestQueue fileRequest;

    private ArrayAdapter<String> adaptater;

    private ArrayAdapter<String> adaptater2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.festival_details);

        description = findViewById(R.id.description);
        title = findViewById(R.id.title);
        chip = findViewById(R.id.chip);
        duration = findViewById(R.id.duration);
        listOrganizer = findViewById(R.id.listOrganizer);
        listShow = findViewById(R.id.listShow);

        // Appel de la méthode getDetailsFestival à partir de l'objet RequestAPI
        RequestAPI requestAPI = new RequestAPI(this);
        Intent intention = getIntent();
        String authentication = intention.getStringExtra(MainActivity.EXTRA_LOGIN);
        int identification = intention.getIntExtra(ListeFestivals.EXTRA_DETAILS, 0);
        requestAPI.getDetailsFestival(identification, authentication, title, chip, description, duration, listOrganizer, listShow);
    }
}
