package com.volcano.holsansys.tools;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Map;

public class SOAPCls {
    private String _WebserviceUrl="";   //the url of webservice
    private boolean _IsDotNet=false;    //true denotes that the webservice accessed is developed by .net
    private SoapObject _BodyOutObj=null;

    public SOAPCls(String myWebserviceUrl, String myNameSpace, String myMethodName
            , List<Map<String, String>> myParamList, boolean myIsDotNet)
    {
        _WebserviceUrl=myWebserviceUrl;
        _IsDotNet=myIsDotNet;

        _BodyOutObj = new SoapObject(myNameSpace, myMethodName);

        for(int i=0;i<myParamList.size();i++)
        {
            Map.Entry<String, String> myEntry=myParamList.get(i).entrySet().iterator().next();

            _BodyOutObj.addProperty(myEntry.getKey(), myEntry.getValue());
        }
    }

    public String ExecuteMethod() throws Exception
    {
        //Creating a SoapSerializationEnvelope object, and meanwhile specifying the soap version
        SoapSerializationEnvelope myEnvelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        myEnvelope.bodyOut = _BodyOutObj;   //sending a request
        myEnvelope.dotNet = _IsDotNet;

        HttpTransportSE myHttpTransSE = new HttpTransportSE(_WebserviceUrl,10000);
        myHttpTransSE.call(null, myEnvelope);

        //Obtaining the returned data
        SoapObject myBodyInObj = (SoapObject) myEnvelope.bodyIn;

        //Obtaining the result we need with json format from the returned data
        String myReturnStr = myBodyInObj.getProperty("return").toString();

        return myReturnStr;
    }
}
