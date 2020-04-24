package com.volcano.holsansys.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServiceAPI {
    String WebserviceUrl = "http://192.168.1.7:8080/MainAPI?wsdl";
    String NameSpace = "http://HolsanSys.com/";

    public String ConnectingWebService(String[] webservice)
    {
        String myReturnStr="";

        List<Map<String, String>> myParamList=new ArrayList<Map<String, String>>();

        myParamList.add(ParamListInit("myTransformat", "json"));

        switch (webservice[0]){
            case "UserLogin" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                myParamList.add(ParamListInit("myPassword", webservice[2]));
                break;
            case "UserRegister" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                myParamList.add(ParamListInit("myUserName", webservice[2]));
                myParamList.add(ParamListInit("myPassword", webservice[3]));
        }

        try
        {
            SOAPCls mySOAPCls=new SOAPCls(WebserviceUrl, NameSpace,webservice[0], myParamList, false);

            myReturnStr=mySOAPCls.ExecuteMethod();
        }
        catch(Exception e)
        {
            myReturnStr=e.getMessage();
        }

        return myReturnStr;
    }

    private Map<String, String> ParamListInit(String myParamName, String myParamValue)
    {
        Map<String, String> myMap=new HashMap<String, String>();

        myMap.put(myParamName, myParamValue);

        return myMap;
    }
}
