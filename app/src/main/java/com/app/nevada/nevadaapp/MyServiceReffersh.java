package com.app.nevada.nevadaapp;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.app.nevada.nevadaapp.FollowOrder;
import com.app.nevada.nevadaapp.ListViewAdapterOldOrders;
import com.app.nevada.nevadaapp.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.app.nevada.nevadaapp.Constants.LT_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_THIRD_COLUMN;

public class MyServiceReffersh extends Service {
    final Handler ha = new Handler();
    Runnable VarRunnable;
    SoapObject responseObject;
    Activity activity= new FollowOrder();
    String intfollow;
    String VarAccNo = "";
    Context context;
    private ArrayList<HashMap<String, String>> list;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intent = activity.getIntent();
        activity = (Activity) getApplicationContext();
        VarAccNo = intent.getStringExtra("acctNo");
        ha.postDelayed(VarRunnable = new Runnable() {
            @Override
            public void run() {

                new GetCurrentOrdersTask(activity).execute();
            }
        }, 10000);
        return Service.START_STICKY;


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class GetCurrentOrdersTask extends AsyncTask<Integer, Integer, String> {
        Activity activity;
        View convertView;

        public GetCurrentOrdersTask(Activity activity) {
            this.activity = activity;
        }

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

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_follow_order, null);
            ListView listView = (ListView) activity.findViewById(R.id.lvOrders);
            list = new ArrayList<HashMap<String, String>>();

            ListViewAdapterOldOrders adapter = new ListViewAdapterOldOrders(activity, list);
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
            adapter = new ListViewAdapterOldOrders(activity, list);
            listView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

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
}
