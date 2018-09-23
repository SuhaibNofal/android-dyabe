package com.app.nevada.nevadaapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;

public class SendSMS extends AppCompatActivity {

    String VarForAll = "T";
    String VarClientAcc = "T";
    String VarClientName = "N";
    String VarMessage = "N";
    private static final String METHOD_NAME = "SendSMS";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/SendSMS";
    SoapObject responseObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        Intent intent = getIntent();
        VarForAll= intent.getStringExtra("ForAll");
        VarClientAcc= intent.getStringExtra("acctNo");
        VarClientName= intent.getStringExtra("ClientName");
        if (VarForAll.equals("T"))
        {
            EditText txtName=(EditText)findViewById(R.id.txtUserName);
            txtName.setText("لجميع العملاء");
            TextView Alert1=(TextView)findViewById(R.id.txtAlert1);
            TextView Alert2=(TextView)findViewById(R.id.txtAlert2);
            Alert1.setVisibility(View.VISIBLE);
            Alert2.setVisibility(View.VISIBLE);
        }
        else {
            EditText txtName=(EditText)findViewById(R.id.txtUserName);
            txtName.setText(VarClientName);
            TextView Alert1=(TextView)findViewById(R.id.txtAlert1);
            TextView Alert2=(TextView)findViewById(R.id.txtAlert2);
            Alert1.setVisibility(View.INVISIBLE);
            Alert2.setVisibility(View.INVISIBLE);
        }
    }
    public void SendMessage(View v){
        EditText txtMessage=(EditText)findViewById(R.id.messageArea);
        VarMessage=txtMessage.getText().toString();
        if (VarMessage.equals("")){
            Toast.makeText(SendSMS.this,"الرجاء ادخال الرسالة المراد ارسالها !", Toast.LENGTH_SHORT).show();
        }else{
            new SendSMSTask().execute();
        }
    }
    class SendSMSTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("VarAccNo", VarClientAcc);
                request.addProperty("VarMessage", VarMessage);

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
            for(int i=0;i<responseObject.getPropertyCount();i++){
                String  reponseArrayString = responseObject.getProperty(i).toString();
                if (reponseArrayString.equals("1"))
                {
                    Toast.makeText(SendSMS.this,"تم ارسال الرسالة بنجاح !", Toast.LENGTH_SHORT).show();
                    finish();
                }else
                {
                    Toast.makeText(SendSMS.this,"لم يتم ارسال الرسالة ! يرجى معاودة الارسال ....", Toast.LENGTH_SHORT).show();
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
}
