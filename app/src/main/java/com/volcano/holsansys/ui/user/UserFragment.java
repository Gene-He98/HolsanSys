package com.volcano.holsansys.ui.user;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserFragment extends Fragment {

    private List<Patient> mData = null;
    private Context mContext;
    private PatientAdapter mAdapter = null;
    private ListView list_user;
    private LinearLayout foot_view;
    private TextView userName;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_user, container, false);

        userName=root.findViewById(R.id.fra_user_name);
        userName.setText(MainActivity.userName);
        list_user = root.findViewById(R.id.listView_user);

        foot_view = (LinearLayout) inflater.inflate(R.layout.footview_listview_user, null);//得到尾部的布局
        list_user.addFooterView(foot_view);//添加尾部
        list_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.patientName=(String)((TextView)view.findViewById(R.id.patientName)).getText();
                updatePatient(MainActivity.patientName);
                ((LinearLayout)root.findViewById(R.id.user_information)).setVisibility(View.GONE);
                ((ScrollView)root.findViewById(R.id.patients_information)).setVisibility(View.VISIBLE);
            }
        });
        mContext =getActivity();

        //提醒界面数据更新
        updateUser();
        return root;
    }

    private void updatePatient(String patientName) {
        String[] myParamsArr ={"PatientInfo",MainActivity.userID,patientName};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }

    private void updateUser() {
        String[] myParamsArr={"UserInfo",MainActivity.userID};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }


    class VerifyTask extends AsyncTask<String, Integer, String> {
        private int kind=0;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... myParams)
        {
            if(myParams[0].equals("PatientInfo"))
                kind=1;
            String myResult="";
            try
            {
                myResult = (new WebServiceAPI()).ConnectingWebService(myParams);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return myResult;
        }

        @Override
        protected void onProgressUpdate(Integer... myValues)
        {
            super.onProgressUpdate(myValues);
        }

        @Override
        protected void onPostExecute(String myResult)
        {
            switch (kind){
                case 0 :
                    if(!myResult.equals("[]")){
                        Gson myGson = new Gson();
                        List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                        try {
                            mData = new LinkedList<>();
                            for (int i=0;i<myList.size();i++){
                                Map<String,String> myMap=myList.get(i);
                                String patientName = myMap.get("PatientName");
                                String recentDrugRecord = myMap.get("RecentDrugRecord");
                                String location = myMap.get("Location");
                                mData.add(new Patient(patientName, recentDrugRecord,location));
                            }
                            mAdapter = new PatientAdapter((LinkedList<Patient>) mData, mContext);
                            list_user.setAdapter(mAdapter);
                        }
                        catch (Exception ex){}
                    }
                    break;
                case 1 :
                    if(!myResult.equals("[]")){
                        Gson myGson = new Gson();
                        List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                        try {
                            Map<String,String> myMap=myList.get(0);
                            String patientName = myMap.get("PatientName");
                            String patientAge = myMap.get("PatientAge");
                            String patientSex = myMap.get("PatientSex");
                            String patientAddress = myMap.get("PatientAddress");
                            String patientBloodType = myMap.get("PatientBloodType");
                            String patientMedicalHistory = myMap.get("PatientMedicalHistory");
                            String patientAllergy = myMap.get("PatientAllergy");

                            ((TextView)getView().findViewById(R.id.me_name)).setText(patientName);
                            ((TextView)getView().findViewById(R.id.me_user_age)).setText(patientAge);
                            ((TextView)getView().findViewById(R.id.me_sex)).setText(patientSex);
                            ((TextView)getView().findViewById(R.id.me_area)).setText(patientAddress);
                            ((TextView)getView().findViewById(R.id.me_blood)).setText(patientBloodType);
                            ((TextView)getView().findViewById(R.id.me_medi)).setText(patientMedicalHistory);
                            ((TextView)getView().findViewById(R.id.me_allergy)).setText(patientAllergy);

                        }
                        catch (Exception ex){}
                    }
            }
        }
    }

}