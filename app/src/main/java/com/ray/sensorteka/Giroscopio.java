package com.ray.sensorteka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// Clase que hace uso del Giroscopio del Dispositivo
public class Giroscopio extends AppCompatActivity {
    // Preparamos una variable de tipo TextView
    TextView txtGyroscope;

    // Variables importantes para el uso del sensor
    SensorManager sensorManager;
    Sensor gyroscopeSensor;
    SensorEventListener gyroscopeSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giroscopio);

        // Preparamos el servicio para hacer uso del sensor
        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        // Llamamos al sensor del giroscopio
        gyroscopeSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Referenciamos el TextView del Layout a la Activity
        txtGyroscope = findViewById(R.id.txtGyroscope);

        // En caso de que no se detecten valores en el giroscopio
        if(gyroscopeSensor == null) {
            // Mostramos una notificación de que es posible que no exista el sensor en el dispositivo
            Toast.makeText(this, R.string.no_gyroscope_sensor, Toast.LENGTH_SHORT).show();
        }
        // Preparamos un nuevo listener para el sensor
        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // Si el sensor detecta movimeinto a la izquierda
                if(sensorEvent.values[2] > 0.5f) {
                    // Agregamos en un TextView que se movio hacia la izquierda
                    txtGyroscope.setText(R.string.txt_rotation_izq);
                    // y pintamos la activity de color azul
                   getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    // En caso contrario agregamos en un TextView que se movio hacia la derecha
                } else if(sensorEvent.values[2] < -0.5f) {
                    txtGyroscope.setText(R.string.txt_rotation_der);
                    // y lo pintamos la activity de color amarillo
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Preparamos el sensor para poder asignarle el listener
        sensorManager.registerListener(gyroscopeSensorListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    // Cuando salgamos de la activity detenemos el listener
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorListener);
    }
}
