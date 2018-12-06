package com.app.nevada.nevadaapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Locale;

public class CustomerInfo extends AppCompatActivity {
    private static final String METHOD_NAME = "getCustomerInfo";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/getCustomerInfo";
    private  String VarCustomerNo = "";
    private  String accNameAr ="";
    private  String accNameEn ="";
    private  String accNo  ="";
    private  String accCountry  ="";
    private  String accCity  ="";
    private  String accStartBalance ="" ;
    private  String accEndBalance  ="";
    private  String accDate  ="";
    private  String accSalesMan  ="";
    private  String accTranType  ="";
    private  String accPhoneNo  ="";
    private  String accPoBox  ="";
    private  String accAddress  ="";
    private  String accEmail  ="";
    private  String accCustMax  ="";
    private  String accChkMax  ="";
    private  String accMaxOk ="0";
    private  boolean isMaxOk = false;
     String language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // String language= String.valueOf(locale);
        setContentView(R.layout.activity_customer_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        VarCustomerNo = intent.getStringExtra("varCustNo");
        language= intent.getStringExtra("lang");
        final Locale  locale = new Locale(language);
        forceLocale(this,locale);




        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (language.contains("en")){
            tabLayout.addTab(tabLayout.newTab().setText("Account Information").setTag("1"));
            tabLayout.addTab(tabLayout.newTab().setText("Deletion information").setTag("2"));
            tabLayout.addTab(tabLayout.newTab().setText("customer information").setTag("3"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }else{
        tabLayout.addTab(tabLayout.newTab().setText("معلومات الحساب").setTag("1"));
        tabLayout.addTab(tabLayout.newTab().setText("معلومات الذمة").setTag("2"));
        tabLayout.addTab(tabLayout.newTab().setText("معلومات العميل").setTag("3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);}

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //Toast.makeText(CustomerInfo.this, tab.getText(), Toast.LENGTH_SHORT).show();
                int tagValue = Integer.parseInt(tab.getTag().toString());
                if (tagValue == 1)
                {
                    TextView txtViewObject = (TextView)findViewById(R.id.tab1_lblAccNoData);
                    txtViewObject.setText(accNo);
                    txtViewObject = (TextView)findViewById(R.id.tab1_lblAccFinalBalanceData);
                    double totalVal = Double.parseDouble(accEndBalance);
                    if (totalVal <0)
                    {
                        totalVal = totalVal*-1;
                        txtViewObject.setTextColor(Color.RED);
                        Locale locale2=new Locale("EN-US");
                        txtViewObject.setText( "(" + String.format(locale2,"%.2f", totalVal) + ")");
                    }else{
                        Locale locale1=new Locale("EN-US");
                        txtViewObject.setText( String.format(locale1,"%.2f", totalVal));
                    }
                    txtViewObject = (TextView)findViewById(R.id.tab1_lblAccDateData);
                    txtViewObject.setText(accDate);
                    txtViewObject = (TextView)findViewById(R.id.tab1_lblStartBalanceData);
                    txtViewObject.setText(accStartBalance);
                    txtViewObject = (TextView)findViewById(R.id.tab1_lblTranTypesData);
                    txtViewObject.setText(accTranType);
                } else if (tagValue == 2)
                {
                    TextView txtViewObject = (TextView)findViewById(R.id.tab2_lblAccMaxData);
                    txtViewObject.setText(accCustMax);
                    txtViewObject = (TextView)findViewById(R.id.tab2_lblChkMaxData);
                    txtViewObject.setText(accChkMax);
                    CheckBox chkObject = (CheckBox)findViewById(R.id.chkAllow);
                    if(accMaxOk.equals("True")){
                        boolean checked = true;
                        chkObject.setChecked(checked);
                    } else {
                        boolean checked = false;
                        chkObject.setChecked(checked);
                    }
                }
                else {
                    TextView txtViewObject = (TextView)findViewById(R.id.tab3_lblCountryData);
                    txtViewObject.setText(accCountry);
                    txtViewObject = (TextView)findViewById(R.id.tab3_lblCityData);
                    txtViewObject.setText(accCity);
                    txtViewObject = (TextView)findViewById(R.id.tab3_lblPhoneNoData);
                    txtViewObject.setText(accPhoneNo);
                    txtViewObject = (TextView)findViewById(R.id.tab3_lblPoOfficeData);
                    txtViewObject.setText(accPoBox);
                    txtViewObject = (TextView)findViewById(R.id.tab3_lblAddressData);
                    txtViewObject.setText(accAddress);
                    txtViewObject = (TextView)findViewById(R.id.tab3_lblEmailData);
                    txtViewObject.setText(accEmail);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Toast.makeText(CustomerInfo.this, tab.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        new getUserDataTask("","").execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateUser (View v){
        TextView txtViewObject = (TextView)findViewById(R.id.tab2_lblAccMaxData);
        accCustMax =  txtViewObject.getText().toString();
        txtViewObject = (TextView)findViewById(R.id.tab2_lblChkMaxData);
        accChkMax =  txtViewObject.getText().toString();
        CheckBox chkViewObject = (CheckBox) findViewById(R.id.chkAllow);
        isMaxOk =  chkViewObject.isChecked();
        new updateUserDataTask("","").execute();
    }
    public class getUserDataTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        getUserDataTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("C_No", VarCustomerNo);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                Object result = (Object) envelope.getResponse();
                SoapObject envResult = (SoapObject)envelope.bodyIn;
                SoapObject root = (SoapObject) envResult.getProperty(0);
                int count = root.getPropertyCount();
                if (count == 0)
                {
                    return false;
                }
                String AccName = root.getProperty(1).toString();


                if(AccName == "")
                    return false;
                else {
                    accNameAr = root.getProperty(1).toString();
                       accNameEn =root.getProperty(2).toString();
                       accNo  =root.getProperty(6).toString();
                       accCountry  =root.getProperty(12).toString();
                       accCity  =root.getProperty(11).toString();
                       accStartBalance =root.getProperty(13).toString();
                       accEndBalance  =root.getProperty(0).toString();
                       accDate  =root.getProperty(7).toString();
                        String typeNo = root.getProperty(14).toString();
                        if(typeNo.equals("1")){
                            accTranType = "مدين";
                        } else if (typeNo.equals("2")){
                            accTranType = "دائن";
                        }else {
                            accTranType = "مدين ودائن";
                        }
                       accPhoneNo  =root.getProperty(3).toString();
                       accPoBox  =root.getProperty(4).toString();
                        if (accPoBox.equals("anyType{}")){
                            accPoBox = "-";
                        }
                       accAddress  =root.getProperty(5).toString();
                    if (accAddress.equals("anyType{}")){
                        accAddress = "-";
                    }
                       accEmail  =root.getProperty(8).toString();
                    if (accEmail.equals("" +
                            "" +
                            "" +
                            "")){
                        accEmail = "-";
                    }
                       accCustMax  =root.getProperty(16).toString();
                    if (accCustMax.equals("anyType{}")){
                        accCustMax = "-";
                    }
                       accChkMax  =root.getProperty(17).toString();
                    if (accChkMax.equals("anyType{}")){
                        accChkMax = "-";
                    }
                       accMaxOk =root.getProperty(15).toString();

                    return true;
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }


            // TODO: register the new account here.

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                TextView txtViewObject = (TextView)findViewById(R.id.tab1_lblAccNoData);
                txtViewObject.setText(accNo);
                TextView txtEndBal = (TextView)findViewById(R.id.tab1_lblAccFinalBalanceData);
                Locale locale2=new Locale("EN-US");
                double totalVal = Double.parseDouble(accEndBalance);
                if (totalVal <0)
                {
                    totalVal = totalVal*-1;
                    txtEndBal.setTextColor(Color.RED);
                    txtEndBal.setText( "(" + String.format(locale2,"%.2f", totalVal) + ")");
                }else{
                    txtEndBal.setText( String.format(locale2,"%.2f", totalVal));
                }
                txtViewObject = (TextView)findViewById(R.id.tab1_lblAccDateData);
                txtViewObject.setText(accDate);
                txtViewObject = (TextView)findViewById(R.id.tab1_lblStartBalanceData);
                txtViewObject.setText(accStartBalance);
                txtViewObject = (TextView)findViewById(R.id.tab1_lblTranTypesData);
                txtViewObject.setText(accTranType);
                TextView txtViewObj = (TextView)findViewById(R.id.custNameAr);
                txtViewObj.setText(accNameAr);
                txtViewObj = (TextView)findViewById(R.id.custNameEn);
                txtViewObj.setText(accNameEn);

            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class updateUserDataTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;


        updateUserDataTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                String METHOD_NAME_Update = "updateCustomerInfo";
                String SOAP_ACTION_Update = "http://37.224.24.195/updateCustomerInfo";

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_Update);

                request.addProperty("C_No", VarCustomerNo);
                request.addProperty("C_Max", accCustMax);
                request.addProperty("C_ChkMax", accChkMax);
                request.addProperty("C_CanMax", isMaxOk);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION_Update, envelope);
                Object result = (Object) envelope.getResponse();
                SoapObject envResult = (SoapObject)envelope.bodyIn;
                SoapObject root = (SoapObject) envResult.getProperty(0);
                return true;

            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }


            // TODO: register the new account here.

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if(language.equals("en")){
                    Toast.makeText(CustomerInfo.this, "Data updated", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CustomerInfo.this, "تم تحديث البيانات", Toast.LENGTH_SHORT).show();
                }
            } else {
                if(language.equals("en")){
                    Toast.makeText(CustomerInfo.this, "Data is not updated", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CustomerInfo.this, "لم يتم تحديث البيانات", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        protected void onCancelled() {

        }
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
}
