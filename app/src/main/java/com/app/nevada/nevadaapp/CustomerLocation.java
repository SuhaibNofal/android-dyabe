package com.app.nevada.nevadaapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class CustomerLocation extends FragmentActivity implements OnMapReadyCallback {
    double LongData = 0;
    double LatData = 0;
    String VarAccountNo = "";
    private GoogleMap mMap;
    MarkerOptions marker =  new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_location);
        Intent intent = getIntent();
        VarAccountNo= intent.getStringExtra("VarAccountNo");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    public  void UpdateLocation(View view){
        new UpdateLocationTask().execute();

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            String TestLocation = "";
            Criteria cr = new Criteria();
            LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener currentLocationListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(CustomerLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CustomerLocation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, currentLocationListener);
            if ( !CurrentLocation.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                TurnGbsOnAlert();
            }

            GPSTracker gps = new GPSTracker(this);
            LatData = gps.getLatitude();
            LongData = gps.getLongitude();




            LatLng loc = new LatLng(LatData,LongData);
            mMap.addMarker(marker.position(loc).title("الموقع الحالي"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo( 16.0f ) );



        }catch (Exception ex){

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
    public void TurnGbsOnAlert() {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
    class UpdateLocationTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "UpdateCustomerLocation";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateCustomerLocation";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                double ttt = marker.getPosition().longitude;
                double tttt = marker.getPosition().latitude;
                String ttttt= VarAccountNo;
                request.addProperty("VarLong", Double.toString(marker.getPosition().longitude));
                request.addProperty("VarLat", Double.toString(marker.getPosition().latitude));
                request.addProperty("VarAccountNo", VarAccountNo);

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
            Toast.makeText(CustomerLocation.this,"تم تحديث الموقع", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}
