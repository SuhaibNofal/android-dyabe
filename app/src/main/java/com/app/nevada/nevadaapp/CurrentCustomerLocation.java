package com.app.nevada.nevadaapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;

public class CurrentCustomerLocation extends FragmentActivity implements OnMapReadyCallback {
//54:06:74:05:4A:65:D3:EB:76:73:11:4D:9D:60:AF:DE:C8:E9:98:B4

    Context context;
    private GoogleMap mMap;
    SoapObject responseObject;
    String XYLocation;
    String VarOrderNo = "";
    String VarCustID = "";
    String VarCustName = "";
    String VarStatus = "";
    String VarIsDone = "";
    boolean VarIsOnRoad = false;
    ArrayList markerPoints = new ArrayList();
    double CurrLongData = 0;
    double CurrLatData = 0;
    double CustLongData = 0;
    double CustLatData = 0;
    int VarTrafficVisable = 0;
    String Vardelevery_not_done = "0";
    String language = "";

    public CurrentCustomerLocation() {
        this.context = CurrentCustomerLocation.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_customer_location);
        Intent intent = getIntent();
        XYLocation = intent.getStringExtra("location");

        language = intent.getStringExtra("lang");
        String[] separated = XYLocation.split(",");
        VarOrderNo = separated[3];
        VarIsOnRoad = Boolean.parseBoolean(separated[5]);
        VarCustID = separated[6];
        VarCustName = separated[2];
        if (VarIsOnRoad) {
            Button btn = (Button) findViewById(R.id.btnStartTrip);


            if (language.contains("en")) {
                btn.setText("    Access     ");
            } else {
                btn.setText("    وصول     ");
            }

            btn.setBackgroundColor(Color.RED);
        } else {
            Button btn = (Button) findViewById(R.id.btnStartTrip);
            Locale local = Locale.getDefault();
            String Languge = String.valueOf(local);
            if (Languge.contains("en")) {
                btn.setText("    start     ");
            } else {
                btn.setText("    ابدأ     ");
            }

            btn.setBackgroundColor(Color.parseColor("#3F51B5"));
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        chicpermision();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String[] separated = XYLocation.split(",");
        LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener currentLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(CurrentCustomerLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CurrentCustomerLocation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CurrentCustomerLocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            Log.d("mMap  ", "::Map ready");
        } else {

            //Toast.makeText(CurrentCustomerLocation.this,"2", Toast.LENGTH_SHORT).show();
            separated = XYLocation.split(",");
            currentLocationListener = new MyLocationListener();
            CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 2, currentLocationListener);
            if (!CurrentLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                TurnGbsOnAlert();
            }
            //Toast.makeText(CurrentCustomerLocation.this,"3", Toast.LENGTH_SHORT).show();
            GPSTracker gps = new GPSTracker(CurrentCustomerLocation.this);
            CurrLongData = gps.getLatitude();
            CurrLatData = gps.getLongitude();
            //Toast.makeText(CurrentCustomerLocation.this,"4", Toast.LENGTH_SHORT).show();
            LatLng loc = new LatLng(CurrLongData, CurrLatData);
            LatLng origin = new LatLng(CurrLongData, CurrLatData);
            LatLng dest = new LatLng(Double.parseDouble(separated[0]), Double.parseDouble(separated[1]));
            CustLongData = Double.parseDouble(separated[0]);
            CustLatData = Double.parseDouble(separated[1]);
            //Toast.makeText(CurrentCustomerLocation.this,"5", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
            VisibleRegion tt = mMap.getProjection().getVisibleRegion();
            String url = getDirectionsUrl(origin, dest);
            String VarAreaName = "";
            createMarker(CurrLongData, CurrLatData, "الموقع الحالي", "", 1);
            createMarker(CustLongData, CustLatData, "موقع العميل", VarCustName, 2);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
            //Toast.makeText(CurrentCustomerLocation.this,"6", Toast.LENGTH_SHORT).show();
            try {
                String languageToLoad = "ar"; // your language
                Locale locale = new Locale(language);

                Geocoder geo = new Geocoder(this.getApplicationContext(), locale);
                List<android.location.Address> addresses = geo.getFromLocation(CustLongData, CustLatData, 1);
                if (addresses.isEmpty()) {
                    VarAreaName = "Waiting for Location";
                } else {
                    if (addresses.size() > 0) {
                        VarAreaName = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                        TextView btn = (TextView) findViewById(R.id.txtCustomerLocation);
                        if (language.equals("en")) {
                            btn.setGravity(Gravity.LEFT);
                            btn.setText("Customer Location " + VarAreaName);
                        } else {
                            btn.setGravity(Gravity.RIGHT);
                            btn.setText("موقع العميل : " + VarAreaName);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Toast.makeText(CurrentCustomerLocation.this,"7", Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String[] separated = XYLocation.split(",");
                    LocationListener currentLocationListener = new MyLocationListener();
                    LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 2, currentLocationListener);
                    if (!CurrentLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        TurnGbsOnAlert();
                    }

                    //Toast.makeText(CurrentCustomerLocation.this,"3", Toast.LENGTH_SHORT).show();
                    GPSTracker gps = new GPSTracker(CurrentCustomerLocation.this);
                    CurrLongData = gps.getLatitude();
                    CurrLatData = gps.getLongitude();
                    //Toast.makeText(CurrentCustomerLocation.this,"4", Toast.LENGTH_SHORT).show();
                    LatLng loc = new LatLng(CurrLongData, CurrLatData);
                    LatLng origin = new LatLng(CurrLongData, CurrLatData);
                    LatLng dest = new LatLng(Double.parseDouble(separated[0]), Double.parseDouble(separated[1]));
                    CustLongData = Double.parseDouble(separated[0]);
                    CustLatData = Double.parseDouble(separated[1]);
                    //Toast.makeText(CurrentCustomerLocation.this,"5", Toast.LENGTH_SHORT).show();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                    //VisibleRegion tt = mMap.getProjection().getVisibleRegion();
                    String url = getDirectionsUrl(origin, dest);
                    String VarAreaName = "";
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);

                    //Toast.makeText(CurrentCustomerLocation.this,"6", Toast.LENGTH_SHORT).show();
                    try {
                        String languageToLoad = "ar"; // your language
                        Locale locale = new Locale(language);

                        Geocoder geo = new Geocoder(this.getApplicationContext(), locale);
                        List<android.location.Address> addresses = geo.getFromLocation(CustLongData, CustLatData, 1);
                        if (addresses.isEmpty()) {
                            VarAreaName = "Waiting for Location";
                        } else {
                            if (addresses.size() > 0) {
                                VarAreaName = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                                TextView btn = (TextView) findViewById(R.id.txtCustomerLocation);
                                if (language.equals("en")) {
                                    btn.setGravity(Gravity.LEFT);
                                    btn.setText("Customer Location " + VarAreaName);
                                } else {
                                    btn.setGravity(Gravity.RIGHT);
                                    btn.setText("موقع العميل : " + VarAreaName);
                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(CurrentCustomerLocation.this,"7", Toast.LENGTH_SHORT).show();


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Toast.makeText(this,"ddd",Toast.LENGTH_LONG).show();
                }

                return;
            }
        }
    }


    void chicpermision() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 0);
                return;
            }
        }
        //GetLocation();

    }


