package com.app.nevada.nevadaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.AR_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SIXTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_THIRD_COLUMN;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String METHOD_NAME = "getAllCustomer";
    private static final String NAMESPACE = "http://37.224.24.195";
    private static final String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
    final String SOAP_ACTION = "http://37.224.24.195/getAllCustomer";
    private ArrayList<HashMap<String, String>> list;
    SoapObject responseObject;
    private String[] reponseList;
    private String endBalance = "";
    private String varCustNo = "";
    private String varCustAcctNo = "";
    private String varCustName = "";
    private String VarIsAdmin = "";
    private String varFilled = "0";
    private int VarOrdersToConfirm = 0;
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

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
               // emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               // emailIntent.setType("vnd.android.cursor.item/email");
               // emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"Mothana@awaelksa.com"});
               // emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                //startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
                Intent myIntent = new Intent(MainActivity.this, TestLayout.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        Intent intent = getIntent();
        String isAdmin =intent.getStringExtra("isAdmin");
        VarIsAdmin = isAdmin;
        View layout;

        if (isAdmin.equals("1")){
            layout = findViewById(R.id.mainAdminUserLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainDriverLayout);
            layout.setVisibility(View.INVISIBLE);
            NavigationView navigationView;
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.Messages).setVisible(true);
            nav_Menu.findItem(R.id.RatingReport).setVisible(true);
            nav_Menu.findItem(R.id.DailyTransactionReport).setVisible(true);
            nav_Menu.findItem(R.id.DriverImportantLocations).setVisible(false);
        } else if (isAdmin.equals("2")){
            layout = findViewById(R.id.mainAdminUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainDriverLayout);
            layout.setVisibility(View.VISIBLE);
            NavigationView navigationView;
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.Messages).setVisible(false);
            nav_Menu.findItem(R.id.RatingReport).setVisible(false);
            nav_Menu.findItem(R.id.DailyTransactionReport).setVisible(false);
            nav_Menu.findItem(R.id.DriverImportantLocations).setVisible(true);
        }else{
            layout = findViewById(R.id.mainAdminUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.mainDriverLayout);
            layout.setVisibility(View.INVISIBLE);
            NavigationView navigationView;
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.Messages).setVisible(false);
            nav_Menu.findItem(R.id.RatingReport).setVisible(false);
            nav_Menu.findItem(R.id.DailyTransactionReport).setVisible(false);
            nav_Menu.findItem(R.id.DriverImportantLocations).setVisible(false);
            DrawerLayout mDrawerLayout;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }
        layout = findViewById(R.id.ourClientsLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.ourProductsLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.supportLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.contactUsLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.quotationsLayout);
        layout.setVisibility(View.INVISIBLE);

        if (isAdmin.equals("1")){
            final EditText etSearchNo = (EditText)findViewById(R.id.etCusAccNo);
            final EditText etSearchName = (EditText)findViewById(R.id.etCusName);
            etSearchNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (responseObject != null){
                        int textlength = cs.length();
                        ListView myListView=(ListView)findViewById(R.id.lvCustomers);
                        myListView.clearChoices();
                        myListView.requestLayout();
                        LinearLayout ll = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
                        ll.setAlpha(0.3F);
                        varCustAcctNo = "";
                        varCustNo = "";
                        varCustName ="";


                        ArrayList<HashMap<String, String>> tempArrayList = new ArrayList<HashMap<String, String>> ();
                        for(int i=0;i<responseObject.getPropertyCount();i++){
                            String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                            HashMap<String,String> temp=new HashMap<String, String>();
                            int count = reponseArrayString.length;
                            if (reponseArrayString[2].contains(etSearchNo.getText()))
                            {
                                temp.put(CUS_SECOND_COLUMN, reponseArrayString[0]);
                                temp.put(CUS_FIRST_COLUMN, reponseArrayString[1]);
                                temp.put(CUS_THIRD_COLUMN, reponseArrayString[2]);
                                tempArrayList.add(temp);
                            }
                        }

                        ListView listView=(ListView)findViewById(R.id.lvCustomers);
                        ListViewAdaptersCustomer adapter=new ListViewAdaptersCustomer(MainActivity.this, tempArrayList);
                        listView.setAdapter(adapter);
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
            etSearchName.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (responseObject != null){
                        ListView myListView=(ListView)findViewById(R.id.lvCustomers);
                        myListView.clearChoices();
                        myListView.requestLayout();
                        LinearLayout ll = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
                        ll.setAlpha(0.3F);
                        varCustAcctNo = "";
                        varCustNo = "";
                        varCustName ="";

                        int textlength = cs.length();
                        ArrayList<HashMap<String, String>> tempArrayList = new ArrayList<HashMap<String, String>> ();
                        for(int i=0;i<responseObject.getPropertyCount();i++){
                            String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                            HashMap<String,String> temp=new HashMap<String, String>();
                            int count = reponseArrayString.length;
                            if (reponseArrayString[1].contains(etSearchName.getText()))
                            {
                                temp.put(CUS_SECOND_COLUMN, reponseArrayString[0]);
                                temp.put(CUS_FIRST_COLUMN, reponseArrayString[1]);
                                temp.put(CUS_THIRD_COLUMN, reponseArrayString[2]);
                                tempArrayList.add(temp);
                            }
                        }

                        ListView listView=(ListView)findViewById(R.id.lvCustomers);
                        ListViewAdaptersCustomer adapter=new ListViewAdaptersCustomer(MainActivity.this, tempArrayList);
                        listView.setAdapter(adapter);
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
            new subsidaryTask().execute();
        } else if (isAdmin.equals("2")) {
            TextView tvDriverName = (TextView)findViewById(R.id.lblDriverNameData);
            TextView tvDriverNo = (TextView)findViewById(R.id.lblDriverNoData);
            TextView tvCarNo = (TextView)findViewById(R.id.lblDriverCarData);
            tvDriverName.setText(intent.getStringExtra("accountName"));
            tvDriverNo.setText(intent.getStringExtra("accountNo"));
            tvCarNo.setText(intent.getStringExtra("accCity"));//Truck No
            varCustNo = intent.getStringExtra("accStartBalance"); // Driver TMS No
            new DriverOrdersTask().execute();
        }else
        {
            String accountName = intent.getStringExtra("accountName");
            String accountNo = intent.getStringExtra("accountNo");
            varCustAcctNo = intent.getStringExtra("accountNo");
            varCustName = accountName;
            TextView txtName = (TextView)findViewById(R.id.userAccName);
            TextView txtNo = (TextView)findViewById(R.id.userAccNo);
            TextView txtCity = (TextView)findViewById(R.id.lblBranchData);
            TextView txtDate = (TextView)findViewById(R.id.lblAccDateData);
            TextView txtStartB = (TextView)findViewById(R.id.lblStartBalanceData);
            TextView txtEndB = (TextView)findViewById(R.id.lblAccBalanceData);
            txtName.setText(accountName);
            txtNo.setText(accountNo);
            txtCity.setText(intent.getStringExtra("accCity"));
            txtDate.setText(intent.getStringExtra("accDate"));
            txtStartB.setText(intent.getStringExtra("accStartBalance"));
            double totalVal = Double.parseDouble(intent.getStringExtra("accEndBalance"));
            if (totalVal <0)
            {
                totalVal = totalVal*-1;
                txtEndB.setTextColor(Color.RED);
                txtEndB.setText( "(" + String.format("%.2f", totalVal) + ")");
            }else{
                txtEndB.setText( String.format("%.2f", totalVal));
            }
            //txtEndB.setText(intent.getStringExtra("accEndBalance"));
            endBalance = intent.getStringExtra("accEndBalance");



        }



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        endBalance = "";
        varCustNo = "";
        varCustAcctNo = "";
        varCustName = "";
        VarIsAdmin = "";
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(myIntent);
        finish();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        View layout;
       if (id == R.id.OurProducts) {
           layout = findViewById(R.id.mainAdminUserLayout);
           layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.ourClientsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.ourProductsLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.supportLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.contactUsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.quotationsLayout);
            layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.mainDriverLayout);
           layout.setVisibility(View.INVISIBLE);
        } else if (id == R.id.OurClients) {
           layout = findViewById(R.id.mainAdminUserLayout);
           layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.ourClientsLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.ourProductsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.supportLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.contactUsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.quotationsLayout);
            layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.mainDriverLayout);
           layout.setVisibility(View.INVISIBLE);
        } else if (id == R.id.Support) {
           layout = findViewById(R.id.mainAdminUserLayout);
           layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.ourClientsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.ourProductsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.supportLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.contactUsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.quotationsLayout);
            layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.mainDriverLayout);
           layout.setVisibility(View.INVISIBLE);
        } else if (id == R.id.AboutUs) {
           layout = findViewById(R.id.mainAdminUserLayout);
           layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.ourClientsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.ourProductsLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.supportLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.contactUsLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.quotationsLayout);
            layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.mainDriverLayout);
           layout.setVisibility(View.INVISIBLE);
        }else if (id == R.id.Quotations) {
           layout = findViewById(R.id.mainAdminUserLayout);
           layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.mainAdminUserLayout);
           layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.ourClientsLayout);
           layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.ourProductsLayout);
           layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.supportLayout);
           layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.contactUsLayout);
           layout.setVisibility(View.INVISIBLE);
           layout = findViewById(R.id.quotationsLayout);
           layout.setVisibility(View.VISIBLE);
           layout = findViewById(R.id.mainDriverLayout);
           layout.setVisibility(View.INVISIBLE);
       }else if (id == R.id.Messages) {
           DrawerLayout mDrawerLayout;
           mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
           mDrawerLayout.closeDrawers();
           Intent myIntent = new Intent(MainActivity.this, SendSMS.class);
           myIntent.putExtra("ForAll","T");
           myIntent.putExtra("ClientName","N");
           myIntent.putExtra("acctNo","N");
           MainActivity.this.startActivity(myIntent);
       }else if (id == R.id.RatingReport) {
           DrawerLayout mDrawerLayout;
           mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
           mDrawerLayout.closeDrawers();
           Intent myIntent = new Intent(MainActivity.this, RatingReport.class);
           myIntent.putExtra("AllCustomers",list);
           MainActivity.this.startActivity(myIntent);
       }else if (id == R.id.DailyTransactionReport) {
           DrawerLayout mDrawerLayout;
           mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
           mDrawerLayout.closeDrawers();
           Intent myIntent = new Intent(MainActivity.this, DailyTransactions.class);
           MainActivity.this.startActivity(myIntent);
       }else if (id == R.id.DriverImportantLocations) {
           DrawerLayout mDrawerLayout;
           mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
           mDrawerLayout.closeDrawers();
           Intent myIntent = new Intent(MainActivity.this, ImportantLocations.class);
           MainActivity.this.startActivity(myIntent);
       }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void goToDirections(View v)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=24.684395, 46.686639"));
        startActivity(intent);
    }
    public void UpdateLocation(View v)
    {
        Intent myIntent = new Intent(MainActivity.this, CustomerLocation.class);
        myIntent.putExtra("VarAccountNo",varCustAcctNo);
        MainActivity.this.startActivity(myIntent);
    }
    public  void SendMessage(View v){

    }
    public void goToCustomerDirections(View v)
    {
        Button btn = ((Button) v);
        final String[] cuurentValues = btn.getTag().toString().split(",");
        ListView Lv=(ListView)findViewById(R.id.lvCurrentOrders);
        View VV;
        Boolean exist = false;
        if ((Integer.parseInt(cuurentValues[7]) >0) && ! Boolean.parseBoolean(cuurentValues[5]) ){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            TextView title = new TextView(this);

            title.setText("خطأ");
            title.setBackgroundColor(Color.DKGRAY);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.RIGHT);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);
            builder.setCustomTitle(title);
            builder.setMessage("الرجاء تنفيذ العملية الاولى حسب الترتيب الزمني !");

            String positiveText = "Ok";
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            Button btnTemp;
            for (int i = 0; i < Lv.getCount(); i++) {
                VV = Lv.getChildAt(i);
                if(VV != null){
                    btnTemp=(Button) VV.findViewById(R.id.btnCusLocation);
                    String[] values = btnTemp.getTag().toString().split(",");
                    if (cuurentValues[3].toString().equals(values[3])){
                    }else{
                        if (Boolean.parseBoolean(values[5])){
                            exist = true;
                        }
                    }
                }
            }
            if (exist){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                TextView title = new TextView(this);

                title.setText("خطأ");
                title.setBackgroundColor(Color.DKGRAY);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.RIGHT);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                builder.setCustomTitle(title);
                builder.setMessage("يوجد عملية جارية حاليا, الرجاء اغلاق العملية الحالية لبدء عملية اخرى");

                String positiveText = "Ok";
                builder.setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                final String[] separated = btn.getTag().toString().split(",");
                if (separated[0].toString().equals("-")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    TextView title = new TextView(this);

                    title.setText("تأكيد");
                    title.setBackgroundColor(Color.DKGRAY);
                    title.setPadding(10, 10, 10, 10);
                    title.setGravity(Gravity.RIGHT);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(20);
                    builder.setCustomTitle(title);
                    builder.setMessage("موقع العميل غير متوفر, هل تريد الاتصال بالعميل لمعرفة الموقع ؟");

                    String positiveText = "نعم";
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + separated[4].toString()));
                                    startActivity(intent);
                                }
                            });

                    String negativeText = "لا";
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Intent myIntent = new Intent(MainActivity.this, CurrentCustomerLocation.class);
                    myIntent.putExtra("location",btn.getTag().toString());
                    MainActivity.this.startActivity(myIntent);
                }
            }

        }


    }
    public void sendEmail(View v)
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"info@awaelksa.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
    }
    public void callUs(View v)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:920000194"));
        startActivity(intent);

    }
    public void openAccDialog(View v)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialog.setView(dialogView);


        TextView text = (TextView) dialogView.findViewById(R.id.dialogText);
        text.setText(getResources().getString(R.string.accInfo));

        ImageView image = (ImageView) dialogView.findViewById(R.id.dialogImage);
        image.setImageResource(R.drawable.acco_title);


        dialog.show();
    }
    public void openInvDialog(View v)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialog.setView(dialogView);


        TextView text = (TextView) dialogView.findViewById(R.id.dialogText);
        text.setText(getResources().getString(R.string.InvInfo));

        ImageView image = (ImageView) dialogView.findViewById(R.id.dialogImage);
        image.setImageResource(R.drawable.inv_title);


        dialog.show();
    }
    public void openSalesDialog(View v)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialog.setView(dialogView);


        TextView text = (TextView) dialogView.findViewById(R.id.dialogText);
        text.setText(getResources().getString(R.string.SaleInfo));

        ImageView image = (ImageView) dialogView.findViewById(R.id.dialogImage);
        image.setImageResource(R.drawable.sale_title);


        dialog.show();
    }
    public void openPurDialog(View v)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialog.setView(dialogView);


        TextView text = (TextView) dialogView.findViewById(R.id.dialogText);
        text.setText(getResources().getString(R.string.PurInfo));

        ImageView image = (ImageView) dialogView.findViewById(R.id.dialogImage);
        image.setImageResource(R.drawable.pur_title);


        dialog.show();
    }
    public void sendQuotation(View v)
    {

    }
    public void goToHome(View v)
    {
        View layout;
        if (VarIsAdmin.equals("1")){
            layout = findViewById(R.id.mainAdminUserLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainDriverLayout);
            layout.setVisibility(View.INVISIBLE);
        }else if (VarIsAdmin.equals("2")){
            layout = findViewById(R.id.mainAdminUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainDriverLayout);
            layout.setVisibility(View.VISIBLE);
        }
        else
        {
            layout = findViewById(R.id.mainAdminUserLayout);
            layout.setVisibility(View.INVISIBLE);
            layout = findViewById(R.id.mainUserLayout);
            layout.setVisibility(View.VISIBLE);
            layout = findViewById(R.id.mainDriverLayout);
            layout.setVisibility(View.INVISIBLE);
        }

        layout = findViewById(R.id.ourClientsLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.ourProductsLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.supportLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.contactUsLayout);
        layout.setVisibility(View.INVISIBLE);
        layout = findViewById(R.id.quotationsLayout);
        layout.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    public void GoToSMS(View v){
        Intent myIntent = new Intent(MainActivity.this, SendSMS.class);
        myIntent.putExtra("ForAll","F");
        myIntent.putExtra("ClientName",varCustName);
        myIntent.putExtra("acctNo",varCustAcctNo);
        MainActivity.this.startActivity(myIntent);
    }
    public void goToFacebook(View v)
    {
        Intent myIntent = new Intent(MainActivity.this, Api_Calls.class);
        MainActivity.this.startActivity(myIntent);
       /* String facebookUrl = "https://www.facebook.com/Awaeltechnologies";
        try {
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));;
        } catch (PackageManager.NameNotFoundException e) {
            // Facebook is not installed. Open the browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }*/
    }
    public void askForSub(View v)
    {
        Intent myIntent = new Intent(MainActivity.this, AccountSubActivity.class);
        myIntent.putExtra("isAdmin",VarIsAdmin);
        myIntent.putExtra("endBalance",endBalance);
        myIntent.putExtra("acctNo",varCustAcctNo);
        myIntent.putExtra("varCustName",varCustName);
        MainActivity.this.startActivity(myIntent);
    }
    public void goToNewOrder(View v)
    {
        new GetFinishedOrders().execute();

    }
    public void GoToFinitshedOrders(View view){
        Intent myIntent = new Intent(MainActivity.this, FinishedOrders.class);
        myIntent.putExtra("acctNo",varCustAcctNo);
        MainActivity.this.startActivity(myIntent);
    }

    public void update(View view) {
       new DriverOrdersTask().execute();
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
                request.addProperty("VarAccountNo", varCustAcctNo);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
                androidHttpTransport.debug = true;
                androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                responseObject = (SoapObject) envelope.getResponse();
                VarOrdersToConfirm = responseObject.getPropertyCount();
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
            if (VarOrdersToConfirm > 0){
                Toast.makeText(MainActivity.this,"يوجد طلبيات غير مؤكدة, الرجاء تأكيد وصول الطلبيات السابقة قبل طلب طلبية جديدة", Toast.LENGTH_SHORT).show();
            }else{
                Intent myIntent = new Intent(MainActivity.this, NewOrder.class);
                myIntent.putExtra("acctNo",varCustAcctNo);
                MainActivity.this.startActivity(myIntent);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    public void goToFollowOrder(View v)
    {
        Intent myIntent = new Intent(MainActivity.this, FollowOrder.class);
        myIntent.putExtra("acctNo",varCustAcctNo);
        MainActivity.this.startActivity(myIntent);
    }
    public void goTotSub(View v)
    {
        if (varCustAcctNo.equals(""))
        {
            Toast.makeText(MainActivity.this,"الرجاء اختيار عميل", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent myIntent = new Intent(MainActivity.this, AccountSubActivity.class);
            myIntent.putExtra("endBalance",endBalance);
            myIntent.putExtra("varCustName",varCustName);
            myIntent.putExtra("acctNo",varCustAcctNo);
            myIntent.putExtra("isAdmin",VarIsAdmin);
            MainActivity.this.startActivity(myIntent);
        }

    }
    public void customerInfo(View v)
    {
        if (varCustNo.equals(""))
        {
            Toast.makeText(MainActivity.this,"الرجاء اختيار عميل", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent myIntent = new Intent(MainActivity.this, CustomerInfo.class);
            myIntent.putExtra("varCustNo",varCustNo);
            MainActivity.this.startActivity(myIntent);
        }
    }
    public void lastInvoice(View v)
    {
        if (varCustAcctNo.equals(""))
        {
            Toast.makeText(MainActivity.this,"الرجاء اختيار عميل", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent myIntent = new Intent(MainActivity.this, LastTransActionActivity.class);
            myIntent.putExtra("varSource","1");
            myIntent.putExtra("varAcctNo",varCustAcctNo);
            myIntent.putExtra("varCustName",varCustName);
            MainActivity.this.startActivity(myIntent);
        }
    }
    class subsidaryTask extends AsyncTask<Integer, Integer, String> {
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
            ListView listView=(ListView)findViewById(R.id.lvCustomers);
            list=new ArrayList<HashMap<String,String>>();
            varFilled = "1";
            ListViewAdaptersCustomer adapter=new ListViewAdaptersCustomer(MainActivity.this, list);
            listView.setAdapter(adapter);

            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String,String> temp=new HashMap<String, String>();
                int count = reponseArrayString.length;
                if (count  == 3){
                    temp.put(CUS_SECOND_COLUMN, reponseArrayString[0]);
                    temp.put(CUS_FIRST_COLUMN, reponseArrayString[1]);
                    temp.put(CUS_THIRD_COLUMN, reponseArrayString[2]);
                    list.add(temp);
                }

            }

            adapter=new ListViewAdaptersCustomer(MainActivity.this, list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                {
                    TextView txtCAccNo = (TextView)view.findViewById(R.id.txtCAccNo);
                    TextView txtCNo = (TextView)view.findViewById(R.id.txtCNo);
                    TextView txtCName = (TextView)view.findViewById(R.id.txtCName);
                    varCustAcctNo = txtCAccNo.getText().toString();
                    varCustNo = txtCNo.getText().toString();
                    varCustName =txtCName.getText().toString();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    LinearLayout ll = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
                    ll.setAlpha(1);

                    //Toast.makeText(MainActivity.this, txtCAccNo.getText(), Toast.LENGTH_SHORT).show();
                }

            });

            varFilled = "0";
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    class DriverOrdersTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "getCurrentDriverOrders";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/getCurrentDriverOrders";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarDriverNo", varCustNo);

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
            final ListView listView=(ListView)findViewById(R.id.lvCurrentOrders);
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdapterCurrentOrders adapter=new ListViewAdapterCurrentOrders(MainActivity.this, list);
            listView.setAdapter(adapter);

            for(int i=0;i<responseObject.getPropertyCount();i++){
                String [] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String,String> temp=new HashMap<String, String>();
                int count = reponseArrayString.length;
                temp.put(LT_FIRST_COLUMN, reponseArrayString[2]);
                temp.put(LT_SECOND_COLUMN, reponseArrayString[3]);
                temp.put(LT_THIRD_COLUMN, reponseArrayString[4]+","+reponseArrayString[5]);
                if (count == 7){
                    temp.put(LT_FOURTH_COLUMN, reponseArrayString[6]);
                }else{
                    temp.put(LT_FOURTH_COLUMN, "0000000");
                }

                temp.put(LT_FIFTH_COLUMN, reponseArrayString[1]);
                temp.put(LT_SIXTH_COLUMN, reponseArrayString[0]);
                list.add(temp);
            }
            adapter=new ListViewAdapterCurrentOrders(MainActivity.this, list);
            listView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        TextView title = new TextView(this);

        title.setText("خروج");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.RIGHT);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        builder.setCustomTitle(title);
        builder.setMessage("هل تريد الخروج ؟");

        String positiveText = "خروج";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       finish();
                    }
                });

        String negativeText = "لا";
        String naturalText = "تسجيل الخروج";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });
        builder.setNeutralButton(naturalText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endBalance = "";
                        varCustNo = "";
                        varCustAcctNo = "";
                        varCustName = "";
                        VarIsAdmin = "";
                        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

    }

    @Override
    protected void onResume() {
        if (VarIsAdmin.equals("2")){
            new DriverOrdersTask().execute();
        }
        super.onResume();
    }
}
