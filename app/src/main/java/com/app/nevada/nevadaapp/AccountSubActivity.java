package com.app.nevada.nevadaapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.SIXTH_COLUMN;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AccountSubActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final String METHOD_NAME = "getSubsidary";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/getSubsidary";
    private static final boolean AUTO_HIDE = true;
    private ArrayList<HashMap<String, String>> list;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private int varFrom = 0;
    private  int varTo = 150;
    private String endBalanceVal ="";
    private String varAcctNo ="";
    private String varIsAdmin ="";
    SoapObject responseObject;
    private String[] reponseList;
    private TextView fromDateEtxt;
    private TextView toDateEtxt;
    private TextView fromDepttxt;
    private TextView toDepttxt;
    private TextView fromCredittxt;
    private TextView toCredittxt;
    private String varFromDate ;
    private String varToDate ;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private float varDebtSum =0;
    private float varCreditSum =0;
    private float varRowsCount =0;
    private double varTotalVal = 0;
    private int debtFrom = 0;
    private int debtTo = 0;
    private int creditFrom = 0;
    private int creditTo = 0;
    private SimpleDateFormat dateFormatter;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.

        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                //actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

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

        setContentView(R.layout.activity_account_sub);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        Intent intent = getIntent();
        endBalanceVal= intent.getStringExtra("endBalance");
        varAcctNo =intent.getStringExtra("acctNo");
        varIsAdmin =intent.getStringExtra("isAdmin");
        String varCustName= intent.getStringExtra("varCustName");

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        findViewsById();

        setDateTimeField();

        TextView txtViewObject = (TextView)findViewById(R.id.txtSubCusAccName);
        txtViewObject.setText(varCustName);
        txtViewObject = (TextView)findViewById(R.id.txtSubCusAccNumber);
        txtViewObject.setText(varAcctNo);
        new subsidaryTask().execute();


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

        fromDepttxt = (TextView) findViewById(R.id.tranDebtFromFilter);
        toDepttxt = (TextView) findViewById(R.id.tranDebtToFilter);
        fromCredittxt = (TextView) findViewById(R.id.tranCreditFromFilter);
        toCredittxt = (TextView) findViewById(R.id.tranCreditToFilter);

        varFromDate = fromDateEtxt.getText().toString();
        varToDate = toDateEtxt.getText().toString();
    }
    private int GetDayOfMonth(int month)
    {
        int VarDayOfMonth = 0;

        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12 )
        {
            VarDayOfMonth = 31;
        }else if (month == 2) {
            VarDayOfMonth = 28;
        } else {
                VarDayOfMonth = 30;
        }


        return VarDayOfMonth;

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
    public void reFillList(View v)
    {
         debtFrom = Integer.parseInt(fromDepttxt.getText().toString());
         debtTo = Integer.parseInt(toDepttxt.getText().toString());
         creditFrom = Integer.parseInt(fromCredittxt.getText().toString());
         creditTo = Integer.parseInt(toCredittxt.getText().toString());

        new subsidaryTask().execute();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        fromDepttxt.clearFocus();
        toDepttxt.clearFocus();
        fromCredittxt.clearFocus();
        toCredittxt.clearFocus();
        Button btnSearch = (Button) findViewById(R.id.btnFillFilters);
        btnSearch.requestFocus();
        ArrayList<HashMap<String, String>> tempArrayList = new ArrayList<HashMap<String, String>> ();
        for(int i=0;i<responseObject.getPropertyCount();i++){
            boolean canAdd = true;
            String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
            HashMap<String,String> temp=new HashMap<String, String>();
            int count = reponseArrayString.length;
            String ts=reponseArrayString[2];
            Float t =Float.parseFloat(reponseArrayString[2]);
            Spinner tranTypeFilter = (Spinner) findViewById(R.id.tranTypeFilter);

            String selected = tranTypeFilter.getSelectedItem().toString();
            if (!selected.equals("جميع الحركات")){
                if (!selected.equals(reponseArrayString[0])){
                    canAdd = false;
                    continue;
                }
            }
            if (canAdd)
            {
                temp.put(FIRST_COLUMN, reponseArrayString[0]);
                String dateAllF = convertFromArabic(reponseArrayString[4].toString());
                String dateFinalF = convertFromArabic(dateAllF.substring(0, Math.min(dateAllF.length(), 10)).replace('/','-'));
                temp.put(FOURTH_COLUMN, convertFromArabic(dateFinalF));
                temp.put(THIRD_COLUMN,  convertFromArabic(String.format("%.2f", Double.parseDouble(reponseArrayString[3]))));
                temp.put(SECOND_COLUMN, convertFromArabic(String.format("%.2f", Double.parseDouble(reponseArrayString[2]))));
                temp.put(FIFTH_COLUMN, convertFromArabic(reponseArrayString[1]));
                temp.put(SIXTH_COLUMN, convertFromArabic(reponseArrayString[5]));
                tempArrayList.add(temp);
            }
        }

        ListView listView=(ListView)findViewById(R.id.listView1);
        ListViewAdapters adapter=new ListViewAdapters(AccountSubActivity.this, tempArrayList);
        listView.setAdapter(adapter);
    }
    public void callDate(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if(view == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            //hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);*/
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
    public void goFirst(View v)
    {
        varFrom = 0;
        varTo  = 0;
        new subsidaryTask().execute();
    }
    public void goLast(View v)
    {
        varFrom = 0;
        varTo  = 1;
        new subsidaryTask().execute();
    }
    public void goNext(View v)
    {
        varFrom = varFrom + 150;
        varTo  = varFrom + 150;
        new subsidaryTask().execute();
    }
    public void goBack(View v)
    {
        varFrom = varFrom - 150;
        varTo  = varTo - 150;
        new subsidaryTask().execute();
    }
    public void relode(View v)
    {
        varFrom = 0;
        varTo  = 150;
        new subsidaryTask().execute();
    }
    public String convertFromArabic(String value)
    {
        String newValue = value.replaceAll("٫", ".").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0");
        return newValue;
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    class subsidaryTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("AcctNo", varAcctNo);
                request.addProperty("from", varFrom);
                request.addProperty("to", varTo);

                request.addProperty("dateFrom", varFromDate);
                request.addProperty("dateTo", varToDate);
    
                request.addProperty("fromDE", debtFrom);
                request.addProperty("toDE", debtTo);
                request.addProperty("fromCr", creditFrom);
                request.addProperty("toCr", creditTo);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                responseObject = (SoapObject) envelope.getResponse();
                Integer x = 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
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
            ListView listView=(ListView)findViewById(R.id.listView1);
            //ArrayList<HashMap<String,String>> tempList=new ArrayList<HashMap<String,String>>();
            //tempList = list;
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdapters adapter=new ListViewAdapters(AccountSubActivity.this, list);
            listView.setAdapter(adapter);
            List<String> spinnerArray =  new ArrayList<String>();
            spinnerArray.add("جميع الحركات");
            boolean exist = false;

            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String,String> temp=new HashMap<String, String>();
                int count = reponseArrayString.length;
                for(int x=0;x<spinnerArray.size();x++){
                    if(spinnerArray.get(x).toString().equals(convertFromArabic(reponseArrayString[0]))){
                        exist = true;
                    }
                }
                if(!exist){
                    spinnerArray.add(convertFromArabic(reponseArrayString[0]));
                }
                exist = false;
                //varDebtSum += Double.parseDouble(reponseArrayString[2]);
                //varCreditSum += Double.parseDouble(reponseArrayString[3]);
                String dateAll = convertFromArabic(reponseArrayString[4]).toString();
                String dateFinal = dateAll.substring(0, Math.min(dateAll.length(), 10)).replace('/','-');
                temp.put(FIRST_COLUMN, convertFromArabic(reponseArrayString[0].replace("1","")));
                temp.put(FOURTH_COLUMN, dateFinal);
                temp.put(THIRD_COLUMN,convertFromArabic(String.format("%.2f", Double.parseDouble((reponseArrayString[3])))));
                temp.put(SECOND_COLUMN, convertFromArabic(String.format("%.2f", Double.parseDouble((reponseArrayString[2])))));
                temp.put(FIFTH_COLUMN, convertFromArabic(reponseArrayString[1]));
                temp.put(SIXTH_COLUMN, convertFromArabic(reponseArrayString[5]));
                list.add(temp);
                TextView txtNoTrans=(TextView)findViewById(R.id.sumTranDebt);
                txtNoTrans.setText( String.format("%.2f", Double.parseDouble(convertFromArabic(reponseArrayString[6]))));
                txtNoTrans=(TextView)findViewById(R.id.sumTranCredit);
                double crVal = Double.parseDouble(convertFromArabic(reponseArrayString[7]));
                if (crVal == 0){
                    txtNoTrans.setText( String.format("%.2f", Double.parseDouble(convertFromArabic(reponseArrayString[7]))));
                }
                else{
                    txtNoTrans.setText("(" + String.format("%.2f", Double.parseDouble(convertFromArabic(reponseArrayString[7])))+ ")");
                }

                txtNoTrans=(TextView)findViewById(R.id.sumTotalBal);
                double totalVal = Double.parseDouble(convertFromArabic(reponseArrayString[8]));
                varTotalVal =totalVal;
                if (totalVal <0)
                {
                    totalVal = totalVal*-1;
                    txtNoTrans.setTextColor(Color.RED);
                    String Val =String.format("%.2f", Double.parseDouble(convertFromArabic(String.valueOf(totalVal))));
                    txtNoTrans.setText( "(" + Val + ")");
                }else{
                    txtNoTrans.setText( String.format("%.2f", Double.parseDouble(convertFromArabic(reponseArrayString[8]))));
                }
                txtNoTrans=(TextView)findViewById(R.id.txtFinalString);
                Locale  locale = Locale.getDefault();
                String language= String.valueOf(locale);
                if (language.contains("en")){
                    txtNoTrans.setText("End Balance");
                }else{
                txtNoTrans.setText("الـرصـيــد الـنـهــائـي");}
                int rowsCount = Integer.parseInt(convertFromArabic(reponseArrayString[9]));
                varRowsCount =rowsCount;
                if (rowsCount < 10)
                {
                    ImageView imgObj = (ImageView)findViewById(R.id.btnfirst);
                    imgObj.setAlpha(0.3F);
                    imgObj.setEnabled(false);
                    imgObj = (ImageView)findViewById(R.id.btnBack);
                    imgObj.setAlpha(0.3F);
                    imgObj.setEnabled(false);
                    imgObj = (ImageView)findViewById(R.id.btnNext);
                    imgObj.setAlpha(0.3F);
                    imgObj.setEnabled(false);
                    imgObj = (ImageView)findViewById(R.id.btnlast);
                    imgObj.setAlpha(0.3F);
                    imgObj.setEnabled(false);
                }else{
                    ImageView imgObj = (ImageView)findViewById(R.id.btnfirst);
                    imgObj.setAlpha(1F);
                    imgObj.setEnabled(true);
                    imgObj = (ImageView)findViewById(R.id.btnBack);
                    imgObj.setAlpha(1F);
                    imgObj.setEnabled(true);
                    imgObj = (ImageView)findViewById(R.id.btnNext);
                    imgObj.setAlpha(1F);
                    imgObj.setEnabled(true);
                    imgObj = (ImageView)findViewById(R.id.btnlast);
                    imgObj.setAlpha(1F);
                    imgObj.setEnabled(true);
                }

                /*else{
                    ImageView imgObj = (ImageView)findViewById(R.id.btnfirst);
                    if (varFrom == 0)
                    {
                        imgObj.setAlpha(0.3F);
                        imgObj.setEnabled(false);
                        imgObj = (ImageView)findViewById(R.id.btnBack);
                        imgObj.setAlpha(0.3F);
                        imgObj.setEnabled(false);
                    }else{
                        imgObj = (ImageView)findViewById(R.id.btnNext);
                        imgObj.setAlpha(1F);
                        imgObj.setEnabled(true);
                        imgObj = (ImageView)findViewById(R.id.btnlast);
                        imgObj.setAlpha(1F);
                        imgObj.setEnabled(true);000
                    }
                }*/
            }
            if (list.size() <=0)
            {
                /*if (varTotalVal != 0)
                {
                    ImageView imgObjB = (ImageView)findViewById(R.id.btnfirst);
                    imgObjB = (ImageView)findViewById(R.id.btnBack);
                    imgObjB.setAlpha(1F);
                    imgObjB.setEnabled(true);
                    imgObjB = (ImageView)findViewById(R.id.btnfirst);
                    imgObjB.setAlpha(1F);
                    imgObjB.setEnabled(true);
                }*/
                listView.setVisibility(View.INVISIBLE);
                TextView txtNoTrans=(TextView)findViewById(R.id.txtNoTrans);
                txtNoTrans.setVisibility(View.VISIBLE);
                ImageView imgObj = (ImageView)findViewById(R.id.btnNext);
                imgObj.setAlpha(0.3F);
                imgObj.setEnabled(false);
                imgObj = (ImageView)findViewById(R.id.btnlast);
                imgObj.setAlpha(0.3F);
                imgObj.setEnabled(false);
                return;
            }

            listView.setVisibility(View.VISIBLE);
            TextView txtNoTrans=(TextView)findViewById(R.id.txtNoTrans);
            txtNoTrans.setVisibility(View.INVISIBLE);
            ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(AccountSubActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.tranTypeFilter);
            sItems.setAdapter(spAdapter);

            adapter=new ListViewAdapters(AccountSubActivity.this, list);
            listView.setAdapter(adapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id)
                {
                    if (varIsAdmin.equals("1")){
                        TextView txtTranType = (TextView)view.findViewById(R.id.tranType);
                        TextView txtTranDebt = (TextView)view.findViewById(R.id.tranDebt);
                        String varTranType = txtTranType.getTag().toString();
                        String varTranNo = txtTranDebt.getTag().toString();
                        Intent myIntent = new Intent(AccountSubActivity.this, LastTransActionActivity.class);
                        myIntent.putExtra("varSource","2");
                        myIntent.putExtra("varTranType",varTranType);
                        myIntent.putExtra("varTranNo",varTranNo);
                        AccountSubActivity.this.startActivity(myIntent);
                    }
                    return true;
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
