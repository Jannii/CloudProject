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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private ArrayList<String> packOfHeartRate = new ArrayList();
    //<-----Temporary variables, used for EG. Sorting out unused data----->//
    private String cordinations = "-";
    private float steps = 0;
    private float heart = 0;
    //<-----SensorManager, handles all of the sensors----->//
    private SensorManager mSensorManager;
    //<-----All the underlying sensors----->//
    private Sensor mStepCounter;
    private Sensor mHeartRate;

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
                    startAndStopBool = true;
                    System.out.println("Starting gathering");
                    startAndStop.setText("Stop Running");
                    startTimer();
                    gatherStepCounter();

                } else {
                    startAndStopBool = false;
                    System.out.println("Stopping gathering");
                    startAndStop.setText("Start Running");
                    stopTimer();
                    gatherStepCounter();
                    debugSendBatch();
                }

            }

        });
        //<-----Trigger listener for sensors----->//
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //<-----Listeners to trigger----->//
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //<-----Variables for timerCode----->//
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {

        //<-----Actions every tick----->//
        @Override
        public void run() {
            //<-----Run the 3 methods for gathering information----->//
            runOnUiThread(new Runnable() {
                public void run() {

                    gatherGPSdata();
                    gatherHeartRate();
                }
            });

        }
    };

    //<-----Start the timer----->//
    public void startTimer() {

        //<-----Redo the timer----->//
        if (timer != null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                //<-----Override the old timer with the new one----->//
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run()
                        {
                            gatherGPSdata();
                            gatherHeartRate();
                        }
                    });
                }
            };
            //return;
        }

        //<-----Create a new timer that tick every x(2000) milliseconds----->//

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 2000);
    }

    //<-----Stop the timer----->//
    public void stopTimer() {
        //<-----Cancel operations and purge all of the things in the remaining of the queue----->//
        timer.cancel();
        timer.purge();
        //timerTask.cancel();
    }

    public void gatherGPSdata() {
        //<-----Init the locationmanager----->//
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //<-----Runtime Operation to verify that the user does intent to use DANGEROUS fine location----->//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        //<-----Gather information into pack----->//
        packOfLocations.add(cordinations);
        System.out.println("Gathering GPS data...");

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    System.out.println("Error1002: Permissions not granted for the GPS");
                }
                return;
            }
        }
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
    public void gatherHeartRate(){
        //<-----Add the heartrate to the package----->//
        packOfHeartRate.add(String.valueOf(heart));

    }
    @Override
    public void onLocationChanged(Location location) {

        //<-----OnLocationChanged, Save the cordinations in temp----->//
            cordinations = "X:" + location.getLatitude() + "Y:" + location.getLongitude();

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
        if(event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            heart = event.values[0];
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
    //<-----Loop though the batches and print them out----->//
    public void debugSendBatch(){
        //<-----PackOfLocations print----->//
        System.out.println("Printing out the batch for locations");
        for(int i = 0; i < packOfLocations.size(); i++){
            System.out.println(i + ": " + packOfLocations.get(i));
        }
        //<-----PackOfLocations print----->//
        System.out.println("Printing out the batch for stepcounter");
        for(int i = 0; i < packOfStepCounter.size(); i++){
            System.out.println(i + ": " + packOfStepCounter.get(i));
        }
        System.out.println("Printing out the batch for heartrate");
        for(int i = 0; i < packOfHeartRate.size(); i++){
            System.out.println(i + ": " + packOfHeartRate.get(i));
        }

        //<-----add meta data to the packages----->//
        configureMetaData();

        //<-----clearears----->//
        packOfLocations.clear();
        packOfStepCounter.clear();
        packOfHeartRate.clear();

        //<-----Send the batches----->//
        sendPackagesToCloud();
    }

    public void configureMetaData(){
        //<-----Init values----->//
        String date;
        String userName;
        String GPS;
        String step;
        String heart;
        //<-----Declare values----->//
        date = getDateAndTime();
        userName = "Swashy";
        GPS = "G";
        step = "S";
        heart = "H";
        //<-----Assign values----->//


        //<-----Date is third last value in table----->//
        packOfLocations.add(date);
        //<-----Username is second last value in table----->//
        packOfLocations.add(userName);
        //<-----Type of sensor is last value in table----->//
        packOfLocations.add(GPS);

        packOfStepCounter.add(date);
        packOfStepCounter.add(userName);
        packOfStepCounter.add(step);

        packOfHeartRate.add(date);
        packOfHeartRate.add(userName);
        packOfHeartRate.add(heart);
    }
    public String getDateAndTime(){
        //<-----Get the current time on the device----->//
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        //<-----Return date as string----->//
        return formattedDate;

    }
    public void sendPackagesToCloud(){


    }
}
