package com.app.nevada.nevadaapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.AR_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_THIRD_COLUMN;

public class FinishedOrders extends AppCompatActivity {
    String VarAccNo = "";
    SoapObject responseObject;
    boolean ordersExist = false;
    String VarOrderNo ="";
    String VarNotes = "";
    String VarIsLate = "0";
    String VarIsQtyNotGood = "0";
    String VarIsItemNotGood ="0";
    String VarNumberOfStars = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad  = "EN-US";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_finished_orders);


        Intent intent = getIntent();
        VarAccNo= intent.getStringExtra("acctNo");

        new GetFinishedOrders().execute();
    }
    public void UpdateFinished(View view){
        Button btn = ((Button) view);
        VarOrderNo = btn.getTag().toString();

        new UpdateFinishedOrders().execute();
    }
    public void ConfirmAll(View view){
        if (ordersExist){
            new UpdateAllFinishedOrders().execute();
        }else{
            Toast.makeText(FinishedOrders.this,"لا يوجد طلبيات للتأكيد", Toast.LENGTH_SHORT).show();
        }

    }
    public String GetItemName(Integer No)
    {
        String ItemName = "";
        switch(No) {
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
        return  ItemName;

    }
    class GetFinishedOrders extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "GetFinishedOrders";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/GetFinishedOrders";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("VarAccountNo", VarAccNo);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
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
        protected void onPostExecute(String result) {
            ListView listView=(ListView)findViewById(R.id.lvOrdersToConfirm);
            ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
            ListViewAdapterOrdersToCOnfirm adapter=new ListViewAdapterOrdersToCOnfirm(FinishedOrders.this, list);
            listView.setAdapter(adapter);
            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String,String> temp=new HashMap<String, String>();
                temp.put(AR_FIRST_COLUMN, reponseArrayString[0]);
                temp.put(AR_SECOND_COLUMN, GetItemName(Integer.parseInt(reponseArrayString[1])));
                temp.put(AR_THIRD_COLUMN, reponseArrayString[2]);
                list.add(temp);
                ordersExist = true;
            }

            adapter=new ListViewAdapterOrdersToCOnfirm(FinishedOrders.this, list);
            listView.setAdapter(adapter);
            TextView txtNoOrders = (TextView)findViewById(R.id.txtNoOrders);
            if (ordersExist){
                txtNoOrders.setVisibility(View.INVISIBLE);
            }else{
                txtNoOrders.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    class UpdateFinishedOrders extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "UpdateFinished";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/UpdateFinished";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("VarOrderNo", VarOrderNo);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
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
        protected void onPostExecute(String result) {

            final Dialog dialog = new Dialog(FinishedOrders.this);

            dialog.setTitle("تقييم الطلبية");


            Rect displayRectangle = new Rect();
            Window window = FinishedOrders.this.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            LayoutInflater inflater = (LayoutInflater)FinishedOrders.this.getSystemService(FinishedOrders.this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.rating_operation, null);
            layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
            layout.setMinimumHeight((int)(displayRectangle.height() * 0.6f));
            dialog.setContentView(layout);
            final RatingBar simpleRatingBar = (RatingBar) dialog.findViewById(R.id.simpleRatingBar);
            Button dialogButton = (Button) dialog.findViewById(R.id.btnRate);
            final EditText txtNotes = (EditText) dialog.findViewById(R.id.ratingMessage);
            final CheckBox chkLate = (CheckBox) dialog.findViewById(R.id.chkLate);
            final CheckBox chkQtyNotGood = (CheckBox) dialog.findViewById(R.id.chkNotGoodQty);
            final CheckBox chkItemNotGood = (CheckBox) dialog.findViewById(R.id.chkNotGoodItem);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(FinishedOrders.this,"تم تأكيد وصول الطلبية, شكرا لك", Toast.LENGTH_SHORT).show();
                    VarNumberOfStars = String.valueOf(Math.round(simpleRatingBar.getRating()));
                    VarNotes = txtNotes.getText().toString();
                    if (chkLate.isChecked()){
                        VarIsLate = "1";
                    }
                    if (chkQtyNotGood.isChecked()){
                        VarIsQtyNotGood = "1";
                    }
                    if (chkItemNotGood.isChecked()){
                        VarIsItemNotGood = "1";
                    }
                    new RateOrder().execute();

                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(final DialogInterface arg0) {
                    new GetFinishedOrders().execute();
                }
            });
            dialog.show();


        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class UpdateAllFinishedOrders extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "UpdateFinishedAll";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/UpdateFinishedAll";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("VarAccountNo", VarAccNo);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
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
        protected void onPostExecute(String result) {
            Toast.makeText(FinishedOrders.this,"تم تأكيد وصول جميع الطلبية, شكرا لك", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    class RateOrder extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "InsertComplaints";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/InsertComplaints";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("VarOrderID", VarOrderNo);
                request.addProperty("VarCustID", VarAccNo);
                request.addProperty("VarRate", VarNumberOfStars);
                request.addProperty("VarNote", VarNotes);
                request.addProperty("VarIsLate", VarIsLate);
                request.addProperty("VarIsQtyNotGood", VarIsQtyNotGood);
                request.addProperty("VarIsItemNotGood", VarIsItemNotGood);



                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
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
        protected void onPostExecute(String result) {
            Toast.makeText(FinishedOrders.this,"تم تقييم الطلبية, شكرا لك", Toast.LENGTH_SHORT).show();
            VarNumberOfStars = "0";
            VarNotes = "";
            VarIsLate = "0";
            VarIsQtyNotGood = "0";
            VarIsItemNotGood = "0";
            new GetFinishedOrders().execute();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

}