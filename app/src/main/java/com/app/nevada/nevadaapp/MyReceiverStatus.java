package com.app.nevada.nevadaapp;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.ListView;
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
import java.util.List;

import static com.app.nevada.nevadaapp.Constants.LT_FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SIXTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_THIRD_COLUMN;

public class MyReceiverStatus extends BroadcastReceiver {
    private String VarOrderNumber = "";
    private String VarStatus = "";
    SoapObject responseObject;
    String intentType;
    Context context;
    int x = 1;
    SharedPreferences sharedPreferences;
    String OrderPrimarynumber = "";
    boolean status;
    String getintent = "";
    final Handler ha = new Handler();
    Runnable VarRunnable;
    private NotificationManager notificationManager;
    String backreference;
    String ordertoCustomer = "";

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        //String getintent = intent.getStringExtra("ONE_TIME");
        sharedPreferences = context.getSharedPreferences("data", 0);
        backreference = sharedPreferences.getString("Driver Id", "null");
       /* if (getintent.equals(VarOrderNumber)) {
            if (isAppForground(context)){
                if(status!=true){
                    new getStatusOrderNo().execute();
                  //  new MainActivity().refreshactivity();
                  Toast.makeText(context,"in ui",Toast.LENGTH_SHORT).show();
                }else{
                    new getStatusOrderNo().execute();

                }

            }else if(getintent.equals(VarOrderNumber)){
                new getStatusOrderNo().execute();
            }else{

            }
        }*/
        ha.postDelayed(VarRunnable = new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(context, "D", Toast.LENGTH_SHORT).show();
                new GetDriversNotification().execute();
                new DriverOrdersTask().execute();
                ha.postDelayed(this, 1000 * 20);
            }
        }, 1000 * 20);


    }

    public void setAlarm(Context context, String VarorderNumber)

    {
        Intent intent = new Intent(context, MyReceiverStatus.class);
        //intent.putExtra("ONE_TIME",VarorderNumber);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1 * 60 * 1000, pi);

    }



   /* public void setAlarmCustomer(Context context,String VarorderNumber)

    {
        this.VarOrderNumber = VarorderNumber;

        Intent intent = new Intent(context, MyReceiverStatus.class);
        intent.putExtra("ONE_TIME",VarorderNumber);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);

    }*/

    public void CancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyReceiverStatus.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_NO_CREATE);
        if (sender != null) {
            alarmManager.cancel(sender);
        }


    }

    static int count = 0;

    public void sendnotification(Context context, String notyType) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_001");
        Intent ii = new Intent(context, LoginActivity.class);
        ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mBuilder.setAutoCancel(true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        if (notyType == "1") {
            bigText.bigText("يرجى التوجه الى العميل الجديد");
            bigText.setBigContentTitle("تم تغير موقع الطلبية");
            bigText.setSummaryText("تنبيه");
        } else {
            bigText.bigText("تم تغير السائق");
            bigText.setBigContentTitle("يرجى مراجعة الطلبيات");
            bigText.setSummaryText("تنبيه");
        }


        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("تنبيه بتعديل الطلبيات");
        mBuilder.setContentText("تنبيه");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(count, mBuilder.build());
        count++;
        //notificationManager.cancel(0);
    }

    /**
     * function to check if driver have order to send notification  (GetDriversNotification)
     */
    class GetDriversNotification extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                String METHOD_NAME = "GetDriversNotification";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/GetDriversNotification";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("VarDriverNo", backreference);

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
           /* final ListView listView=(ListView)findViewById(R.id.lvCurrentOrders);
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdapterCurrentOrders adapter=new ListViewAdapterCurrentOrders(MainActivity.this, list);
            listView.setAdapter(adapter);*/

            for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                String[] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                HashMap<String, String> temp = new HashMap<String, String>();

                VarOrderNumber = reponseArrayString[3];
                if (reponseArrayString[2].equals("False")) {
                    sendnotification(context, "2");
                    new UpdateDriversNotification().execute();
                    // new UpdateOrderDriverNotification().execute();
                    //send yes i send()
                }
            }


        }
    }

    /**
     * function to update order that whant to send notification (UpdateDriversNotification)
     */
    class UpdateDriversNotification extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                String METHOD_NAME = "UpdateDriversNotification";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateDriversNotification";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarOrderNo", VarOrderNumber);

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

        }
    }

    /**
     * function to update order that whant to send notification (UpdateDriversNotification)
     */
    class UpdateOrderDriverNotification extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                String METHOD_NAME = "UpdateOrderDriverNotification";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateOrderDriverNotification";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarOrderNo", VarOrderNumber);

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

        }
    }

    /**
     * class to get all driver order and check if he has notification or not
     */
    class DriverOrdersTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "getCurrentDriverOrders";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/getCurrentDriverOrders";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarDriverNo", backreference);

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


            for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                String[] reponseArrayString = responseObject.getProperty(i).toString().split("&&");
                if (reponseArrayString[8].equals("True")) {
                    sendnotification(context, "1");
                    ordertoCustomer = reponseArrayString[2];
                    new UpdateOrderCustomerNotification().execute();

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

    class UpdateOrderCustomerNotification extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                String METHOD_NAME = "UpdateOrderCustNotification";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateOrderCustNotification";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarOrderNo", ordertoCustomer);

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

        }
    }

    public boolean isAppForground(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }

        return true;
    }

}
