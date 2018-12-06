package com.app.nevada.nevadaapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.LT_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FOURTH_COLUMN;

public class FollowOrder extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<HashMap<String, String>> list;
    private String varSelectedNo = "N";
    private String varDriverNo = "N";
    private String VarAccNo;
    SoapObject responseObject;
    Intent intent1;
    //Threaded t;
    ListView listView;
    ListViewAdapterOldOrders adapter;
    final Handler ha = new Handler();
    Runnable VarRunnable ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*String languageToLoad  = "EN-US";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/
        setContentView(R.layout.activity_follow_order);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        VarAccNo = intent.getStringExtra("acctNo");
       /* intent1 = new Intent(this, MyServiceReffersh.class);
        intent1.putExtra("acctNo", VarAccNo);*//*
        startService(intent1);*/
        final ListView listView = (ListView) findViewById(R.id.lvOrders);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                TextView txtCAccNo = (TextView) view.findViewById(R.id.lblOrderNo);
                varSelectedNo = txtCAccNo.getText().toString();
            }

        });
        new GetCurrentOrdersTask().execute();
        /*t = new Threaded(mhandler);
        t.start();*/
        ha.postDelayed(VarRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "D", Toast.LENGTH_SHORT).show();
                new GetCurrentOrdersTask1().execute();
                ha.postDelayed(this, 9000);
            }
        },9000);
    }
    /*private Handler myHandler= new Handler();;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //Do Something
        }
    };*/

    @Override
    public void onDestroy () {

        ha.removeCallbacks(VarRunnable);
        super.onDestroy ();

    }
   /* private final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 8:
                    Toast.makeText(getApplicationContext(), "D", Toast.LENGTH_SHORT).show();
                    new GetCurrentOrdersTask().execute();

            }
        }
    };*/

   /* class Threaded extends Thread {
        private final Handler mhandeler;

        public Threaded(Handler mhandeler) {
            this.mhandeler = mhandeler;
        }

        @Override
        public void run() {
            int i = 0;
            while (true) {
                Message msg = mhandeler.obtainMessage(8);
                Bundle bundle = new Bundle();
                bundle.putString("refresh", "refresh");
                msg.setData(bundle);
                mhandeler.sendMessage(msg);

                i++;
                try {
                    Thread.sleep(9000);
                } catch (Exception e) {

                }
            }
        }
    }*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near loc, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng loc = new LatLng(24.685539, 46.686655);
        mMap.addMarker(new MarkerOptions().position(loc).title("الموقع الحالي"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        mMap.clear();
    }


    public void callCompany(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:920000194 "));
        startActivity(intent);
    }

    public void callDriver(View v) {
        Locale locale = Locale.getDefault();
        String language = String.valueOf(locale);
        if (varSelectedNo.equals("N")) {
            if (language.contains("en")) {
                Toast.makeText(FollowOrder.this, "Please Select Order", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FollowOrder.this, "الرجاء اختيار طلبية", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (varDriverNo.equals("N")) {
                if (varSelectedNo.equals("N")) {
                    Toast.makeText(FollowOrder.this, "Driver number not available", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FollowOrder.this, "رقم السائق غير متوفر", Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + varDriverNo));
                startActivity(intent);
            }
        }


    }

    public String GetItemName(Integer No) {
        String ItemName = "";
        switch (No) {
            case 1:
                ItemName = "بنزين 95";
                break;
            case 2:
                ItemName = "بنزين 91";
                break;
            case 3:
                ItemName = "ديزل";
                break;
            case 4:
                ItemName = "خام زيت";
                break;
            case 5:
                ItemName = "كيروسين";
                break;
            default:
                ItemName = "بنزين 95";
        }
        return ItemName;

    }

    public void RefreshMap(View v) {
        Locale locale = Locale.getDefault();
        String language = String.valueOf(locale);
        if (varSelectedNo.equals("N")) {
            if (language.contains("en")) {
                Toast.makeText(FollowOrder.this, "Please Select Order", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FollowOrder.this, "الرجاء اختيار طلبية", Toast.LENGTH_SHORT).show();

            }
        } else {
            new GetOrderInfoTask().execute();
        }
    }

    class GetCurrentOrdersTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "getCurrentOrders";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/getCurrentOrders";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("VarAccNo", VarAccNo);

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

            listView = (ListView) findViewById(R.id.lvOrders);
            list = new ArrayList<HashMap<String, String>>();

            adapter = new ListViewAdapterOldOrders(FollowOrder.this, list);
            listView.setAdapter(adapter);

            for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                String[] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String, String> temp = new HashMap<String, String>();
                int count = reponseArrayString.length;
                temp.put(LT_FIRST_COLUMN, reponseArrayString[0]);
                temp.put(LT_SECOND_COLUMN, GetItemName(Integer.parseInt(reponseArrayString[2])));
                temp.put(LT_THIRD_COLUMN, reponseArrayString[7]);
                temp.put(LT_FOURTH_COLUMN, reponseArrayString[4]);
                list.add(temp);
            }
            adapter = new ListViewAdapterOldOrders(FollowOrder.this, list);
            listView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class GetOrderInfoTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "getOrderInfo";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/getOrderInfo";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarOrderNo", varSelectedNo);

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
            for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                String[] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                String tt = reponseArrayString[0];
                if (reponseArrayString[0] != null) {
                    mMap.clear();
                    Bitmap b = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.fuel);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                    LatLng loc = new LatLng(Double.parseDouble(reponseArrayString[0]), Double.parseDouble(reponseArrayString[1].toString()));
                    mMap.addMarker(new MarkerOptions().position(loc).title("الموقع الحالي").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

                   /* Locale locale = Locale.getDefault();
                    String language = String.valueOf(locale);*/

                    TextView tvDriverName = (TextView) findViewById(R.id.txtDriverName);
                    tvDriverName.setText(reponseArrayString[3]);
                    TextView tvOrderDate = (TextView) findViewById(R.id.txtOrderDate);
                    tvOrderDate.setText(reponseArrayString[2].toString().substring(0, 10));
                    TextView tvOrderNo = (TextView) findViewById(R.id.txtOrderNo);
                    TextView tvSep = (TextView) findViewById(R.id.txtSeperator);
                    tvSep.setVisibility(View.VISIBLE);
                    tvOrderNo.setText(varSelectedNo);
                    varDriverNo = reponseArrayString[4];
                }


            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }


/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        t.interrupt();
    }*/
class GetCurrentOrdersTask1 extends AsyncTask<Integer, Integer, String> {
    @Override
    protected String doInBackground(Integer... params) {
        try {

            String METHOD_NAME = "getCurrentOrders";
            String NAMESPACE = "http://37.224.24.195";
            String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
            String SOAP_ACTION = "http://37.224.24.195/getCurrentOrders";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("VarAccNo", VarAccNo);

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

       for (int i = 0; i < responseObject.getPropertyCount(); i++) {
            String[] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
            HashMap<String, String> temp = new HashMap<String, String>();
            int count = reponseArrayString.length;
            temp.put(LT_FIRST_COLUMN, reponseArrayString[0]);
            temp.put(LT_SECOND_COLUMN, GetItemName(Integer.parseInt(reponseArrayString[2])));
            temp.put(LT_THIRD_COLUMN, reponseArrayString[7]);
            temp.put(LT_FOURTH_COLUMN, reponseArrayString[4]);
            if(list.contains(temp)){

            }else{
                list.add(temp);
            }
           adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }
}
}
