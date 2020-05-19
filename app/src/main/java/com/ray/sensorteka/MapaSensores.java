package com.ray.sensorteka;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;

import androidx.annotation.NonNull;

import timber.log.Timber;

// Clase que permite usar sensores en un mapa de Mapbox
public class MapaSensores extends AppCompatActivity implements SensorEventListener {

    // Declaración de Variables importantes
    /* Variables de Mapbox */
    private MapboxMap mapboxMap;
    private MapView mapView;
    /* Variable para uso del sensor */
    private SensorManager sensorManager;
    private SensorControl sensorControl;
    /*Variables para almacenar los valores de los sensores*/
    private float[] gravityArray;
    private float[] magneticArray;
    private float[] inclinationMatrix = new float[9];
    private float[] rotationMatrix = new float[9];

    // Constantes para el uso de los sensores
    private static final int PITCH_AMPLIFIER = -90;
    private static final int BEARING_AMPLIFIER = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializamos Mapbox con nuestro Token Unico
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_mapa_sensores);

        // Referenciamos el Mapa
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        // Preparamos el mapa para su uso a traves de Lamba Functions
        mapView.getMapAsync(map -> {
            mapboxMap = map;
            // Le damos un tema Oscuro al mapa y le asignamos un metodo para modelar en 3D
            mapboxMap.setStyle(Style.DARK, this::setupBuildingExtrusionPlugin);
        });

    }

    // Metodo que permite modelar el el mapa en 3D
    private void setupBuildingExtrusionPlugin(@NonNull Style style) {
        // Preparamos el plugin para modelar el mapa en 3D
        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
        // A la modelacion le damos un color gris claro
        buildingPlugin.setColor(Color.LTGRAY);
        // Le damos una opacidad del 60%
        buildingPlugin.setOpacity(0.6f);
        // Le damos una vista minima de 15
        buildingPlugin.setMinZoomLevel(15);
        // Hademos la modelación visible
        buildingPlugin.setVisibility(true);
    }

    // Sobrecarga del metodo onStart
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        // En el preparamos el servicio de sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Si se detectan datos en los sensores
        if (sensorManager != null) {
            // registramos el listener
            sensorControl = new SensorControl(sensorManager);
            registerSensorListeners();
        }
    }

    // Sobrecargamos los demas metodos del ciclo de vida para optimización del mapa
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        // Detenemos los listener del giroscopio y el sensor magnetico
        sensorManager.unregisterListener(this, sensorControl.getGyro());
        sensorManager.unregisterListener(this, sensorControl.getMagnetic());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    // Sobrecarga del metodo onSensorChanged
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Asignamos los valores del acelerometro en el arreglo de datos de gravitacion
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityArray = event.values;
        }

        // Asignamos los valores del sensor magnetico en el arreglo de datos magneticos
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticArray = event.values;
        }
        // Si los datos obtenidos de los sensores no son nulos
        if (gravityArray != null && magneticArray != null) {
            // Asignamos en una variable booleana si existio rotacion o no por los sensores
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix,
                    gravityArray, magneticArray);
            // En caso de haber existido valores
            if (success) {
                // Y el mapa se haya cargado
                if (mapboxMap != null) {
                    // Asiganamos una variable para la velocidad de animacion de la camara
                    int mapCameraAnimationMillisecondsSpeed = 100;
                    // Movemos la camara segun los movimientos detectdos por los sensores
                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(createNewCameraPosition()), mapCameraAnimationMillisecondsSpeed
                    );
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// Intentionally left empty
    }

    // Creamos un metodo para el posicionamiento de camara
    private CameraPosition createNewCameraPosition() {
        // Creamos un arreglo para asignar la orientacion de la camara
        float[] orientation = new float[3];
        // Preparamos los sensores para asignar una orientacion
        SensorManager.getOrientation(rotationMatrix, orientation);
        // Asignamos variables para segmentar la orientacion
        float pitch = orientation[1];
        float roll = orientation[2];

        // Devolvemos una posicion para la camara
        return new CameraPosition.Builder()
                .tilt(pitch * PITCH_AMPLIFIER)
                .bearing(roll * BEARING_AMPLIFIER)
                .build();
    }

    // Metodo que permite veriificar si hay los tipos de sensores requeridos en la aplicacion
    private void registerSensorListeners() {
        int sensorEventDeliveryRate = 200;
        // Si se detectan datos en el giroscopio
        if (sensorControl.getGyro() != null) {
            sensorManager.registerListener(this, sensorControl.getGyro(), sensorEventDeliveryRate);
        } else {
            Toast.makeText(this, R.string.no_accelerometer, Toast.LENGTH_SHORT).show();
        }
        // Si se detectan datos en el sensor magnetico
        if (sensorControl.getMagnetic() != null) {
            // Preparamos el listener y detectamos los datos
            sensorManager.registerListener(this, sensorControl.getMagnetic(), sensorEventDeliveryRate);
        } else {
            // en caso contrario significa que no existe el sensor magnetico
            Timber.d("Whoops, no magnetic sensor");
            Toast.makeText(this, R.string.no_magnetic, Toast.LENGTH_SHORT).show();
        }
    }

    // Clase auxiliar para el uso de los sensores del acelerometro y magnetico
    private class SensorControl {
        private Sensor gyro;
        private Sensor magnetic;

        // Constructor que prepara los sensores magnetico y acelerometro
        SensorControl(SensorManager sensorManager) {
            this.gyro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            this.magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        // Metodos get de la clase auxiliar
        Sensor getGyro() {
            return gyro;
        }

        Sensor getMagnetic() {
            return magnetic;
        }
    }
}
