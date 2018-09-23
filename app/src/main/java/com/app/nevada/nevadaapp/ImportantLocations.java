package com.app.nevada.nevadaapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ImportantLocations extends FragmentActivity implements OnMapReadyCallback {
    private static final String METHOD_NAME = "GetImportantLocations";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/GetImportantLocations";
    private ArrayList<HashMap<String, String>> list;
    SoapObject responseObject;
    String VarCurrentLocationInfo = "";
    String VarCurrentLocationName = "";
    private GoogleMap mMap;
    double CurrLongData =0;
    double CurrLatData = 0;
    double DestLongData =0;
    double DestLatData = 0;
    int VarTrafficVisable = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad  = "EN-US";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        setContentView(R.layout.activity_important_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new GetAllLocationsTask().execute();
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
        mMap = googleMap;
        LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener currentLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(ImportantLocations.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ImportantLocations.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, currentLocationListener);
        if ( !CurrentLocation.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            TurnGbsOnAlert();
        }
        GPSTracker gps = new GPSTracker(ImportantLocations.this);
        CurrLongData = gps.getLatitude();
        CurrLatData = gps.getLongitude();
        LatLng loc = new LatLng(CurrLongData, CurrLatData);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 16.0f ) );
        createMarker(CurrLongData,CurrLatData, "الموقع الحالي", "",1);

    }
    public void ShowCurrentLocationInfo(View v){
        String[] separated = VarCurrentLocationInfo.split(",&,");
        if (separated.length > 1){
            LatLng origin = new LatLng(CurrLongData, CurrLatData);
            LatLng dest = new LatLng(Double.parseDouble(separated[1]), Double.parseDouble(separated[2]));
            String url = getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }else{
            Toast.makeText(ImportantLocations.this,"الرجاء اختيار موقع لعرض المعلومات", Toast.LENGTH_SHORT).show();
        }

    }
    public void RefreshLocation(View v)
    {
        mMap.clear();
        LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener currentLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(ImportantLocations.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ImportantLocations.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, currentLocationListener);
        if ( !CurrentLocation.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            TurnGbsOnAlert();
        }
        GPSTracker gps = new GPSTracker(ImportantLocations.this);
        CurrLongData = gps.getLatitude();
        CurrLatData = gps.getLongitude();
        LatLng loc = new LatLng(CurrLongData, CurrLatData);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 16.0f ) );
        String[] separated = VarCurrentLocationInfo.split(",&,");
        if (separated.length > 1){
            LatLng origin = new LatLng(CurrLongData, CurrLatData);
            LatLng dest = new LatLng(Double.parseDouble(separated[1]), Double.parseDouble(separated[2]));
            String url = getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }else{
            Toast.makeText(ImportantLocations.this,"الرجاء اختيار موقع لعرض المعلومات", Toast.LENGTH_SHORT).show();
        }

    }
    public void SetSatView(View v)
    {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }
    public void SetStreetView(View v)
    {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    public void SetTrafficView(View v)
    {
        if (VarTrafficVisable == 0)
        {
            mMap.setTrafficEnabled(true);
            VarTrafficVisable = 1;
        }else{
            mMap.setTrafficEnabled(false);
            VarTrafficVisable = 0;
        }
    }
    class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if(location != null)
            {
                double PLong = location.getLongitude();
                double PLat = location.getLatitude();
                CurrLongData = PLong;
                CurrLatData = PLat;
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
    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int isDest) {
        if (isDest == 2){
            Bitmap b= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.impflag);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            return mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }else{
            Bitmap b= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.fuel);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            return mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
    }
    class GetAllLocationsTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                responseObject = (SoapObject) envelope.getResponse();
                Integer x = 1;
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
            Spinner ddlAllLocations = (Spinner)findViewById(R.id.spImpLoc);
            List<StringWithTag> AllLocations = new ArrayList<StringWithTag>();
            AllLocations.add(new StringWithTag("اختر الموقع", "0"));
            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                AllLocations.add(new StringWithTag(reponseArrayString[1].toString(),reponseArrayString[2].toString().concat(",&,").concat(reponseArrayString[3].toString()).concat(",&,").concat(reponseArrayString[4].toString())));
            }
            ArrayAdapter<StringWithTag> spLocationAdapter = new ArrayAdapter<StringWithTag>(ImportantLocations.this, android.R.layout.simple_spinner_dropdown_item, AllLocations);
            ddlAllLocations.setAdapter(spLocationAdapter);
            ddlAllLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    StringWithTag sel = (StringWithTag) parentView.getItemAtPosition(position);
                    VarCurrentLocationInfo = sel.tag.toString();
                    VarCurrentLocationName= sel.string.toString();
                    if(position >0){
                        String[] separated = VarCurrentLocationInfo.split(",&,");
                        TextView txt= (TextView) findViewById(R.id.txtDesc);
                        txt.setText(separated[0].toString());
                    }else{
                        TextView txt= (TextView) findViewById(R.id.txtDesc);
                        txt.setText("-");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){


        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }



    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            java.net.URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){

        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(20);
                lineOptions.color(Color.parseColor("#3F51B5"));
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions !=null){
                mMap.clear();
                mMap.addPolyline(lineOptions);

                createMarker(CurrLongData,CurrLatData, "الموقع الحالي", "",1);
                createMarker(DestLongData, DestLatData, "الموقع", VarCurrentLocationName,2);
                Button btnRefreshLocation= (Button) findViewById(R.id.btnRefreshLocation);

            }
        }
    }

}
