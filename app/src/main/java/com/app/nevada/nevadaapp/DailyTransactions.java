package com.app.nevada.nevadaapp;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.CUS_FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SEVENTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SIXTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;

public class DailyTransactions extends AppCompatActivity {
    private static final String METHOD_NAME = "GetOrdersReport";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/GetOrdersReport";
    private ArrayList<HashMap<String, String>> list;
    SoapObject responseObject;


    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private String varFromDate ;
    private String varToDate ;
    private TextView fromDateEtxt;
    private TextView toDateEtxt;
    private SimpleDateFormat dateFormatter;
    String VarIsOnRoad = "0";
    String VarIsDriverApproved = "0";
    String VarIsApproved = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_transactions);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        findViewsById();
        setDateTimeField();
    }
    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                CharSequence output = DateFormat.format("dd-MM-yyyy", cal);
                fromDateEtxt.setText(output);
                varFromDate = fromDateEtxt.getText().toString();
                varToDate = toDateEtxt.getText().toString();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                CharSequence output = DateFormat.format("dd-MM-yyyy", cal);
                toDateEtxt.setText(output);
                varFromDate = fromDateEtxt.getText().toString();
                varToDate = toDateEtxt.getText().toString();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        varFromDate = fromDateEtxt.getText().toString();
        varToDate = toDateEtxt.getText().toString();
    }
    private void findViewsById() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int VarThisMonth = calendar.get(Calendar.MONTH);
        String ThisMonthFinal ="";
        if (VarThisMonth < 10){;
            ThisMonthFinal = "0"+String.valueOf(VarThisMonth+1);
        }else{
            ThisMonthFinal = String.valueOf(VarThisMonth+1);
        }
        fromDateEtxt = (TextView) findViewById(R.id.tranDateFromFilter);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        toDateEtxt = (TextView) findViewById(R.id.tranDateToFilter);
        toDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.setText("01-" + ThisMonthFinal + "-"+year);
        Calendar cal = Calendar.getInstance();
        CharSequence output = DateFormat.format("dd-MM-yyyy", cal);
        String[] VarDateVal = (String.valueOf(output).split("-"));
        int day = Integer.valueOf(VarDateVal[0]);
        int month =  Integer.valueOf(VarDateVal[1]);
        if (month < 10){
            if (day<10){
                toDateEtxt.setText("0"+day+"-0"+month+"-"+year);
            }else{
                toDateEtxt.setText(day+"-0"+month+"-"+year);
            }
        }else{
            if (day<10){
                toDateEtxt.setText("0"+day+"-"+month+"-"+year);
            }else{
                toDateEtxt.setText(day+"-"+month+"-"+year);
            }
        }
        varFromDate = fromDateEtxt.getText().toString();
        varToDate = toDateEtxt.getText().toString();
    }
    public void callDate(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if(view == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }
    public void UpdateOnCheck(View v){
        String id = v.getTag().toString();
        RadioButton rbAllOrders = (RadioButton) findViewById(R.id.rbAllOrders);
        RadioButton rbOnRoad = (RadioButton) findViewById(R.id.rbOnRoad);
        RadioButton rbApprovedDriver = (RadioButton) findViewById(R.id.rbApprovedDriver);
        RadioButton rbApprovedBoth = (RadioButton) findViewById(R.id.rbApprovedBoth);
        if (id.equals("0")){
            rbOnRoad.setChecked(false);
            rbApprovedDriver.setChecked(false);
            rbApprovedBoth.setChecked(false);
            VarIsOnRoad = "0";
            VarIsDriverApproved = "0";
            VarIsApproved = "0";
        }else if (id.equals("1")){
            rbAllOrders.setChecked(false);
            rbApprovedDriver.setChecked(false);
            rbApprovedBoth.setChecked(false);
            VarIsOnRoad = "1";
            VarIsDriverApproved = "0";
            VarIsApproved = "0";
        }else if (id.equals("2")){
            rbAllOrders.setChecked(false);
            rbOnRoad.setChecked(false);
            rbApprovedBoth.setChecked(false);
            VarIsOnRoad = "0";
            VarIsDriverApproved = "1";
            VarIsApproved = "0";
        }else if (id.equals("3")){
            rbAllOrders.setChecked(false);
            rbOnRoad.setChecked(false);
            rbApprovedDriver.setChecked(false);
            VarIsOnRoad = "0";
            VarIsDriverApproved = "0";
            VarIsApproved = "1";
        }
    }
    public void SearchRepOrders(View v){
        ImageView imgCurrentSearch = (ImageView)findViewById(R.id.imgCurrentOrderSearch);
        imgCurrentSearch.setVisibility(View.VISIBLE);
        new GetSearchOrderResult().execute();
    }
    class GetSearchOrderResult extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("VarFromDate", varFromDate);
                request.addProperty("VarToDate", varToDate);
                request.addProperty("VarIsOnRoad", VarIsOnRoad);
                request.addProperty("VarIsDriverApproved", VarIsDriverApproved);
                request.addProperty("VarIsApproved", VarIsApproved);

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
            ListView listView=(ListView)findViewById(R.id.lvSearchOrdersResults);
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdaptersOrdersReport adapter=new ListViewAdaptersOrdersReport(DailyTransactions.this, list);
            listView.setAdapter(adapter);

            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String,String> temp=new HashMap<String, String>();
                int count = reponseArrayString.length;
                temp.put(CUS_FIRST_COLUMN, reponseArrayString[0]);
                temp.put(CUS_SECOND_COLUMN, reponseArrayString[1]);
                temp.put(CUS_THIRD_COLUMN, reponseArrayString[2]);
                temp.put(CUS_FOURTH_COLUMN, reponseArrayString[3]);
                temp.put(CUS_FIFTH_COLUMN, reponseArrayString[4]);
                list.add(temp);
            }

            adapter=new ListViewAdaptersOrdersReport(DailyTransactions.this, list);
            listView.setAdapter(adapter);
            ImageView imgCurrentSearch = (ImageView)findViewById(R.id.imgCurrentOrderSearch);
            imgCurrentSearch.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}
