package com.app.nevada.nevadaapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.CUS_FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SEVENTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SIXTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;

public class RatingReport extends AppCompatActivity {

    private static final String METHOD_NAME = "getAllCustomer";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/getAllCustomer";
    private ArrayList<HashMap<String, String>> list;
    SoapObject responseObject;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private String varFromDate ;
    private String varToDate ;
    private TextView fromDateEtxt;
    private TextView toDateEtxt;
    private SimpleDateFormat dateFormatter;
    String VarCustomerAccNumber ="0";
    String VarIsLate = "0";
    String VarIsQtyNotGood = "0";
    String VarIsItemNotGood = "0";
    String VarRate = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_report);

        new GetAllCustomerTask().execute();

        Spinner ddlAllCustomers = (Spinner)findViewById(R.id.spCustomers);
        ddlAllCustomers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (pos > 0 )
                {
                    StringWithTag sel = (StringWithTag) parent.getItemAtPosition(pos);
                    VarCustomerAccNumber =sel.tag.toString();
                } else
                {
                    VarCustomerAccNumber ="0";
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
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
        fromDateEtxt = (TextView) findViewById(R.id.tranDateFromFilter);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        toDateEtxt = (TextView) findViewById(R.id.tranDateToFilter);
        toDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.setText("01-01-"+year);
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
    public void SearchOrders(View v){
        final CheckBox chkLate = (CheckBox)findViewById(R.id.chkGetLate);
        final CheckBox chkQtyNotGood = (CheckBox) findViewById(R.id.chkGetNotGoodQty);
        final CheckBox chkItemNotGood = (CheckBox)findViewById(R.id.chkGetNotGoodItem);
        if (chkLate.isChecked()){
            VarIsLate = "1";
        }else{
            VarIsLate = "0";
        }
        if (chkQtyNotGood.isChecked()){
            VarIsQtyNotGood = "1";
        }else{
            VarIsQtyNotGood = "0";
        }
        if (chkItemNotGood.isChecked()){
            VarIsItemNotGood = "1";
        }else{
            VarIsItemNotGood = "0";
        }
        RatingBar simpleRatingBar = (RatingBar) findViewById(R.id.repRatingBar);
        VarRate = String.valueOf(Math.round(simpleRatingBar.getRating()));
        ImageView imgCurrentSearch = (ImageView)findViewById(R.id.imgCurrentSearch);
        imgCurrentSearch.setVisibility(View.VISIBLE);
        new GetSearchResult().execute();
    }
    public void OpenDetails(View v){
        ImageView btn = ((ImageView) v);
        final String[] cuurentValues = btn.getTag().toString().split(",&,");

        final Dialog dialog = new Dialog(RatingReport.this);

        dialog.setTitle("معلومات");


        Rect displayRectangle = new Rect();
        Window window = RatingReport.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        LayoutInflater inflater = (LayoutInflater)RatingReport.this.getSystemService(RatingReport.this.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.search_info_dialog, null);
        layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int)(displayRectangle.height() * 0.4f));
        dialog.setContentView(layout);
        TextView txtNotes = (TextView) dialog.findViewById(R.id.txtDNote);
        TextView txtDLate = (TextView) dialog.findViewById(R.id.txtDLate);
        TextView txtDQNG = (TextView) dialog.findViewById(R.id.txtDQNG);
        TextView txtDING = (TextView) dialog.findViewById(R.id.txtDING);
        txtNotes.setText(cuurentValues[0]);
        if (cuurentValues[1].equals("True")){
            txtDLate.setText("نعم");
        }else{
            txtDLate.setText("لا");
        }
        if (cuurentValues[2].equals("True")){
            txtDQNG.setText("نعم");
        }else{
            txtDQNG.setText("لا");
        }
        if (cuurentValues[3].equals("True")){
            txtDING.setText("نعم");
        }else{
            txtDING.setText("لا");
        }
        dialog.show();
    }
    class GetAllCustomerTask extends AsyncTask<Integer, Integer, String> {
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
            Spinner ddlAllCustomers = (Spinner)findViewById(R.id.spCustomers);
            List<StringWithTag> AllCustomers = new ArrayList<StringWithTag>();
            AllCustomers.add(new StringWithTag("جميع العملاء", "0"));
            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                if (reponseArrayString.length == 3){
                    AllCustomers.add(new StringWithTag(reponseArrayString[1].toString(),reponseArrayString[2].toString()));
                }
            }
            ArrayAdapter<StringWithTag> spLocationAdapter = new ArrayAdapter<StringWithTag>(RatingReport.this, android.R.layout.simple_spinner_dropdown_item, AllCustomers);
            ddlAllCustomers.setAdapter(spLocationAdapter);

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    class GetSearchResult extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                SoapObject request = new SoapObject(NAMESPACE, "getComplaints");

                request.addProperty("VarCustID", VarCustomerAccNumber);
                request.addProperty("VarRate", VarRate);
                request.addProperty("VarIsLate", VarIsLate);
                request.addProperty("VarIsQtyNotGood", VarIsQtyNotGood);
                request.addProperty("VarIsItemNotGood", VarIsItemNotGood);
                request.addProperty("VarFromDate", varFromDate);
                request.addProperty("VarToDate", varToDate);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call("http://37.224.24.195/getComplaints", envelope);
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
            ListView listView=(ListView)findViewById(R.id.lvSearchResults);
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdaptersSearch adapter=new ListViewAdaptersSearch(RatingReport.this, list);
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
                temp.put(CUS_SIXTH_COLUMN, reponseArrayString[5]);
                temp.put(CUS_SEVENTH_COLUMN, reponseArrayString[6]);
                list.add(temp);
            }

            adapter=new ListViewAdaptersSearch(RatingReport.this, list);
            listView.setAdapter(adapter);
            ImageView imgCurrentSearch = (ImageView)findViewById(R.id.imgCurrentSearch);
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
