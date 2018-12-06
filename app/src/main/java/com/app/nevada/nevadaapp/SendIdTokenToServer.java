package com.app.nevada.nevadaapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class SendIdTokenToServer {
     String id;
     String IdDevice;
     String isAdmin;

    SendIdTokenToServer(String id, String IdDevice, String isAdmin) {
        this.id = id;
        this.IdDevice = IdDevice;
        this.isAdmin = isAdmin;
        if (isAdmin.equals("2")) {
            new UpdateDrivers().execute();
        } else {
            new UpdateCustomers().execute();
        }

    }


    public  class UpdateCustomers extends AsyncTask<String, String, String> {
        SoapObject responseObject;

        @Override
        protected String doInBackground(String... integers) {
            try {
                String METHOD_NAME = "UpdateCustomers";
                String NAMESPASE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateCustomers";
                SoapObject request = new SoapObject(NAMESPASE, METHOD_NAME);
                request.addProperty("IDDevice", IdDevice);
                request.addProperty("CustomerNo", id);
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



        }

    }

    public  class UpdateDrivers extends AsyncTask<String, String, String> {
        SoapObject responseObject;

        @Override
        protected String doInBackground(String... integers) {
            try {
                String METHOD_NAME = "UpdateDrivers";
                String NAMESPASE = "http://37.224.24.195";
                String URL = "http://37.224.24.195/AndroidWS/GetInfo.asmx";
                String SOAP_ACTION = "http://37.224.24.195/UpdateDrivers";
                SoapObject request = new SoapObject(NAMESPASE, METHOD_NAME);
                request.addProperty("IDDevice", IdDevice);
                request.addProperty("DriverNo", id);
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



        }

    }
}
