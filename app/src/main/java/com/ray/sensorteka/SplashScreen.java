package com.ray.sensorteka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

// Clase para el Splash Screen
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Metodo tipo Lamba que permite crear un hilo de 3 segundos y nos manda a la Activity
        // de opciones
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, MenuOpciones.class);
            startActivity(intent);
            //Destruimos la activity para no evitar regresar al Splash desde la APP
            finish();
        },3000);

    }
}
