package com.ray.sensorteka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

public class MenuOpciones extends AppCompatActivity {

    // Creacion de Variables de tipo Cards
    CardView cardProximidad, cardGiroscopio, cardRotacionV, cardEjemplo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_opciones);

        // Referencia de las Cards
        cardProximidad = findViewById(R.id.cardProximidad);
        cardGiroscopio = findViewById(R.id.cardGiroscopio);
        cardRotacionV = findViewById(R.id.cardRotacionV);
        cardEjemplo = findViewById(R.id.cardEjemplo);

        // Nueva sintaxis -> tipo Arrow-Lamba

        // Evento clic en el card Proximidad
        cardProximidad.setOnClickListener(v -> {
            Intent intent = new Intent(this, Proximidad.class);
            startActivity(intent);
        });

        // Evento clic en el card Giroscopio
        cardGiroscopio.setOnClickListener(v -> {
            Intent intent = new Intent(this,Giroscopio.class);
            startActivity(intent);
        });

        // Evento clic en el card Rotacion Vector
        cardRotacionV.setOnClickListener(v -> {
            Intent intent = new Intent(this,RotacionVector.class);
            startActivity(intent);
        });

        // Evento clic en el card Ejemplo
        cardEjemplo.setOnClickListener(v -> {
            Intent intent = new Intent(this,MapaSensores.class);
            startActivity(intent);
        });

    }
}
