package com.example.elisa.urop3;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import android.telephony.SmsManager;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    Button btnShowLocation;
    Button sendlocation;
    private TextView coordinates;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private LocationManager locationManager;
    private LocationListener listener;

    String longitude;
    String latitude;

    EditText txtphoneNo;
    String phoneNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        coordinates = (TextView) findViewById(R.id.GPSlocation);
        btnShowLocation = (Button) findViewById(R.id.button3);

        txtphoneNo = (EditText) findViewById(R.id.phone1);
        btnShowLocation = (Button) findViewById(R.id.button4);
        sendlocation = (Button) findViewById(R.id.button3);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = Double.toString(location.getLongitude());
                latitude = Double.toString(location.getLatitude());
                coordinates.append("\n " + location.getLongitude() + " " + location.getLatitude());
                //Toast.makeText(this, longitude , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();

        sendlocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                Toast.makeText(getApplicationContext(), "Sending SMS!.", Toast.LENGTH_LONG).show();
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, "My location is: longitude = " + longitude + " latitude = " + latitude, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "SMS failed, No permissions, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            }
        });
    }

    protected void sendSMSMessage() {
        phoneNo = txtphoneNo.getText().toString();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else {

            try {
                Toast.makeText(getApplicationContext(), "Sending SMS!.", Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, "My location is: longitude = " + longitude + " latitude = " + latitude, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();
            }
            catch ( Exception e){
                Toast.makeText(getApplicationContext(), "SMS Failed!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void onClickStartService(View view){
        Intent startingIntent = new Intent(this, MyService.class);
        startService(startingIntent);
    }

    public void onClickStopService(View view){
        Intent stoppingIntent = new Intent(this, MyService.class);
        stopService(stoppingIntent);
    }

}
