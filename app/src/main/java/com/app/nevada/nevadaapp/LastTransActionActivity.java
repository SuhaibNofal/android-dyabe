package com.app.nevada.nevadaapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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


public class LastTransActionActivity extends AppCompatActivity {

    private static final String METHOD_NAME = "getLastTrans";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/getLastTrans";
    private ArrayList<HashMap<String, String>> list;
    private int varFrom = 0;
    private  int varTo = 150;
    private String varAcctNo ="";
    private String varCustName ="";
    private String varTranType ="";
    private String varTranNo ="";
    SoapObject responseObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_last_trans_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        String varSource = intent.getStringExtra("varSource");
        if (varSource.equals("1")){
            varAcctNo =intent.getStringExtra("varAcctNo");
            varCustName =intent.getStringExtra("varCustName");
            TextView txtViewObject = (TextView)findViewById(R.id.txtCusAccName);
            txtViewObject.setText(varCustName);
            txtViewObject = (TextView)findViewById(R.id.txtCusAccNumber);
            txtViewObject.setText(varAcctNo);
            new lastTranTask().execute();
        }else{
            TextView txtViewObject = (TextView)findViewById(R.id.txtCusAccName);
            txtViewObject.setText("-----");
            txtViewObject = (TextView)findViewById(R.id.txtCusAccNumber);
            txtViewObject.setText("-----");
            varTranType =intent.getStringExtra("varTranType");
            varTranNo =intent.getStringExtra("varTranNo");
            new getTranTask().execute();
        }


    }

    class lastTranTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("Acc_no", varAcctNo);

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
            ListView listView=(ListView)findViewById(R.id.lvLastTrans);
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdaptersLastTran adapter=new ListViewAdaptersLastTran(LastTransActionActivity.this, list);
            listView.setAdapter(adapter);
            TextView txtViewObject;
            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String,String> temp=new HashMap<String, String>();
                int count = reponseArrayString.length;
                if (reponseArrayString.length == 7)
                {
                    temp.put(LT_FOURTH_COLUMN, reponseArrayString[6]);
                }else{
                    temp.put(LT_FOURTH_COLUMN, "---");
                }

                temp.put(LT_SECOND_COLUMN, reponseArrayString[3]);
                temp.put(LT_FIRST_COLUMN, reponseArrayString[5]);
                temp.put(LT_THIRD_COLUMN, reponseArrayString[4]);

                list.add(temp);
                txtViewObject = (TextView)findViewById(R.id.txtTranTypeData);
                txtViewObject.setText(reponseArrayString[0]);
                txtViewObject = (TextView)findViewById(R.id.txtTranNoData);
                txtViewObject.setText(reponseArrayString[1]);
                txtViewObject = (TextView)findViewById(R.id.txtTranDateData);
                String dateAll = reponseArrayString[2].toString();
                String dateFinal = dateAll.substring(0, Math.min(dateAll.length(), 10)).replace('/','-');
                txtViewObject.setText(dateFinal);
            }
            if (list.size() <=0) {
                listView.setVisibility(View.INVISIBLE);
                TextView txtNoTrans = (TextView) findViewById(R.id.txtNoTran);
                txtNoTrans.setVisibility(View.VISIBLE);
            }
            adapter=new ListViewAdaptersLastTran(LastTransActionActivity.this, list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                {
                    //Toast.makeText(MainActivity.this, txtCAccNo.getText(), Toast.LENGTH_SHORT).show();
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
    class getTranTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME_Tran = "getTrans";
                String SOAP_ACTION_Tran = "http://37.224.24.195/getTrans";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_Tran);

                request.addProperty("TranTypeNo", varTranType);
                request.addProperty("TranNo", varTranNo);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION_Tran, envelope);
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
            ListView listView=(ListView)findViewById(R.id.lvLastTrans);
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdaptersLastTran adapter=new ListViewAdaptersLastTran(LastTransActionActivity.this, list);
            listView.setAdapter(adapter);
            TextView txtViewObject;
            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String,String> temp=new HashMap<String, String>();
                int count = reponseArrayString.length;
                String t =reponseArrayString[6];
                temp.put(LT_FOURTH_COLUMN, reponseArrayString[6]);
                temp.put(LT_SECOND_COLUMN, reponseArrayString[3]);
                temp.put(LT_FIRST_COLUMN, reponseArrayString[5]);
                temp.put(LT_THIRD_COLUMN, reponseArrayString[4]);

                list.add(temp);
                txtViewObject = (TextView)findViewById(R.id.txtTranTypeData);
                txtViewObject.setText(reponseArrayString[0]);
                txtViewObject = (TextView)findViewById(R.id.txtTranNoData);
                txtViewObject.setText(reponseArrayString[1]);
                txtViewObject = (TextView)findViewById(R.id.txtTranDateData);
                String dateAll = reponseArrayString[2].toString();
                String dateFinal = dateAll.substring(0, Math.min(dateAll.length(), 10)).replace('/','-');
                txtViewObject.setText(dateFinal);
            }

            adapter=new ListViewAdaptersLastTran(LastTransActionActivity.this, list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                {
                    //Toast.makeText(MainActivity.this, txtCAccNo.getText(), Toast.LENGTH_SHORT).show();
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
}
