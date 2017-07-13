package com.example.elisa.urop3;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MyService extends Service {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private int time = 0;
    public int duration = 0;
    public float max_acc = 0;

    @Override
    public void onCreate(){

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(new SensorEventListener() {

            private long lastUpdate = 0;
            private float last_x, last_y, last_z, last_mag_acceleration;
            // sets the threshold of how sensitive you want the app to be to movement
            private static final int SHAKE_THRESHOLD = 600;

            @Override
            public void onSensorChanged(SensorEvent event) {
                String file_name = "hello_file";
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                    Log.d(TAG, "Inside onSensorChanged");
                    // to take in the three co-ordinates of the position of the phone
                    // x = horizontal movement of the phone
                    // y = vertical movement of the phone
                    // z = forward/backwards movement of the phone
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    float acceleration = (float) Math.sqrt((x*x)+(y*y)+(z*z));

                    // constantly moving so to ensure it's not reading all the time set it to only
                    // take in another reading if 100ms have gone by
                    long curTime = System.currentTimeMillis();

                    if ((curTime - lastUpdate) > 100) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;

                        float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                        if (speed > SHAKE_THRESHOLD) {
                            /*long curTime2 = System.currentTimeMillis();

                            if((curTime2 - lastUpdate) > 10) {
                                long diffTime2 = (curTime2 - lastUpdate);
                                lastUpdate = curTime2;
                                time++;

                                String acc = String.valueOf(acceleration);
                                Log.d(TAG, acc);
                                last_x = x;
                                last_y = y;
                                last_z = z;
                                last_mag_acceleration = acceleration;

                                //Assigns the new acceleration as maximum acceleration if the new one is higher
                                max_acc = Math.max(last_mag_acceleration, max_acc);
                            }*/

                            String acc = String.valueOf(acceleration);
                            Log.d(TAG, acc);
                            Toast.makeText(getApplicationContext(), acc, Toast.LENGTH_LONG).show();
                            last_x = x;
                            last_y = y;
                            last_z = z;
                            last_mag_acceleration = acceleration;

                            //Assigns the new acceleration as maximum acceleration if the new one is higher
                            max_acc = Math.max(last_mag_acceleration, max_acc);
                            time++;
                            lastUpdate = curTime;
                            duration = time*100;
                            savingData(duration, max_acc);
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startiD){
        Toast.makeText(this, "MyService started ", Toast.LENGTH_LONG).show();

        /*If you return START_STICKY the service gets recreated whenever the
        resources are available. If you return START_NOT_STICKY you have to
        re-activate the service sending a new intent so I have chosen to
        return START_STICKY here so constantly running in the background*/

        return START_STICKY;
    }

    /*private long lastUpdate = 0;
    private float last_x, last_y, last_z, last_mag_acceleration;
    // sets the threshold of how sensitive you want the app to be to movement
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    public void onSensorChanged(SensorEvent event) {

        String file_name = "hello_file";
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            Log.d(TAG, "Inside onSensorChanged");
            // to take in the three co-ordinates of the position of the phone
            // x = horizontal movement of the phone
            // y = vertical movement of the phone
            // z = forward/backwards movement of the phone
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float acceleration = (float) Math.sqrt((x*x)+(y*y)+(z*z));


            // constantly moving so to ensure it's not reading all the time set it to only
            // take in another reading if 100ms have gone by
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                    if ((curTime - lastUpdate) > 10) {
                        time++;
                        Toast.makeText(this, "Above threshold", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Recorded speed above threshold");
                        last_x = x;
                        last_y = y;
                        last_z = z;
                        last_mag_acceleration = acceleration;

                        //Assigns the new acceleration as maximum acceleration if the new one is higher
                        max_acc = Math.max(last_mag_acceleration, max_acc);
                        lastUpdate = curTime;
                        duration = time*10;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }*/

    public void savingData(int duration, float max_acceleration){

    }



    @Override
    public void onDestroy(){
        Toast.makeText(this, "MyService destroyed ", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");#
        return null;
    }

}
