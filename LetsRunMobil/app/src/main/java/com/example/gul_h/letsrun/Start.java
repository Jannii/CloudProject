package com.example.gul_h.letsrun;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gul_h on 2017-05-03.
 */

public class Start extends AppCompatActivity implements LocationListener, SensorEventListener {

    //<-----Private value for detemining if the start and stop function----->//
    private boolean startAndStopBool = false;
    //<-----Variable for the locationManager----->//
    private LocationManager locationManager;
    //<-----Packs of gathered data that should be sent to AZURE----->//
    private ArrayList<String> packOfLocations = new ArrayList();
    private ArrayList<String> packOfStepCounter = new ArrayList();
    private ArrayList<String> packOfTemperature = new ArrayList();
    //<-----Temporary variables, used for EG. Sorting out unused data----->//
    private String cordinations = "-";
    private float steps;
    //<-----SensorManager, handles all of the sensors----->//
    private SensorManager mSensorManager;
    //<-----All the underlying sensors----->//
    private Sensor mStepCounter;
    private Sensor mTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //<-----Find all the sensors on the phone and print them----->//
        checkAllSensorsOnPhone();



        //<-----Find components in view----->//
        final Button startAndStop = (Button) findViewById(R.id.start);

        //<-----Method for performing start and stop operations----->//
        startAndStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //<-----Click on button action----->//

                //<-----Change text on button and call tickListener----->//
                if (startAndStopBool == false) {
                    System.out.println("Starting gathering");
                    startAndStop.setText("Stop Running");
                    startTimer();
                    gatherStepCounter();

                } else {
                    System.out.println("Stopping gathering");
                    startAndStop.setText("Start Running");
                    stopTimer();
                    gatherStepCounter();
                }

            }

        });
        //<-----Trigger listener for sensors----->//
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //<-----Listeners to trigger----->//
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //<-----Variables for timerCode----->//
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {

        //<-----Actions every tick----->//
        @Override
        public void run() {
            //<-----Run the 3 methods for gathering information----->//
            gatherGPSdata();
        }
    };

    //<-----Start the timer----->//
    public void startTimer() {
        if (timer != null) {
            return;
        }

        //<-----Create a new timer that tick every x(2000) milliseconds----->//

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 2000);
    }

    //<-----Stop the timer----->//
    public void stopTimer() {
        timer.cancel();
        timer = null;
    }

    public void gatherGPSdata() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //<-----Check if the required permissions is in the manifest----->//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //<-----Do something if they dont exist----->//
            System.out.println("Error1000: Check manifestfile for location permissions");
            return;
        }
        //<-----Start the locationListener And Request Data----->//
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);

        //<-----Gather information into pack----->//
        packOfLocations.add(cordinations);

    }
    public void gatherStepCounter(){
        //<-----If reset, reset the list aswell so dont send already sent value----->//
        if(packOfStepCounter.size() >= 2){
            packOfStepCounter.clear();
        }

        //<-----Gather start and stop values, and the value between is walken steps----->//
        if (startAndStopBool == false){
            packOfStepCounter.add(String.valueOf(steps));

        }else{
            packOfStepCounter.add(String.valueOf(steps));
        }

    }
    public void gatherTemperature(){


    }
    @Override
    public void onLocationChanged(Location location) {

        //<-----OnLocationChanged, Save the cordinations in temp----->//
        if(startAndStopBool == true) {
            //<-----Pause information Gathering----->//
        }
        else{
            //<-----Resume information Gathering----->//
            cordinations = "X:" + location.getLatitude() + "Y:" + location.getLongitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

        //<-----If GPS is off----->//
        System.out.println("Error1001: GPS Function is shut down.");
    }

    @Override
    public void onProviderEnabled(String provider) {

        //<-----If GPS gets enabled----->//
        System.out.println("GPS Enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //<-----Auto Generated code----->//

    }

    //<-----When a sensor changes----->//
    @Override
    public void onSensorChanged(SensorEvent event) {
        //<-----Store sensor values into temps----->//
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            steps = event.values[0];
        }

    }

    //<-----When the accuracy of a sensor changes----->//
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void checkAllSensorsOnPhone(){

        //<-----Check for all the sensors on the phone----->//
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);


        //<-----Loop though the list and print them out----->//
        for(int i = 0; i < deviceSensors.size(); i++){
            System.out.println("Sensor" + i + ": " + deviceSensors.get(i).toString() );
        }

    }
}