    public void GoToCustomerLocation(View v) {
        LatLng loc = new LatLng(CustLongData, CustLatData);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
    }

    public void RefreshLocation(View v) {
        mMap.clear();
        String[] separated = XYLocation.split(",");
        LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener currentLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(CurrentCustomerLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CurrentCustomerLocation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "suha", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(CurrentCustomerLocation.this,"2", Toast.LENGTH_SHORT).show();
        CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, currentLocationListener);
        if (!CurrentLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            TurnGbsOnAlert();
        }
        Toast.makeText(CurrentCustomerLocation.this, "GPS Connected ", Toast.LENGTH_SHORT).show();
        GPSTracker gps = new GPSTracker(CurrentCustomerLocation.this);
        CurrLongData = gps.getLatitude();
        CurrLatData = gps.getLongitude();
        //Toast.makeText(CurrentCustomerLocation.this,"4", Toast.LENGTH_SHORT).show();
        LatLng loc = new LatLng(CurrLongData, CurrLatData);
        LatLng origin = new LatLng(CurrLongData, CurrLatData);
        LatLng dest = new LatLng(Double.parseDouble(separated[0]), Double.parseDouble(separated[1]));
        CustLongData = Double.parseDouble(separated[0]);
        CustLatData = Double.parseDouble(separated[1]);
        //Toast.makeText(CurrentCustomerLocation.this,"5", Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        //VisibleRegion tt = mMap.getProjection().getVisibleRegion();
        String url = getDirectionsUrl(origin, dest);
        String VarAreaName = "";
        //Toast.makeText(CurrentCustomerLocation.this,"6", Toast.LENGTH_SHORT).show();

        createMarker(CurrLongData, CurrLatData, "الموقع الحالي", "", 1);
        createMarker(CustLongData, CustLatData, "موقع العميل", VarCustName, 2);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
        try {
            String languageToLoad = "ar"; // your language
            Locale locale = new Locale(language);

            Geocoder geo = new Geocoder(this.getApplicationContext(), locale);
            List<android.location.Address> addresses = geo.getFromLocation(CustLongData, CustLatData, 1);
            if (addresses.isEmpty()) {
                VarAreaName = "Waiting for Location";
            } else {
                if (addresses.size() > 0) {
                    VarAreaName = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                    TextView btn = (TextView) findViewById(R.id.txtCustomerLocation);
                    if (language.equals("en")) {
                        btn.setGravity(Gravity.LEFT);
                        btn.setText("Customer Location " + VarAreaName);
                    } else {
                        btn.setGravity(Gravity.RIGHT);
                        btn.setText("موقع العميل : " + VarAreaName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(CurrentCustomerLocation.this,"7", Toast.LENGTH_SHORT).show();

    }

    public void SetSatView(View v) {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void SetStreetView(View v) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void SetTrafficView(View v) {
        if (VarTrafficVisable == 0) {
            mMap.setTrafficEnabled(true);
            VarTrafficVisable = 1;
        } else {
            mMap.setTrafficEnabled(false);
            VarTrafficVisable = 0;
        }
    }

    public void gotogoogleMap(View view) {
        Toast.makeText(this, "hellow", Toast.LENGTH_SHORT).show();
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", CustLongData, CustLatData);
      /* Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+CurrLatData+","+CurrLongData+"&daddr="+CustLatData+","+CustLongData));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }*/ // Uri gmmIntentUri = Uri.parse("geo:"+CustLongData+CustLatData);


        /*Uri navigationIntentUri = Uri.parse("google.navigation:q=" + Cu  +"," + 2f);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);*/
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + CustLongData + "," + CustLatData));
        startActivity(intent);
    }

    class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
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
        if (isDest == 2) {
            return mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(title)
                    .snippet(snippet));
        } else {
            Bitmap b = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.fuel);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
            return mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
    }

    public void StartTrip(View view) {
        if (VarIsOnRoad) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CurrentCustomerLocation.this);

            TextView title = new TextView(this);
            if (language.equals("en")){
                title.setText("Submit");
            }else{ title.setText("تأكيد");}

            title.setBackgroundColor(Color.DKGRAY);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.RIGHT);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);
            builder.setCustomTitle(title);
            String positiveText = "";
            String negativeText ="";
            if (language.equals("en")) {
                builder.setMessage("Access to the client site and you will start unloading?");
                 positiveText = "yes";
                 negativeText = "NO";
            }else{
                builder.setMessage("تم الوصول الى موقع العميل وسيتم بدء التفريغ ؟");

                positiveText = "نعم";
                negativeText = "لا";
            }

            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //startActivity(new Intent(getApplicationContext(),CurrentCustomerLocation.class));
                            new UpDateOrderStatus().execute();
                            finish();
                        }

                    });


            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CurrentCustomerLocation.this);
            String positiveText="";
            String negativeText = "";
            TextView title = new TextView(this);
            if (language.equals("en")){
                title.setText("Submit");
                builder.setMessage("The client will notice the start of the trip, to confirm?");
                positiveText = "Yes";
                negativeText = "Noا";

            }else{
                title.setText("تأكيد");
                builder.setMessage("سوف يتم اشعار العميل ببدء الرحلة, تأكيد ؟");
                positiveText = "نعم";
                negativeText = "لا";
            }
            title.setBackgroundColor(Color.DKGRAY);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.RIGHT);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);
            builder.setCustomTitle(title);


            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VarStatus = "1";
                            VarIsDone = "0";
                            Intent i = new Intent(CurrentCustomerLocation.this, GPSService.class);
                            i.putExtra("VarOrderNo", VarOrderNo);
                            CurrentCustomerLocation.this.startService(i);
                            new ChangeStatusTask().execute();
                            finish();
                        }
                    });


            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    class UpDateOrderStatus extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "UpdateOrderStatus";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateOrderStatus";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarOrderNo", VarOrderNo);
                request.addProperty("VarOrderStatus", 3);

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


        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class ChangeStatusTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "ChangeTripStatus";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/ChangeTripStatus";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarStatus", VarStatus);
                request.addProperty("VarIsDone", VarIsDone);
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
            if (VarStatus.equals("0")) {
               /* Intent i = new Intent(CurrentCustomerLocation.this, GPSService.class);
                CurrentCustomerLocation.this.stopService(i);
                //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                Intent myIntent = new Intent(CurrentCustomerLocation.this, DrawingSign.class);
                myIntent.putExtra("OrderNo", VarOrderNo);
                myIntent.putExtra("CustID", VarCustID);
                CurrentCustomerLocation.this.startActivity(myIntent);*/
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {


        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
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

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
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
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();


                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

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
            if (lineOptions != null) {
                //mMap.clear();
                mMap.addPolyline(lineOptions);

                createMarker(CurrLongData, CurrLatData, "الموقع الحالي", "", 1);
                createMarker(CustLongData, CustLatData, "موقع العميل", VarCustName, 2);
            } else {


                Log.d("onPostExecute", String.valueOf(result));

                Toast.makeText(CurrentCustomerLocation.this, "Please Refresh Map", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Location oldLocation;

    class MyThred extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GPSTracker gps = new GPSTracker(CurrentCustomerLocation.this);
                            mMap.clear();
                            String[] separated = XYLocation.split(",");
                            LocationListener currentLocationListener = new MyLocationListener();
                            LocationManager CurrentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            CurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, currentLocationListener);
                            CurrLongData = gps.getLatitude();
                            CurrLatData = gps.getLongitude();
                            //Toast.makeText(CurrentCustomerLocation.this,"4", Toast.LENGTH_SHORT).show();
                            LatLng loc = new LatLng(CurrLongData, CurrLatData);
                            LatLng origin = new LatLng(CurrLongData, CurrLatData);
                            LatLng dest = new LatLng(Double.parseDouble(separated[0]), Double.parseDouble(separated[1]));
                            CustLongData = Double.parseDouble(separated[0]);
                            CustLatData = Double.parseDouble(separated[1]);
                            //Toast.makeText(CurrentCustomerLocation.this,"5", Toast.LENGTH_SHORT).show();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                            VisibleRegion tt = mMap.getProjection().getVisibleRegion();
                            String url = getDirectionsUrl(origin, dest);
                            String VarAreaName = "";


                            //Toast.makeText(CurrentCustomerLocation.this,"6", Toast.LENGTH_SHORT).show();
                            try {
                                String languageToLoad = "ar"; // your language
                                Locale locale = new Locale(languageToLoad);

                                Geocoder geo = new Geocoder(gps.getApplicationContext(), locale);
                                List<android.location.Address> addresses = geo.getFromLocation(CustLongData, CustLatData, 1);
                                if (addresses.isEmpty()) {
                                    VarAreaName = "Waiting for Location";
                                } else {
                                    if (addresses.size() > 0) {
                                        VarAreaName = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                                        TextView btn = (TextView) findViewById(R.id.txtCustomerLocation);
                                        btn.setText("موقع العميل : " + VarAreaName);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(CurrentCustomerLocation.this,"7", Toast.LENGTH_SHORT).show();
                            DownloadTask downloadTask = new DownloadTask();
                            downloadTask.execute(url);
                        }
                    });
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
        }

    }

}

