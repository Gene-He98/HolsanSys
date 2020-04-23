package com.volcano.holsansys.login;

import com.volcano.holsansys.tools.SOAPCls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLoginVerification {
    String WebserviceUrl = "http://192.168.1.7:8080/MainAPI?wsdl";
    String NameSpace = "http://HolsanSys.com/";

    //Connecting WebService
    public String ConnectingWebService(String myMethodName, String myUserID, String myPassword)
    {
        String myReturnStr="";

        List<Map<String, String>> myParamList=new ArrayList<Map<String, String>>();

        myParamList.add(ParamListInit("myTransformat", "json"));
        myParamList.add(ParamListInit("myUserID", myUserID));
        myParamList.add(ParamListInit("myPassword", myPassword));

        try
        {
            SOAPCls mySOAPCls=new SOAPCls(WebserviceUrl, NameSpace,myMethodName, myParamList, false);

            myReturnStr=mySOAPCls.ExecuteMethod();
            System.out.println("myReturnStr:"+myReturnStr);
        }
        catch(Exception e)
        {
            myReturnStr=e.getMessage();
        }

        return myReturnStr;
    }//private String ConnectingWebService(String myMethodName, List<Map<String, String>> myParamList)

    //Initializing the ParamList
    private Map<String, String> ParamListInit(String myParamName, String myParamValue)
    {
        Map<String, String> myMap=new HashMap<String, String>();

        myMap.put(myParamName, myParamValue);

        return myMap;
    }//private Map<String, String> ParamListInit(String myParamName, String myParamValue)
}
