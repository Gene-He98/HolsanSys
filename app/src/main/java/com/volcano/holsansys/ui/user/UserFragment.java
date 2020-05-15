package com.volcano.holsansys.ui.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.volcano.holsansys.add.AddPatientActivity;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserFragment extends Fragment {

    private List<Patient> mData = null;
    private Context mContext;
    private PatientAdapter mAdapter = null;
    private ListView list_user;
    private TextView userName;
    private View root;
    private LinearLayout userContent;
    private ScrollView patientContent;
    private VerifyTask myVerifyTask;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity.currentView=3;
        root = inflater.inflate(R.layout.fragment_user, container, false);
        userContent= root.findViewById(R.id.user_information);
        patientContent= root.findViewById(R.id.patients_information);
        mContext =getActivity();
        if(MainActivity.mode){
            if(MainActivity.patientName.equals("")){
                //提醒界面数据更新
                updateUser();
            }else {
                updatePatient();
                userContent.setVisibility(View.GONE);
                patientContent.setVisibility(View.VISIBLE);
            }
        }else {
            updatePatient();
            userContent.setVisibility(View.GONE);
            patientContent.setVisibility(View.VISIBLE);
            root.findViewById(R.id.me_bt_emer).setVisibility(View.VISIBLE);
            root.findViewById(R.id.edit_patient).setVisibility(View.GONE);
            root.findViewById(R.id.delete_patient).setVisibility(View.GONE);
            root.findViewById(R.id.back_main).setVisibility(View.GONE);
        }
        if(MainActivity.bgThread3==null){
            MainActivity.bgThread3=new Thread() {
                @Override
                public void run() {
                    while (true){
                        if(MainActivity.refreshPatientFlag){
                            if(MainActivity.patientName.equals("")){
                                String[] myParamsArr={"UserInfo",MainActivity.userID};
                                myVerifyTask = new VerifyTask();
                                myVerifyTask.execute(myParamsArr);
                            }else {
                                String[] myParamsArr ={"PatientInfo",MainActivity.userID,MainActivity.patientName};
                                myVerifyTask = new VerifyTask();
                                myVerifyTask.execute(myParamsArr);
                            }
                            MainActivity.refreshPatientFlag =false;
                        }
                    }
                }
            };
            MainActivity.bgThread3.start();
        }

        return root;
    }

    private void updatePatient() {
        String[] myParamsArr ={"PatientInfo",MainActivity.userID,MainActivity.patientName};
        myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
        root.findViewById(R.id.back_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.patientName="";
                updateUser();
                userContent.setVisibility(View.VISIBLE);
                patientContent.setVisibility(View.GONE);
            }
        });
        root.findViewById(R.id.edit_patient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), AddPatientActivity.class);
                intent.putExtra("kind","change");
                startActivity(intent);
            }
        });

        root.findViewById(R.id.delete_patient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
                normalDialog.setTitle("您将要删除此用药人");
                normalDialog.setMessage("是否确定？");
                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePatient();
                        MainActivity.patientName="";
                        updateUser();
                        userContent.setVisibility(View.VISIBLE);
                        patientContent.setVisibility(View.GONE);
                    }
                });
                normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                normalDialog.setCancelable(false);
                // 显示
                normalDialog.show();
            }
        });
    }

    private void updateUser() {
        userName=root.findViewById(R.id.fra_user_name);
        userName.setText(MainActivity.userName);
        list_user = root.findViewById(R.id.listView_user);

        list_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.patientName=(String)((TextView)view.findViewById(R.id.patientName)).getText();
                updatePatient();
                userContent.setVisibility(View.GONE);
                patientContent.setVisibility(View.VISIBLE);
            }
        });
        String[] myParamsArr={"UserInfo",MainActivity.userID};
        myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }

    private void deletePatient(){
        String[] myParamsArr ={"DeletePatient",MainActivity.userID,MainActivity.patientName};
        myVerifyTask = new VerifyTask();
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
                    if(myResult.equals("[{\"msg\":\"ok\"}]")){

                    }
                    else {
                        if(!myResult.equals("[]")){
                            Gson myGson = new Gson();
                            List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                            try {
                                mData = new LinkedList<>();
                                for (int i=0;i<myList.size();i++){
                                    Map<String,String> myMap=myList.get(i);
                                    String patientName = myMap.get("PatientName");
                                    String location = myMap.get("Location");
                                    mData.add(new Patient(patientName,location));
                                }
                                mAdapter = new PatientAdapter((LinkedList<Patient>) mData, mContext);
                                list_user.setAdapter(mAdapter);
                            }
                            catch (Exception ex){}
                        }
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