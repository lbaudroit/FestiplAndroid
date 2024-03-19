package com.example.festiplandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * Tableau des dépenses qui seront affichées : nature de chacune des
     * dépenses */
    private static final String[] TITRES =
            { "Titre 1", "Titre 2", "Titre 3",
                    "Titre 4", "Titre 5", "Titre 6" };
    /**
     * Tableau des dépenses qui seront affichées : montant de chacune des
     * dépenses */
    private static final String[] SOUSTITRES = { "Sous-titre 1", "Sous-titre 2", "Sous-titre 3",
                                                "Sous-titre 4", "Sous-titre 5", "Sous-titre 6" };
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listeAffichee = findViewById(R.id.list);
        /*
         * initialisation de la liste source des données à afficher :
         * liste qui contient des instances de type ItemDepense
         */
        listeFestivalAAfficher = new ArrayList<>();
        for (int i = 0; i < TITRES.length; i++) {
            listeFestivalAAfficher.add(new ItemFestival(TITRES[i],
                    SOUSTITRES[i]));
        }

        // création de l'adaptateur
        adaptateurFestival = new ItemFestivalAdapter(this,
                R.layout.custom_list_element, listeFestivalAAfficher);
        // on associe l'adaptateur au widget de type ListView
        listeAffichee.setAdapter(adaptateurFestival);
    }
}