package com.app.nevada.nevadaapp;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Mothana on 11/10/2016.
 */

public class clsApi_Calls {


    private static final String METHOD_NAME = "GetCustomerByID";
    private static final String NAMESPACE = "http://erp.circlegl.com/";
    private static final String URL = "http://erp.circlegl.com/webs/GetEmp.asmx";
    final String SOAP_ACTION = "http://erp.circlegl.com/GetCustomerByID";

    public String login(String UserName, String Password) throws IOException, XmlPullParserException {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("ID", "20000001");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 1000000000);
        androidHttpTransport.debug = true;
        androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        androidHttpTransport.call(SOAP_ACTION, envelope);
        Object result = (Object) envelope.getResponse();
        String resultData = result.toString();
        return resultData;
    }

}
