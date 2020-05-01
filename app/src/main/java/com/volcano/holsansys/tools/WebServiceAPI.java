package com.volcano.holsansys.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServiceAPI {
    String WebserviceUrl = "http://192.168.43.118:8080/MainAPI?wsdl";
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
                break;
            case "UserInfo" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                break;
            case "PatientInfo" :
            case "NotificationInfo" :
            case "DrugRecordInfo" :
            case "DeletePatient" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                myParamList.add(ParamListInit("myPatientName", webservice[2]));
                break;
            case "AddPatient" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                myParamList.add(ParamListInit("newPatientName", webservice[2]));
                myParamList.add(ParamListInit("newPatientAge", webservice[3]));
                myParamList.add(ParamListInit("newPatientSex", webservice[4]));
                myParamList.add(ParamListInit("newPatientAddress", webservice[5]));
                myParamList.add(ParamListInit("newPatientBloodType", webservice[6]));
                myParamList.add(ParamListInit("newPatientMedicalHistory", webservice[7]));
                myParamList.add(ParamListInit("newPatientAllergy", webservice[8]));
                break;
            case "AddNotification" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                myParamList.add(ParamListInit("newNotificationName", webservice[2]));
                myParamList.add(ParamListInit("newDayNotification", webservice[3]));
                myParamList.add(ParamListInit("newWeekNotificaction", webservice[4]));
                myParamList.add(ParamListInit("newNotificationWay", webservice[5]));
                myParamList.add(ParamListInit("newPictureSrc", webservice[6]));
                myParamList.add(ParamListInit("newPictureText", webservice[7]));
                myParamList.add(ParamListInit("newVoiceSrc", webservice[8]));
                myParamList.add(ParamListInit("newTextText", webservice[9]));
                myParamList.add(ParamListInit("newTinkleSrc", webservice[10]));
                myParamList.add(ParamListInit("newNotificationVibrate", webservice[11]));
                myParamList.add(ParamListInit("myPatientName", webservice[12]));
                break;
            case "NotificationDetail" :
            case "DeleteNotification" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                myParamList.add(ParamListInit("myPatientName", webservice[2]));
                myParamList.add(ParamListInit("myNotificationName", webservice[3]));
                break;
            case "DrugRecord" :
                myParamList.add(ParamListInit("myUserID", webservice[1]));
                myParamList.add(ParamListInit("myPatientName", webservice[2]));
                myParamList.add(ParamListInit("myNotificationName", webservice[3]));
                myParamList.add(ParamListInit("myIfDrug",webservice[4]));
                break;
        }

        try
        {
            SOAPCls mySOAPCls=new SOAPCls(WebserviceUrl, NameSpace,webservice[0], myParamList, false);

            myReturnStr=mySOAPCls.ExecuteMethod();
        }
        catch(Exception e)
        {
            myReturnStr="操作失败";
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
