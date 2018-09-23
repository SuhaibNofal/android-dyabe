package com.app.nevada.nevadaapp;


import android.app.Service;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class GPSService extends Service {
    String VarOrderNo ="";
    double LongData =0;
    double LatData = 0;
    final Handler ha = new Handler();
    Runnable VarRunnable ;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        VarOrderNo = intent.getStringExtra("VarOrderNo");
        ha.postDelayed(VarRunnable = new Runnable() {
            @Override
            public void run() {

                try{
                    LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    LocationListener currentLocationListener = new MyLocationListener();
                    if (ActivityCompat.checkSelfPermission(GPSService.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPSService.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, currentLocationListener);
                    if ( !CurrentLocation.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        TurnGbsOnAlert();
                    }
                    GPSTracker gps = new GPSTracker(GPSService.this);
                    LatData = gps.getLatitude();
                    LongData = gps.getLongitude();
                    //Toast.makeText(GPSService.this, "IN", Toast.LENGTH_SHORT).show();

                    new UpdateLocationTask().execute();
                    ha.postDelayed(this, 10000);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error:"+ex,Toast.LENGTH_LONG).show();
                }

            }
        }, 10000);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
    public void TurnGbsOnAlert() {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
    class UpdateLocationTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "UpdateLocation";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateLocation";


                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("VarLong", String.valueOf(LongData));
                request.addProperty("VarLat", String.valueOf(LatData));
                request.addProperty("VarOrderNo", VarOrderNo);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);

            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if(location != null)
            {
                double PLong = location.getLongitude();
                double PLat = location.getLatitude();
                LongData = PLong;
                LatData = PLat;
            }
        }


        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // TODO Auto-generated method stub

        }

    }

    public void onDestroy(){
        //Toast.makeText(GPSService.this, "onDestroy", Toast.LENGTH_SHORT).show();
        ha.removeCallbacks(VarRunnable);
        stopSelf();
    }

}