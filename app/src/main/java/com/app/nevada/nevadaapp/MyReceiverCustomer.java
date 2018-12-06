package com.app.nevada.nevadaapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class MyReceiverCustomer extends BroadcastReceiver {
    private static String VarCustomerId = "";
    private String VarOrderNumber = "";
    private String VarStatus = "";
    private NotificationManager notificationManager;
    SoapObject responseObject;
    Context context;
    SharedPreferences sharedPreferences;
    final Handler haCustomer = new Handler();
    Runnable VarRunnableCustomer;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        sharedPreferences = context.getSharedPreferences("data", 0);
        VarCustomerId = sharedPreferences.getString("VarCustomerId", "null");
        /** last code to show notification*/
        /*String getintent = intent.getStringExtra("TWO_TIME");
        if (getintent.equals(VarCustomerId)) {
            new getCustOrder().execute();
            Toast.makeText(context, "in ui", Toast.LENGTH_SHORT).show();
        } else {
            try {
                String s = VarOrderNumber;
            } catch (Exception e) {
            }
            //Toast.makeText(context, "in2 ui", Toast.LENGTH_SHORT).show();
            //CancelAlarm1(context);
        }*/
        haCustomer.postDelayed(VarRunnableCustomer = new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(context, "D", Toast.LENGTH_SHORT).show();
                new getCustOrder().execute();
                haCustomer.postDelayed(this, 1000 * 20);
            }
        }, 1000 * 20);


    }


    public void setAlarm1(Context context, String CustomerId) {
        Intent intent = new Intent(context, MyReceiverCustomer.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //After after 1 minute
        am1.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1*60*1000 , pi);

    }

    public void CancelAlarm1(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyReceiverCustomer.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_NO_CREATE);
        if (sender != null){
            alarmManager.cancel(sender);}
    }

    public void sendnotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_001");
        Intent ii = new Intent(context, LoginActivity.class);
        ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mBuilder.setAutoCancel(true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("تم بدء الطلبية");
        bigText.setBigContentTitle("");
        bigText.setSummaryText("تنبيه");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
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

        notificationManager.notify(0, mBuilder.build());
        //notificationManager.cancel(0);
    }

    class getCustOrder extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "getCustOrders";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/getCustOrders";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarCustID", VarCustomerId);

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
            if (responseObject.getPropertyCount() > 0) {
                //Toast.makeText(context,"yes",Toast.LENGTH_SHORT).show();
                for (int i = 0; i < responseObject.getPropertyCount(); i++) {
                    String[] d = responseObject.getProperty(i).toString().split("&&");
                    if (d[2].equals("True")) {
                        VarOrderNumber = d[0];
                        //sendnotification
                        sendnotification(context);
                        new UpdateOrderNotification().execute();

                    }
                }
            } else {
                //CancelAlarm1(context);

            }


        }
            /*adapter=new ListViewAdapterCurrentOrders(MainActivity.this, list);
            listView.setAdapter(adapter);*/


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class UpdateOrderNotification extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "UpdateOrderNotification\n";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateOrderNotification\n";
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
           /* final ListView listView=(ListView)findViewById(R.id.lvCurrentOrders);
            list=new ArrayList<HashMap<String,String>>();
            ListViewAdapterCurrentOrders adapter=new ListViewAdapterCurrentOrders(MainActivity.this, list);
            listView.setAdapter(adapter);*/
        }





        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

}
