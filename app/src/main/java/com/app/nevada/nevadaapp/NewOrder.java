package com.app.nevada.nevadaapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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


import static com.app.nevada.nevadaapp.Constants.AR_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.R.drawable.first;

public class NewOrder extends AppCompatActivity {
    private ArrayList<HashMap<String, String>> list;
    private String VarDate;
    private DatePickerDialog fromDatePickerDialog;
    private TextView txtItemDate;
    private TextView txtOrderDate;
    SoapObject responseObject;
    String[] reponseArrayString1;
    private String VarOrderNo = "N";
    private Integer VarItemNo = 0;
    private String VarAccNo = "";
    private Integer VarStationNo = 0;
    private String VarTotal = "";
    Integer VarTotNoOfOrders = 0;
    Integer VarMaxNoOfOrders = 0;
    private String VarTypeItemOrder = "";
    private String VarQuntityOrder = "";
    private String ExpectedTime = "";
    private String OrderNumber = "";
    private String VarTimeOrder = "";
    private String VarTotOrderItem = "";
    private String VarTotOrderTrans = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale local = Locale.getDefault();
        String language = String.valueOf(local);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        VarAccNo = intent.getStringExtra("acctNo");

        EditText etItemTime = (EditText) findViewById(R.id.etItemTime);
        etItemTime.setInputType(InputType.TYPE_NULL);


        findViewsById();
        setDateTimeField();

