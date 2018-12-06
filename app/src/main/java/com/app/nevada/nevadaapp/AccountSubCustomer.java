package com.app.nevada.nevadaapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;

public class AccountSubCustomer extends AppCompatActivity {
    private String endBalanceVal = "";
    private String varAcctNo = "";
    private String varIsAdmin = "";
    String languges;
    private TextView cusName, work_orders_closed, cusAccun, work_order, tv_balance_transfered, endbalance_customer;
    SoapObject responseObject;
    String varCustName;
    Double final_balane=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_sub_customer);
        Intent intent = getIntent();
        endBalanceVal = intent.getStringExtra("endBalance");
        varAcctNo = intent.getStringExtra("acctNo");
        varIsAdmin = intent.getStringExtra("isAdmin");
        languges = intent.getStringExtra("lang");
        varCustName = intent.getStringExtra("varCustName");
        Locale local = new Locale(languges);
        forceLocale(this, local);
        findViewsByid();
        cusName.setText(varCustName);
        cusAccun.setText(varAcctNo);
        tv_balance_transfered.setText(endBalanceVal);
        final_balane +=Double.parseDouble(endBalanceVal);
        new CalcWorkOrder().execute();
        new CalcClosedWorkOrder().execute();

        //+ Integer.parseInt(work_orders_closed.getText().toString()) + Integer.parseInt(endBalanceVal);

    }

    public void findViewsByid() {
        cusName = (TextView) findViewById(R.id.cus_name);
        cusAccun = (TextView) findViewById(R.id.cus_acc);
        work_orders_closed = (TextView) findViewById(R.id.work_orders_closed);
        work_order = (TextView) findViewById(R.id.work_order);
        tv_balance_transfered = (TextView) findViewById(R.id.tv_balance_transfered);
        endbalance_customer = (TextView) findViewById(R.id.endbalance_customer);
    }


    public void Workorders(View view) {
        Intent myIntent = new Intent(AccountSubCustomer.this, WorkOrder.class);
        myIntent.putExtra("lang", languges);
        myIntent.putExtra("varCustName", varCustName);
        myIntent.putExtra("acctNo", varAcctNo);
        myIntent.putExtra("open",work_order.getText().toString());
        startActivity(myIntent);
    }

    public void ClosedWorkOrders(View view) {
        Intent myIntent = new Intent(AccountSubCustomer.this, ClosedWorkOrder.class);
        myIntent.putExtra("lang", languges);
        myIntent.putExtra("varCustName", varCustName);
        myIntent.putExtra("acctNo", varAcctNo);
        myIntent.putExtra("close",work_orders_closed.getText().toString());
        startActivity(myIntent);
    }

    public void goTobalanceactivity(View view) {
        Intent myIntent = new Intent(AccountSubCustomer.this, BalanceTransferredToAccounting.class);
        myIntent.putExtra("endBalance", endBalanceVal);
        myIntent.putExtra("varCustName", varCustName);
        myIntent.putExtra("acctNo", varAcctNo);
        myIntent.putExtra("isAdmin", varIsAdmin);
        myIntent.putExtra("lang", languges);


        AccountSubCustomer.this.startActivity(myIntent);
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

    public class CalcWorkOrder extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... integers) {
            try {
                String METHOD_NAME = "GetSumTotJobOrder";
                String NAMESPASE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/GetSumTotJobOrder";
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
            return "Test Complete";
        }

        @Override
        protected void onPostExecute(String s) {

            String arryofstring = responseObject.getProperty(0).toString();
            final_balane += Double.parseDouble(arryofstring.toString());

            work_order.setText(arryofstring);
        }

    }

    public class CalcClosedWorkOrder extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... integers) {
            try {
                String METHOD_NAME = "GetSumTotJobOrderClosed";
                String NAMESPASE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/GetSumTotJobOrderClosed";
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
            return "Test Complete";
        }

        @Override
        protected void onPostExecute(String s) {

            String arryofstring = responseObject.getProperty(0).toString();
            final_balane += Double.parseDouble(arryofstring);
            work_orders_closed.setText(arryofstring);
            endbalance_customer.setText(String.valueOf(final_balane));
        }

    }
}
