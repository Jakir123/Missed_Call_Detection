package com.cse24gmail.jakir.missed_call_detection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
    static boolean ring = false;
    static boolean callReceived = false;
    private String provider;
    static double longitude = 0;
    static double latitude = 0;
    TelephonyManager telephonyManager;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getCurrentLocation();

        telephonyManager.listen(new TeleListener(), PhoneStateListener.LISTEN_CALL_STATE);
        //---get the phone number---
        String telNumber = telephonyManager.getLine1Number();
        if (telNumber != null)
            Toast.makeText(this, "Phone number: " + telNumber,
                    Toast.LENGTH_LONG).show();
        //---get the SIM card ID---
        String simID = telephonyManager.getSimSerialNumber();
        if (simID != null)
            Toast.makeText(this, "SIM card ID: " + simID,
                    Toast.LENGTH_LONG).show();
    }

    private void getCurrentLocation() {
        boolean isGPSEnable= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnable)
        {
            Toast.makeText(getApplicationContext(),"First You have to enable GPS!!",Toast.LENGTH_SHORT).show();
            Intent intent =new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled",true);
            sendBroadcast(intent);
        }else {
            Toast.makeText(getApplicationContext(),"GPS Enable",Toast.LENGTH_SHORT).show();
        }
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null)
        {
            Toast.makeText(getApplicationContext(), "Location Found", Toast.LENGTH_LONG).show();

            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        else if(location==null){
            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null){
                longitude=location.getLongitude();
                latitude=location.getLatitude();
            }else{
                Toast.makeText(getApplicationContext(),"Location Not Found using network provider!",Toast.LENGTH_SHORT).show();            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Location Not Found !",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    class TeleListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {

                case TelephonyManager.CALL_STATE_IDLE:

                    if (ring == true && callReceived == false) {
                        String callerNumber=incomingNumber;
                        String IMEI=String.valueOf(telephonyManager.getDeviceId());

                        // create custom alert dialog
                        LayoutInflater li = LayoutInflater.from(getBaseContext());
                        View editDialogView = li.inflate(R.layout.dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setView(editDialogView);

                        final EditText etLatitude = (EditText) editDialogView.findViewById(R.id.etLatitude);
                        final EditText etLongitude = (EditText) editDialogView.findViewById(R.id.etLongitude);
                        final EditText etIncomingNumber = (EditText) editDialogView.findViewById(R.id.etIncomingNumber);
                        final EditText etIMEINumber = (EditText) editDialogView.findViewById(R.id.etIMEINumber);

                        etLatitude.setText("" + latitude);
                        etLongitude.setText("" + longitude);
                        etIMEINumber.setText(IMEI);
                        etIncomingNumber.setText(callerNumber);

                        alertDialogBuilder.setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                // send data to server........

                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        // end of custom dialog.........
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    callReceived = true;
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    ring = true;


                    break;

                default:
                    break;
            }
        }

    }


}