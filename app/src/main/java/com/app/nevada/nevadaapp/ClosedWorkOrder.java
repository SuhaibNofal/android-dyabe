package com.app.nevada.nevadaapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.CUS_FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;

public class ClosedWorkOrder extends AppCompatActivity {
    ArrayList<HashMap<String, String>> list;
    String languges;
    SoapObject responseObject;
    String varCustName;
    String varAcctNo;
    TextView text_balansein_Arabic, Number_balansein_Arabic,text_balansein_English, Number_balansein_English;
    String  workclosed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_work_order);
        Intent intent = getIntent();
        languges = intent.getStringExtra("lang");
        varCustName = intent.getStringExtra("varCustName");
        varAcctNo = intent.getStringExtra("acctNo");
        workclosed=intent.getStringExtra("close");
        Locale locale = new Locale(languges);
        forceLocale(this, locale);
        findViewsById();
        if(languges.equals("en")){
            text_balansein_Arabic.setText("");
            text_balansein_English.setText(R.string.Closed_Work_Orders);
            Number_balansein_Arabic.setText("");
            Number_balansein_English.setText(workclosed);
        }else{
            text_balansein_Arabic.setText(R.string.Closed_Work_Orders);
            text_balansein_English.setText("");
            Number_balansein_Arabic.setText(workclosed);
            Number_balansein_English.setText("");
        }
        new CloseWork().execute();

    }

    private void findViewsById() {
        TextView textView = (TextView) findViewById(R.id.customer_name_work);
        textView.setText(varCustName);
        TextView textView2 = (TextView) findViewById(R.id.customer_acc_number);
        textView2.setText(varAcctNo);
        text_balansein_Arabic = (TextView) findViewById(R.id.text_balansein_Arabic);
        Number_balansein_Arabic = (TextView) findViewById(R.id.Number_balansein_Arabic);
        text_balansein_English = (TextView) findViewById(R.id.text_balansein_English);
        Number_balansein_English = (TextView) findViewById(R.id.Number_balansein_English);

    }

    public static void forceLocale(Context ctx, Locale locale) {
        Configuration conf = ctx.getResources().getConfiguration();
        conf.locale = locale;
        ctx.getResources().updateConfiguration(conf, ctx.getResources().getDisplayMetrics());

        Configuration systemConf = Resources.getSystem().getConfiguration();
        systemConf.locale = locale;
        Resources.getSystem().updateConfiguration(systemConf, Resources.getSystem().getDisplayMetrics());

        Locale.setDefault(locale);
    }

    public class CloseWork extends AsyncTask<Integer, Integer, String> {


        @Override
        protected String doInBackground(Integer... integers) {
            try {
                String METHOD_NAME = "getCloesdWorkOrder";
                String NAMESPASE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/getCloesdWorkOrder";
                SoapObject request = new SoapObject(NAMESPASE, METHOD_NAME);
                request.addProperty("VarCustID", varAcctNo);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidhttpTransportSE = new HttpTransportSE(URL, 1000000000);
                androidhttpTransportSE.debug = true;
                androidhttpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                androidhttpTransportSE.call(SOAP_ACTION, envelope);
                responseObject = (SoapObject) envelope.getResponse();
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            ListView listView = (ListView) findViewById(R.id.list_Closed_work_order);
            list = new ArrayList<HashMap<String, String>>();
            ListViewAdapterWorkOrder listViewAdapterWorkOrder = new ListViewAdapterWorkOrder(ClosedWorkOrder.this, list);
            listView.setAdapter(listViewAdapterWorkOrder);
            //if(responseObject.getPropertyCount()!=0){}
            for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                String[] arryofstring = responseObject.getProperty(i).toString().split("&&");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(CUS_FIRST_COLUMN, arryofstring[0]);//colum cridet
                map.put(CUS_SECOND_COLUMN, arryofstring[1]);//colum order number
                map.put(CUS_THIRD_COLUMN, arryofstring[3]);//colum station name
                String Date = arryofstring[2].replaceAll("/", "-");
                map.put(CUS_FOURTH_COLUMN, Date.substring(0, Date.length() - 11));//colum upload date
                list.add(map);
            }
            listViewAdapterWorkOrder = new ListViewAdapterWorkOrder(ClosedWorkOrder.this, list);
            listView.setAdapter(listViewAdapterWorkOrder);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

}
