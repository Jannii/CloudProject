package com.example.gul_h.letsrun;

import android.Manifest;
import android.content.ActivityNotFoundException;
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
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.table.*;
import com.microsoft.azure.storage.table.TableQuery.*;

import org.w3c.dom.Text;

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
    private ArrayList<String> packOfNotes = new ArrayList();
    //<-----Temporary variables, used for EG. Sorting out unused data----->//
    private String cordinations = "0,0";
    private float steps = 0;
    private float heart = 0;

    //<-----SensorManager, handles all of the sensors----->//
    private SensorManager mSensorManager;
    //<-----All the underlying sensors----->//
    private Sensor mStepCounter;
    private Sensor mHeartRate;
    //<-----Components in view----->//
    private TextView theLabelText = null;
    private TextView theWhatYouSaid = null;
    private ImageButton theRecord = null;
    private Button theConfirmAndSend = null;
    private Button theConfirmToCloud = null;
    //<-----CheckBoxes----->//
    private CheckBox gpsCheckBox;
    private CheckBox stepCheckBox;
    private CheckBox heartChecBox;
    //<-----CheckBoxesValues----->//
    private boolean gpsEnabled = true;
    private boolean stepEnabled = true;
    private boolean heartEnabled = true;
    //<-----Email----->//
    private String userEmail;


    //<-----Audio Code----->//
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //<-----Get the user email----->//
        getTheIntent();

        //<-----Find components in view----->//
        final Button startAndStop = (Button) findViewById(R.id.start);

        final TextView labelText = (TextView) findViewById(R.id.LabelForWhatToDo);
        final TextView whatYouSaid = (TextView) findViewById(R.id.whatYouSaid);
        final ImageButton record = (ImageButton) findViewById(R.id.btnSpeak);
        final Button confirmAndSend = (Button) findViewById(R.id.confirmSend);
        final Button confirmToCloud = (Button) findViewById(R.id.ConfirmToCloud);

        final CheckBox checkBoxForGPS = (CheckBox) findViewById(R.id.checkBoxGPS);
        final CheckBox checkBoxForStep = (CheckBox) findViewById(R.id.checkBoxStep);
        final CheckBox checkBoxForHeart = (CheckBox) findViewById(R.id.checkBoxHeart);


        //<-----Create Global variables----->//
        theLabelText = labelText;
        theWhatYouSaid = whatYouSaid;
        theRecord = record;
        theConfirmAndSend = confirmAndSend;
        theConfirmToCloud = confirmToCloud;

        gpsCheckBox = checkBoxForGPS;
        stepCheckBox = checkBoxForStep;
        heartChecBox = checkBoxForHeart;

        //<-----Find all the sensors on the phone and print them----->//
        checkAllSensorsOnPhone();

        //<-----Disable checkBoxes if sensors does not exist----->//
        checkIfSensorExists();

        //<-----CheckBoxes Clicked----->//
        checkBoxForGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    gpsEnabled = true;
                    System.out.println("Enabled GPS");
                    createShortToastInvoke("Enabled GPS");

                }
                else{
                    gpsEnabled = false;
                    System.out.println("Disabled GPS");
                    createShortToastInvoke("Disabled GPS");
                }
            }
        });
        checkBoxForStep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    stepEnabled = true;
                    System.out.println("Enabled StepCounter");
                    createShortToastInvoke("Enabled StepCounter");
                }
                else{
                    stepEnabled = false;
                    System.out.println("Disabled StepCounter");
                    createShortToastInvoke("Disabled StepCounter");
                }
            }
        });
        checkBoxForHeart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    heartEnabled = true;
                    System.out.println("Enabled HeartRate");
                    createShortToastInvoke("Enabled HeartRate");
                }
                else{
                    heartEnabled = false;
                    System.out.println("Disabled HeartRate");
                    createShortToastInvoke("Disabled HeartRate");
                }
            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        confirmAndSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addMetaDataToTable();
            }
        });
        confirmToCloud.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                debugSendBatch();
                hideComponents();

            }
        });

        //<-----Method for performing start and stop operations----->//
        startAndStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //<-----Click on button action----->//

                //<-----Change text on button and call tickListener----->//
                if (startAndStopBool == false) {

                    System.out.println("Starting gathering");
                    startAndStop.setText("Stop Running");
                    displayButtonsAndText();
                    startTimer();
                    gatherStepCounter();

                    startAndStopBool = true;

                } else {
                    System.out.println("Stopping gathering");
                    startAndStop.setText("Start Running");
                    displayButtonsAndText();
                    stopTimer();
                    gatherStepCounter();
                    //debugSendBatch();

                    startAndStopBool = false;
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
                        public void run() {
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    public void gatherStepCounter() {
        //<-----If reset, reset the list aswell so dont send already sent value----->//
        if (packOfStepCounter.size() >= 2) {
            packOfStepCounter.clear();
        }

        //<-----Gather start and stop values, and the value between is walken steps----->//
        if (startAndStopBool == false) {
            packOfStepCounter.add(String.valueOf(steps));

        } else {
            packOfStepCounter.add(String.valueOf(steps));
        }

    }

    public void gatherHeartRate() {
        //<-----Add the heartrate to the package----->//
        packOfHeartRate.add(String.valueOf(heart));

    }

    @Override
    public void onLocationChanged(Location location) {

        //<-----OnLocationChanged, Save the cordinations in temp----->//
        cordinations = location.getLatitude() + "," + location.getLongitude();

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
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            steps = event.values[0];
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            heart = event.values[0];

            //<-----Debug heartRate----->//
            System.out.println("Heartrate: " + heart);
        }

    }

    //<-----When the accuracy of a sensor changes----->//
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void checkAllSensorsOnPhone() {

        //<-----Check for all the sensors on the phone----->//
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);


        //<-----Loop though the list and print them out----->//
        for (int i = 0; i < deviceSensors.size(); i++) {
            System.out.println("Sensor" + i + ": " + deviceSensors.get(i).toString());
        }

    }

    //<-----Loop though the batches and print them out----->//
    public void debugSendBatch() {
        //<-----PackOfLocations print----->//
        System.out.println("Printing out the batch for locations");
        for (int i = 0; i < packOfLocations.size(); i++) {
            System.out.println(i + ": " + packOfLocations.get(i));
        }
        //<-----PackOfLocations print----->//
        System.out.println("Printing out the batch for stepcounter");
        for (int i = 0; i < packOfStepCounter.size(); i++) {
            System.out.println(i + ": " + packOfStepCounter.get(i));
        }
        System.out.println("Printing out the batch for heartrate");
        for (int i = 0; i < packOfHeartRate.size(); i++) {
            System.out.println(i + ": " + packOfHeartRate.get(i));
        }

        //<-----add meta data to the packages----->//
        configureMetaData();


        //<-----Send the batches----->//
        sendPackagesToCloud();
    }

    public void configureMetaData() {
        //<-----Init values----->//
        String date;
        String userName;
        String GPS;
        String step;
        String heart;
        String note;
        //<-----Declare values----->//
        date = getDateAndTime();
        userName = userEmail;
        GPS = "G";
        step = "S";
        heart = "H";
        note = "N";

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

        packOfNotes.add(date);
        packOfNotes.add(userName);
        packOfNotes.add(note);
    }

    public String getDateAndTime() {
        //<-----Get the current time on the device----->//
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        //<-----Return date as string----->//
        return formattedDate;

    }
    public String getDay() {
        //<-----Get the current time on the device----->//
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        //<-----Return date as string----->//
        return formattedDate;

    }

    public void sendPackagesToCloud() {
    //<-----Threading for network use----->//
        Thread th = new Thread(new Runnable() {
            public void run() {

                //<-----Get and set up connectionstring----->//
                String storageConnectionString = setUpCloud();
                //<-----send Batch(4mb max)----->//
                sendTableBatch(storageConnectionString);
            }});
        th.start();
    }

    public String setUpCloud() {
        //<-----Connection String----->//
      String storageConnectionString = "DefaultEndpointsProtocol=https;"
                + "AccountName=iuliunicolaestorage;"
                + "AccountKey=L4YdTWd3pBvTGAFtBzCL3B/rALAn06nEI9iPSKgOLWabrFF/xFZFPy32bVXUzmy8z8slqkj/k4tpPLVjtJzBmA==";

        try {

            //<-----Setting up GPS Table----->//
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudTableClient tableClient = storageAccount.createCloudTableClient();
            CloudTable cloudTable = tableClient.getTableReference("dettagps");
            cloudTable.createIfNotExists();

            //<-----Setting up StepCounter Table----->//
            CloudStorageAccount storageAccount2 = CloudStorageAccount.parse(storageConnectionString);
            CloudTableClient tableClient2 = storageAccount2.createCloudTableClient();
            CloudTable cloudTable2 = tableClient2.getTableReference("dettastep");
            cloudTable2.createIfNotExists();

            //<-----Setting up HeartRate Table----->//
            CloudStorageAccount storageAccount3 = CloudStorageAccount.parse(storageConnectionString);
            CloudTableClient tableClient3 = storageAccount3.createCloudTableClient();
            CloudTable cloudTable3 = tableClient3.getTableReference("dettaheart");
            cloudTable3.createIfNotExists();

            //<-----Setting up Notes Table----->//
            CloudStorageAccount storageAccount4 = CloudStorageAccount.parse(storageConnectionString);
            CloudTableClient tableClient4 = storageAccount4.createCloudTableClient();
            CloudTable cloudTable4 = tableClient4.getTableReference("dettanote");
            cloudTable4.createIfNotExists();

            //<-----Finished----->//

            System.out.println("Setted up the cloud");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //<-----Return the string----->//
        return storageConnectionString;
    }

    public void sendTableBatch(String connectionString){
        try
        {
            //<-----Generate a random key----->//
            UUID uniqueKey;

            //<--------------------------------------------GPS---------------------------------------->//
            //<-----Get reference to GPS table storage----->//
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
            CloudTableClient tableClient = storageAccount.createCloudTableClient();
            TableBatchOperation batchOperation = new TableBatchOperation();
            CloudTable cloudTable = tableClient.getTableReference("dettagps");

            //<-----Loop through the packs and add it to a table batch----->//
            if(gpsEnabled == true) {
                for (int i = 0; i < packOfLocations.size() - 3; i++) {
                    //<-----Split locations into X and Y----->//
                    uniqueKey = UUID.randomUUID();
                    String splittedArray = packOfLocations.get(i);
                    String[] vals = new String[2];
                    if (!splittedArray.equals("0")) {
                        vals = splittedArray.split(",");
                    }

                    GPSEntity GPSPack = new GPSEntity(userEmail + getDay(), uniqueKey.toString());
                    if (vals.length == 2) {
                        GPSPack.setLongitude(Double.parseDouble(vals[0]));
                        GPSPack.setLatitude(Double.parseDouble(vals[1]));
                    } else {
                        GPSPack.setLongitude(Double.parseDouble(vals[0]));
                        GPSPack.setLatitude(Double.parseDouble(vals[0]));
                    }
                    GPSPack.setUserName(packOfLocations.get(packOfLocations.size() - 2));
                    GPSPack.setType(packOfLocations.get(packOfLocations.size() - 1));
                    batchOperation.insert(GPSPack);
                    //<-----Insert into the batch(list)----->//
                }
            }else{

                System.out.println("Error3000: GPS Disabled");

            }

            //<--------------------------------------------StepCounter---------------------------------------->//
            CloudStorageAccount storageAccount2 = CloudStorageAccount.parse(connectionString);
            CloudTableClient tableClient2 = storageAccount2.createCloudTableClient();
            TableBatchOperation batchOperation2 = new TableBatchOperation();
            CloudTable cloudTable2 = tableClient2.getTableReference("dettastep");

            //<-----Loop through the packs and add it to a table batch----->//
            if(stepEnabled == true) {
                for (int i = 0; i < packOfStepCounter.size() - 3; i++) {
                    uniqueKey = UUID.randomUUID();

                    StepCounterEntity stepPack = new StepCounterEntity(userEmail + getDay(), uniqueKey.toString());
                    stepPack.setSteps(Double.parseDouble(packOfStepCounter.get(i)));

                    stepPack.setDate(getDay());
                    stepPack.setUserName(packOfLocations.get(packOfLocations.size() - 2));
                    stepPack.setType(packOfLocations.get(packOfLocations.size() - 1));
                    batchOperation2.insert(stepPack);
                    //<-----Insert into the batch(list)----->//
                }
            }else{

                System.out.println("Error3001: Step Counter disabled");

            }

            //<--------------------------------------------HeartRate---------------------------------------->//
            CloudStorageAccount storageAccount3 = CloudStorageAccount.parse(connectionString);
            CloudTableClient tableClient3 = storageAccount3.createCloudTableClient();
            TableBatchOperation batchOperation3 = new TableBatchOperation();
            CloudTable cloudTable3 = tableClient3.getTableReference("dettaheart");

            //<-----Loop through the packs and add it to a table batch----->//
            if(heartEnabled == true) {
                for (int i = 0; i < packOfHeartRate.size() - 3; i++) {
                    uniqueKey = UUID.randomUUID();

                    HeartRateEntity heartPack = new HeartRateEntity(userEmail + getDay(), uniqueKey.toString());
                    heartPack.setHeartRate(Double.parseDouble(packOfHeartRate.get(i)));

                    heartPack.setDate(getDay());
                    heartPack.setUserName(packOfLocations.get(packOfLocations.size() - 2));
                    heartPack.setType(packOfLocations.get(packOfLocations.size() - 1));
                    batchOperation3.insert(heartPack);
                    //<-----Insert into the batch(list)----->//
                }
            }else{

                System.out.println("Error3002: Heart Rate Disabled");

            }

            //<--------------------------------------------Notes---------------------------------------->//
            CloudStorageAccount storageAccount4 = CloudStorageAccount.parse(connectionString);
            CloudTableClient tableClient4 = storageAccount4.createCloudTableClient();
            TableBatchOperation batchOperation4 = new TableBatchOperation();
            CloudTable cloudTable4 = tableClient4.getTableReference("dettanote");

            //<-----Loop through the packs and add it to a table batch----->//

            for(int i = 0; i < packOfNotes.size()-3; i++) {
                uniqueKey = UUID.randomUUID();

                NotesEntity notePack = new NotesEntity(userEmail + getDay(), uniqueKey.toString());
                notePack.setTheNote(packOfNotes.get(i));

                notePack.setDate(getDay());
                notePack.setUserName(packOfLocations.get(packOfLocations.size()-2));
                notePack.setType(packOfLocations.get(packOfLocations.size()-1));
                batchOperation4.insert(notePack);
                //<-----Insert into the batch(list)----->//
            }


            //<-----Execute batch----->//
            if(!batchOperation.isEmpty()) {
                cloudTable.execute(batchOperation);
            }
            else{

                System.out.println("Error2000: Locations Batch is empty");

            }
            if(!batchOperation2.isEmpty()) {
                cloudTable2.execute(batchOperation2);
            }
            else{

                System.out.println("Error2001: Step Batch is empty");

            }
            if(!batchOperation3.isEmpty()) {
                cloudTable3.execute(batchOperation3);
            }
            else{

                System.out.println("Error2002: Heart Batch is empty");

            }
            if(!batchOperation4.isEmpty()) {
                cloudTable4.execute(batchOperation4);
            }
            else{

                System.out.println("Error2003: Notes Batch is empty");

            }

            //<-----clearears----->//
            packOfLocations.clear();
            packOfStepCounter.clear();
            packOfHeartRate.clear();
            packOfNotes.clear();


            //<-----DEBUG, Get Table----->//
            //debugGetBatchFromCloud(connectionString);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void debugGetBatchFromCloud(String connectionString){
        try
        {
            // Define constants for filters.
            final String PARTITION_KEY = "Mattias" + getDay();

            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);

            // Create the table client.
            CloudTableClient tableClient = storageAccount.createCloudTableClient();

            // Create a cloud table object for the table.
            CloudTable cloudTable = tableClient.getTableReference("dettagps");

            // Create a filter condition where the partition key is "Smith".
            String partitionFilter = TableQuery.generateFilterCondition(PARTITION_KEY, QueryComparisons.EQUAL, "Mattias" + getDay());

            // Specify a partition query, using "Smith" as the partition key filter.
            TableQuery<GPSEntity> partitionQuery = TableQuery.from(GPSEntity.class).where(partitionFilter);

            // Loop through the results, displaying information about the entity.
            for (GPSEntity entity : cloudTable.execute(partitionQuery)) {

                System.out.println(entity.getPartitionKey() +
                        " " + entity.getRowKey() +
                        "\t" + entity.getLongitude() +
                        "\t" + entity.getLatitude());
            }
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }

    }

    public void displayButtonsAndText(){

        if(startAndStopBool == false){
        //<-----Start----->//

            //<-----DEBUG----->//
            System.out.println("Hide New components");

            theLabelText.setVisibility(View.INVISIBLE);
            theWhatYouSaid.setVisibility(View.VISIBLE);
            theRecord.setVisibility(View.VISIBLE);
            theConfirmAndSend.setVisibility(View.VISIBLE);
            theConfirmToCloud.setVisibility(View.INVISIBLE);

        }
        else{
        //<-----Stop----->//

            //<-----DEBUG----->//
            System.out.println("Display New components");

            theLabelText.setVisibility(View.VISIBLE);
            theWhatYouSaid.setVisibility(View.VISIBLE);
            theRecord.setVisibility(View.VISIBLE);
            theConfirmAndSend.setVisibility(View.VISIBLE);
            theConfirmToCloud.setVisibility(View.VISIBLE);
            //<-----DEBUG----->//
            System.out.println("Visibility is: " + theConfirmAndSend.getVisibility());

        }
    }
    private void promptSpeechInput() {
        //<-----Prompt the speech----->//
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            //<-----Toast this----->//
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    theWhatYouSaid.setText(result.get(0));
                }
                break;
            }

        }
    }
    public void addMetaDataToTable(){
        if(!theWhatYouSaid.getText().equals("Speech to text:")) {
            //<-----Add note to pack----->//
            packOfNotes.add(theWhatYouSaid.getText().toString());
            //<-----DEBUG----->//
            System.out.println("Added a note to packOfNotes: " + packOfNotes.get(0) + "Size: " + packOfNotes.size());
            //<-----Toast this for response----->//
            Toast.makeText(this, "Added a note", Toast.LENGTH_LONG).show();
        }
        else{

            Toast.makeText(this, "Please say something", Toast.LENGTH_LONG).show();
        }

    }
    public void hideComponents(){

        //<-----Manually hide all the components----->//

        //<-----DEBUG----->//
        System.out.println("Hide New components");

        theLabelText.setVisibility(View.INVISIBLE);
        theWhatYouSaid.setVisibility(View.INVISIBLE);
        theRecord.setVisibility(View.INVISIBLE);
        theConfirmAndSend.setVisibility(View.INVISIBLE);
        theConfirmToCloud.setVisibility(View.INVISIBLE);


        //<-----Toast this for response----->//
        Toast.makeText(this, "Information uploaded to cloud", Toast.LENGTH_LONG).show();
    }

    public void checkIfSensorExists(){

        PackageManager PM= this.getPackageManager();
        boolean gps = PM.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        boolean step = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        boolean heart = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE);

        if(gps == false){
            gpsCheckBox.setEnabled(false);
            gpsEnabled = false;
        }
        else{

            gpsCheckBox.setEnabled(true);
            gpsCheckBox.setChecked(true);
            gpsEnabled = true;

        }
        if(step == false){
            stepCheckBox.setEnabled(false);
            stepEnabled = false;
        }else{

            stepCheckBox.setEnabled(true);
            stepCheckBox.setChecked(true);
            stepEnabled = true;

        }
        if(heart == false){
            heartChecBox.setEnabled(false);
            heartEnabled = false;
        }else{

            heartChecBox.setEnabled(true);
            heartChecBox.setChecked(true);
            heartEnabled = true;

        }
    }

    public void createShortToastInvoke(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void getTheIntent(){
        //<-----Get intent from previous scene----->//
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("email");
        //<-----DEBUG----->//
        System.out.println("Email entered: " + userEmail);
    }
}

