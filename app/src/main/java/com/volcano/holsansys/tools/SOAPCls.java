package com.volcano.holsansys.tools;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Map;

public class SOAPCls {
    private String webServiceUrl ="";
    private boolean isDotNet =false;
    private SoapObject bodyOutObj =null;

    public SOAPCls(String myWebserviceUrl, String myNameSpace, String myMethodName
            , List<Map<String, String>> myParamList, boolean myIsDotNet)
    {
        webServiceUrl =myWebserviceUrl;
        isDotNet =myIsDotNet;

        bodyOutObj = new SoapObject(myNameSpace, myMethodName);

        for(int i=0;i<myParamList.size();i++)
        {
            Map.Entry<String, String> myEntry=myParamList.get(i).entrySet().iterator().next();

            bodyOutObj.addProperty(myEntry.getKey(), myEntry.getValue());
        }
    }

    public String ExecuteMethod() throws Exception
    {
        SoapSerializationEnvelope myEnvelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        myEnvelope.bodyOut = bodyOutObj;
        myEnvelope.dotNet = isDotNet;

        HttpTransportSE myHttpTransSE = new HttpTransportSE(webServiceUrl,10000);
        myHttpTransSE.call(null, myEnvelope);

        SoapObject myBodyInObj = (SoapObject) myEnvelope.bodyIn;

        String myReturnStr = myBodyInObj.getProperty("return").toString();

        return myReturnStr;
    }
}
