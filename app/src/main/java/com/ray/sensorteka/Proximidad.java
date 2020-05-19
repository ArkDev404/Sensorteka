package com.ray.sensorteka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

// Clase que hace uso del sensor de proximidad
public class Proximidad extends AppCompatActivity {

    TextView txtProximity;

    SensorManager sensorManager;
    SensorEventListener proximityListener;
    Sensor proximitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximidad);

        // Preparamos los servicios de sensores
        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        // Preparamos el sensor de proximidad
        proximitySensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        txtProximity = findViewById(R.id.txtProximity);

        // Si el sensor de proximidad no arroja valores
        if(proximitySensor == null) {
            // Es probable que no haya sensor y se notificia en un Toast
            Toast.makeText(this, R.string.no_proximity_sensor, Toast.LENGTH_SHORT).show();
            // Se redirige al Menu de opciones
            Intent intent = new Intent(this, MenuOpciones.class);
            startActivity(intent);
        }

        // Creamos un nuevo listener de tipo sensor
        proximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // Si existe algun cambio de valor en el sensor
                if(sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    // Preparamos el servicio de vibración
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        // Le damos una duracion de 0.6 segundos
                        vibrator.vibrate(600);
                    }
                    // Mostramos que se activo el sensor
                    txtProximity.setText(R.string.proximityOn);
                } else {
                    // Mostramos que no se detectaron valores en el sensor
                    txtProximity.setText(R.string.proximityOff);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Este metodo puede ir vacio
            }
        };
        // Hacemos un calculo para los valores que pueda arrojar el sensor
        sensorManager.registerListener(proximityListener,
                proximitySensor, 2 * 1000 * 1000);

    }


    // Sobrecarga del metodo onPause
    @Override
    protected void onPause() {
        super.onPause();
        // Detenemos el escuchador
        sensorManager.unregisterListener(proximityListener);
    }


}
