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
import android.widget.TextView;
import android.widget.Toast;
// Clase que hace del sensor compuesto de rotacion vectorial
public class RotacionVector extends AppCompatActivity {

    // Preparamos una variable tipo TextView
    TextView txtRotation;

    // Variables importantes para el uso del sensor
    SensorManager sensorManager;
    Sensor rotationVectorSensor;
    SensorEventListener rvListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotacion_vector);

        // Preparamos el uso de sensores en el dispositivo
        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        // Preparamos el uso del sensor de software rotacion vectorial
        rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Referenciamos el TextView con su layout
        txtRotation = findViewById(R.id.txt_rotation);

        // Creamos el listener
        rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // Preparamos los valores base para hacer uso del sensor
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, sensorEvent.values);

                // Remapeaos los valores para tener valores aproximados a los grados
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);

                // Hacemos conversiones para poder calcular los grados girados
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                // Hacemos la conversion a grados
                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }

                // Si se gira el dispositivo aprox 45 grados a la Derecha
                if(orientations[2] > 45) {
                    // Agregamos en el TextView que giramos el dispositivo a la derecha
                    txtRotation.setText(R.string.rotation_der);
                    // Y pintamos la pantalla de color amarillo
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    //  Si se gira el dispositivo aprox 45 grados a la Izquierda
                } else if(orientations[2] < -45) {
                    // Agregamos en el TextView que giramos el dispositivo a la izquierda
                    txtRotation.setText(R.string.rotation_izq);
                    // y pintamos el activity de azul
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    // En caso de que la aplicacion este menos de 45° a la izq y 45° a la derecha
                } else if(Math.abs(orientations[2]) < 10) {
                    // Agregamos en el TextView que giramos el dispositivo esta centrado
                    txtRotation.setText(R.string.rotation_center);
                    // pintamos el activity de blanco
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Registramos el listener para hacer uso de el
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    // En caso de salir del activity detenemos el listener
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rvListener);
    }
}