        Spinner ddItems = (Spinner) findViewById(R.id.spItemName);
        String[] items = new String[]{"الرجاء اختيار مادة", "بنزين 95", "بنزين 91", "ديزل", "زيت خام", "كايروسين"};
        String[] itemss = new String[]{"Please select subject", "Gasoline 95", "Gasoline 91", "diesel", "Crude Oil", "Kerosene"};
        ArrayAdapter<String> spItemsAdapter;
        if (language.contains("en")) {
            spItemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemss);

        } else {
            spItemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        }


        ddItems.setAdapter(spItemsAdapter);
        ddItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {


                VarItemNo = pos;
                Spinner ddItems = (Spinner) findViewById(R.id.spItemName);
                if (VarItemNo > 0) {

                    ddItems.setBackgroundResource(R.drawable.sp_arrow);
                    if (VarDate != null) {
                        EditText etItemTime = (EditText) findViewById(R.id.etItemTime);
                        etItemTime.setText("");
                        new GetAramcoTask().execute();
                    }

                } else {
                    ddItems.setSelection(1);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        Spinner ddLocations = (Spinner) findViewById(R.id.spLocation);
        ddLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Spinner ddLocations = (Spinner) findViewById(R.id.spLocation);
                if (pos > 0) {
                    StringWithTag sel = (StringWithTag) parent.getItemAtPosition(pos);
                    VarStationNo = Integer.parseInt(sel.tag.toString());
                    //Toast.makeText(NewOrder.this, VarStationNo.toString(),Toast.LENGTH_SHORT).show();

                    ddLocations.setBackgroundResource(R.drawable.sp_arrow);
                } else {
                    ddLocations.setSelection(1);
                    StringWithTag sel = (StringWithTag) parent.getItemAtPosition(pos);
                    VarStationNo = Integer.parseInt(sel.tag.toString());
                }
                if (VarItemNo > 0) {
                    if (VarDate != null) {
                        new GetAramcoTask().execute();
                    }
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        new GetStationsTask().execute();
    }

    private void findViewsById() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        txtItemDate = (TextView) findViewById(R.id.etItemDate);
        txtItemDate.setInputType(InputType.TYPE_NULL);
        txtOrderDate = (TextView) findViewById(R.id.etOrderDate);
        txtOrderDate.setInputType(InputType.TYPE_NULL);

        txtItemDate.setText("01-01-" + year);
        Calendar cal = Calendar.getInstance();
        CharSequence output = DateFormat.format("dd-MM-yyyy", cal);
        txtOrderDate.setText(output);

    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                CharSequence output = DateFormat.format("dd-MM-yyyy", cal);
                cal.add(Calendar.DATE, 1);
                Date selDate = getZeroTimeDate(cal.getTime());
                Date nowDate = getZeroTimeDate(new Date());

                if (new Date().after(selDate)) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                    String tomm = sdf1.format(c.getTime());

                    AlertDialog.Builder builder = new AlertDialog.Builder(NewOrder.this);
                    Locale local = Locale.getDefault();
                    String language = String.valueOf(local);
                    if (language.contains("en")) {
                        builder.setMessage("You can not select a date before" + tomm);
                    } else {
                        builder.setMessage("لا يمكنك اختيار تاريخ قبل  " + tomm);
                    }

                    builder.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }
                txtItemDate.setText(output);
                VarDate = txtItemDate.getText().toString();
                if (VarItemNo == 0) {
                    Locale local = Locale.getDefault();
                    String language = String.valueOf(local);
                    if (language.contains("en")) {
                        Toast.makeText(NewOrder.this, "please select subject", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewOrder.this, "الرجاء اختيار مادة", Toast.LENGTH_SHORT).show();
                    }

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setStroke(5, Color.RED);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Spinner ddItems = (Spinner) findViewById(R.id.spItemName);
                        ddItems.setBackground(drawable);
                    }
                } else {
                    EditText etItemTime = (EditText) findViewById(R.id.etItemTime);
                    etItemTime.setText("");
                    new GetAramcoTask().execute();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());


    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public void setOrder(View View) {
        if (checkValid()) {
            new GetTotOrderTask().execute();
            new GettotOrderItem().execute();
            new GettotOrderTrans().execute();
        }
    }

    public boolean checkValid() {
        boolean valid = true;
        if (VarItemNo == 0) {
            Locale local = Locale.getDefault();
            String language = String.valueOf(local);
            if (language.contains("en")) {
                Toast.makeText(NewOrder.this, "please select subject", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NewOrder.this, "الرجاء اختيار مادة", Toast.LENGTH_SHORT).show();
            }

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(5, Color.RED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Spinner ddItems = (Spinner) findViewById(R.id.spItemName);
                ddItems.setBackground(drawable);
            }
            return false;

        }
        if (VarOrderNo == "N") {
            Locale local = Locale.getDefault();
            String language = String.valueOf(local);
            if (language.contains("en")) {
                Toast.makeText(NewOrder.this, "Please choose one of Aramco orders", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NewOrder.this, "الرجاء اختيار واحدة من طلبيات ارامكو", Toast.LENGTH_SHORT).show();
            }

            return false;
        }
        Spinner ddLocation = (Spinner) findViewById(R.id.spLocation);
        Integer sel = ddLocation.getSelectedItemPosition();
        if (sel == 0) {
            Locale local = Locale.getDefault();
            String language = String.valueOf(local);
            if (language.contains("en")) {
                Toast.makeText(NewOrder.this, "Please select a dump station", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NewOrder.this, "الرجاء اختيار محظة تفريغ", Toast.LENGTH_SHORT).show();
            }

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(5, Color.RED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ddLocation.setBackground(drawable);
            }
            return false;
        }


        return valid;
    }

    public void callDate(View view) {
        fromDatePickerDialog.show();
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

    class GetAramcoTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "getAramcoOrders";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/getAramcoOrders";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("VarProductNo", VarItemNo);
                request.addProperty("VarDate", VarDate);
                request.addProperty("VarStation", VarStationNo);
                request.addProperty("VarAccNo", VarAccNo);

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
            ListView listView = (ListView) findViewById(R.id.lvAramcoOrders);
            list = new ArrayList<HashMap<String, String>>();
            ListViewAdapterAramcoOrders adapter = new ListViewAdapterAramcoOrders(NewOrder.this, list);
            listView.setAdapter(adapter);
            boolean availableOrders = false;
            for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                reponseArrayString1 = responseObject.getProperty(i).toString().split("&&");
                HashMap<String, String> temp = new HashMap<String, String>();
                int count = reponseArrayString1.length;
                temp.put(AR_FIRST_COLUMN, reponseArrayString1[2]);
                temp.put(AR_SECOND_COLUMN, reponseArrayString1[9]);//GetItemName(Integer.parseInt(reponseArrayString[4])));
                temp.put(AR_THIRD_COLUMN, reponseArrayString1[5]);
                temp.put(AR_FOURTH_COLUMN, reponseArrayString1[7]);
                temp.put("OrderType", reponseArrayString1[4]);
                temp.put("VarTimeOrder", reponseArrayString1[6]);


                VarMaxNoOfOrders = Integer.parseInt(reponseArrayString1[10].toString());
                VarTotNoOfOrders = Integer.parseInt(reponseArrayString1[11].toString());
                list.add(temp);
                availableOrders = true;
            }
            if (!availableOrders) {
                Button btnSetOrder = (Button) findViewById(R.id.SetOrder);
                btnSetOrder.setEnabled(false);
                Locale local = Locale.getDefault();
                String language = String.valueOf(local);
                if (language.contains("en")) {
                    btnSetOrder.setText("No requests available");
                } else {
                    btnSetOrder.setText("لا يوجد طلبات متاحة");
                }

            } else if (VarTotNoOfOrders >= VarMaxNoOfOrders) {
                Button btnSetOrder = (Button) findViewById(R.id.SetOrder);
                btnSetOrder.setEnabled(false);
                Locale local = Locale.getDefault();
                String language = String.valueOf(local);
                if (language.contains("en")) {
                    btnSetOrder.setText("You have exceeded the daily limit for daily orders, please check the administration");
                } else {
                    btnSetOrder.setText("لقد تجاوزت الحد الاعلى للطلبيات اليومية, يرجى مراجعة الادارة");
                }

            } else {
                Button btnSetOrder = (Button) findViewById(R.id.SetOrder);
                btnSetOrder.setEnabled(true);
                Locale local = Locale.getDefault();
                String language = String.valueOf(local);
                if (language.contains("en")) {
                    btnSetOrder.setText("Select");
                } else {
                    btnSetOrder.setText("أطلب");
                }

            }
            adapter = new ListViewAdapterAramcoOrders(NewOrder.this, list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    TextView txtOrderNo = (TextView) view.findViewById(R.id.lblAramcoNo);
                    VarOrderNo = txtOrderNo.getText().toString();
                    String[] tagOrder = txtOrderNo.getTag().toString().split(",");
                    ExpectedTime = tagOrder[0].toString();
                    VarTimeOrder = tagOrder[2];
                    VarQuntityOrder = tagOrder[3];
                    String VarTime = txtOrderNo.getTag().toString();
                    EditText etItemTime = (EditText) findViewById(R.id.etItemTime);

                    String time = VarTime;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
                    try {
                        Date date = dateFormat.parse(time);
                        String out = dateFormat2.format(date);
                    } catch (ParseException e) {
                    }
                    etItemTime.setText(VarTime.substring(11, 21));
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

    class GetStationsTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "getStations";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/getStations";

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

            Spinner ddLocation = (Spinner) findViewById(R.id.spLocation);
            List<StringWithTag> locations = new ArrayList<StringWithTag>();
            Locale locale = Locale.getDefault();
            String language = String.valueOf(locale);
            if (language.contains("en")) {
                locations.add(new StringWithTag("please select station", "0"));

            } else {
                locations.add(new StringWithTag("الرجاء اختيار محطة", "0"));

            }
            for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                String[] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                locations.add(new StringWithTag(reponseArrayString[1].toString(), reponseArrayString[0].toString()));
                //VarMaxNoOfOrders = Integer.parseInt(reponseArrayString[2].toString());
                //VarTotNoOfOrders = Integer.parseInt(reponseArrayString[3].toString());
            }
            ArrayAdapter<StringWithTag> spLocationAdapter = new ArrayAdapter<StringWithTag>(NewOrder.this, android.R.layout.simple_spinner_dropdown_item, locations);
            ddLocation.setAdapter(spLocationAdapter);
            if (ddLocation.getCount() == 2) {
                ddLocation.setSelection(1);
            }
            //if (VarTotNoOfOrders >= VarMaxNoOfOrders){
            //    Button btnSetOrder = (Button)findViewById(R.id.SetOrder);
            //    btnSetOrder.setEnabled(false);
            //   btnSetOrder.setText("لقد تجاوزت الحد الاعلى للطلبيات, يرجى مراجعة الادارة");
            //}

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class UpdateAramcoTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "updateAramcoOrder";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/updateAramcoOrder";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("VarCustID", VarAccNo);
                request.addProperty("VarDeliveryStationNo1", VarStationNo);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(NewOrder.this);
            Locale local = Locale.getDefault();
            String language = String.valueOf(local);
            if (language.contains("en")) {
                builder.setMessage("Your order has been successfully processed, thank you ");
            } else {
                builder.setMessage("تمت عملية الطلب بنجاح, شكراً لك ");
            }
            builder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class GetTotOrderTask extends AsyncTask<Integer, Integer, String> {
        AlertDialog builder1;

        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "GetTotOrder";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/GetTotOrder";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("VarCustID", VarAccNo);
                request.addProperty("VarOrderNo", VarOrderNo);


                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                responseObject = (SoapObject) envelope.getResponse();
                VarTotal = (responseObject.getProperty(0).toString());
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

        public void onCancel() {
            builder1.dismiss();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class GettotOrderItem extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "GetTotOrderItem";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/GetTotOrderItem";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("VarCustID", VarAccNo);
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


            VarTotOrderItem = responseObject.getProperty(0).toString();

        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class GettotOrderTrans extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String METHOD_NAME = "GetTotOrderTrans";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                final String SOAP_ACTION = "http://37.224.24.195/GetTotOrderTrans";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("VarCustID", VarAccNo);
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
            VarTotOrderTrans = responseObject.getProperty(0).toString();
            final Dialog dialog = new Dialog(NewOrder.this);
            Rect displayRectangle = new Rect();
            Window window = NewOrder.this.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            LayoutInflater inflater = (LayoutInflater) NewOrder.this.getSystemService(NewOrder.this.LAYOUT_INFLATER_SERVICE);
            //LayoutInflater inflater = NewOrder.this.getLayoutInflater();

            final View v1 = inflater.inflate(R.layout.review_the_order, null);
            v1.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
            v1.setMinimumHeight((int) (displayRectangle.height() * 0.4f));
            dialog.setContentView(v1);
            final TextView dateOrder = (TextView) v1.findViewById(R.id.DateOrder);
            final TextView orderNumber = (TextView) v1.findViewById(R.id.orderNumber);
            final TextView timeExpected = (TextView) v1.findViewById(R.id.TimeExpected);
            final TextView qantity = (TextView) v1.findViewById(R.id.Quantity);

            final TextView typeOfOrder = (TextView) v1.findViewById(R.id.typeOforder);
            final TextView priceOfOrder = (TextView) v1.findViewById(R.id.priceOforder);
            final TextView transetionPrice = (TextView) v1.findViewById(R.id.transetionPrice);
            final TextView total = (TextView) v1.findViewById(R.id.total);
            try {
                orderNumber.setText(VarOrderNo);
                timeExpected.setText(VarTimeOrder);
                Locale locale = Locale.getDefault();
                String language =String.valueOf(locale);
                qantity.setText(VarQuntityOrder);
                switch (reponseArrayString1[4]) {
                    case "1":
                        if (language.contains("en")){
                            typeOfOrder.setText("Gasoline 95");
                        }else{typeOfOrder.setText("بنزين 95");}
                        break;
                    case "2":
                        if (language.contains("en")){
                            typeOfOrder.setText("Gasoline 91");
                        }else{ typeOfOrder.setText("بنزين 91");}
                        break;
                    case "3":
                        if (language.contains("en")){
                            typeOfOrder.setText("diesel");
                        }else{ typeOfOrder.setText("ديزل");}
                        break;
                    case "4":
                        if (language.contains("en")){
                            typeOfOrder.setText("Crude Oil");
                        }else{ typeOfOrder.setText("زيت خام");}
                        break;
                    case "5":
                        if (language.contains("en")){
                            typeOfOrder.setText("Kerosene");
                        }else{typeOfOrder.setText("كيروسين");}

                        break;
                    default:
                        if (language.contains("en")){
                            typeOfOrder.setText("Gasoline 95");
                        }else{ typeOfOrder.setText("بنزين 95");}


                }
                String dateOrderTime = ExpectedTime.substring(0, ExpectedTime.length() - 11);
                dateOrder.setText(dateOrderTime);
                priceOfOrder.setText(VarTotOrderItem);
                transetionPrice.setText(VarTotOrderTrans);
                total.setText(VarTotal);


            } catch (Exception e) {

            }
            Button noSubmit = (Button) v1.findViewById(R.id.noSubmit);
            noSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button Submit = (Button) v1.findViewById(R.id.submet);
            Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpdateAramcoTask().execute();

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

}
