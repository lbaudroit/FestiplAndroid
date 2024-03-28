package com.example.festiplandroid.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.festiplandroid.R;
import com.example.festiplandroid.model.RequestAPI;

public class MainActivity extends AppCompatActivity {

    private EditText inputId;

    private EditText inputPassword;

    public static final String EXTRA_LOGIN = "login" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        inputId = findViewById(R.id.fieldLogin);
        inputPassword = findViewById(R.id.fieldPwd);
    }

    public void connection(View bouton) {
        String identifiant = inputId.getText().toString();
        String password = inputPassword.getText().toString();
        if (identifiant.trim().isEmpty() || password.trim().isEmpty()) {
            // message d'erreur dans un toast
            Toast.makeText(this, R.string.messageToast, Toast.LENGTH_LONG).show();
        } else {
            RequestAPI requestAPI = new RequestAPI(this);
            requestAPI.connectionAPI(identifiant, password);
        }
    }
}