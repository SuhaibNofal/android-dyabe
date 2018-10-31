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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

public class MyReceiverStatus extends BroadcastReceiver {
private static String VarOrderNumber ="";
private String VarStatus = "";
SoapObject  responseObject;
String intentType;
Context context;
int x = 1;
boolean status;
    private NotificationManager notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String getintent = intent.getStringExtra("ONE_TIME");
        if (getintent.equals(VarOrderNumber)) {
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
        }




    }
    public void setAlarm(Context context,String VarorderNumber)

    {
       this.VarOrderNumber = VarorderNumber;

        Intent intent = new Intent(context, MyReceiverStatus.class);
        intent.putExtra("ONE_TIME",VarorderNumber);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, intent.FILL_IN_DATA);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);

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

    public void CancelAlarm(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyReceiverStatus.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent, intent.FILL_IN_DATA);

        alarmManager.cancel(sender);


    }
    public void sendnotification(Context context)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_001");
        Intent ii = new Intent(context, LoginActivity.class);
        ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mBuilder.setAutoCancel(true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("يرجى التوجه الى العميل الجديد");
        bigText.setBigContentTitle("تم تغير موقع الطلبية");
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

    class getStatusOrderNo extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {

                String METHOD_NAME = "getStatusOrderNo";
                String NAMESPACE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/getStatusOrderNo";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("VarOrderNo",VarOrderNumber);
                //request.addProperty("VarOrderStatus", Integer.parseInt(VarStatus));

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

            if (responseObject!=null){
                for (int i=0 ;i<responseObject.getPropertyCount();i++){
                    String[] statusOrder=responseObject.getProperty(i).toString().split("&&");
                    if(statusOrder[0].equals("2")){

                        sendnotification(context);
                        CancelAlarm(context);
                    }

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
