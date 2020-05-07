package com.volcano.holsansys.add;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.List;
import java.util.Map;

public class AddPatientActivity extends AppCompatActivity {
    private Toast mToast=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        Intent intent= getIntent();
        if(intent.getStringExtra("kind").equals("add")){
            findViewById(R.id.add_patient).setVisibility(View.VISIBLE);
            findViewById(R.id.change_patient).setVisibility(View.GONE);
        }else if(intent.getStringExtra("kind").equals("change")){
            findViewById(R.id.add_patient).setVisibility(View.GONE);
            findViewById(R.id.change_patient).setVisibility(View.VISIBLE);
            String[] myParamsArr={"PatientInfo", MainActivity.userID,MainActivity.patientName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
    }

    public void Add_Patient(View view) {
        if(isReady()){
            String patientName = ((TextView)findViewById(R.id.add_patient_name))
                    .getText().toString();
            String patientAge = ((TextView)findViewById(R.id.add_patient_age))
                    .getText().toString();
            String patientSex = ((Spinner)findViewById(R.id.add_patient_sex))
                    .getSelectedItem().toString();
            String patientAddress = ((TextView)findViewById(R.id.add_patient_address))
                    .getText().toString();
            String patientBloodType = ((Spinner)findViewById(R.id.add_patient_blood_type))
                    .getSelectedItem().toString();
            String patientMedicalHistory = ((TextView)findViewById(R.id.add_patient_medical_history))
                    .getText().toString();
            String patientAllergy = ((TextView)findViewById(R.id.add_patient_allergy))
                    .getText().toString();
            String[] myParamsArr={"AddPatient",MainActivity.userID,patientName,patientAge,
                    patientSex,patientAddress,patientBloodType,patientMedicalHistory,patientAllergy};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
            AddPatientActivity.this.finish();
        }
    }

    public void Change_Patient(View view) {
        if(isReady()){
            String patientName = ((TextView)findViewById(R.id.add_patient_name))
                    .getText().toString();
            if(!MainActivity.patientName.equals(patientName)){
                MainActivity.patientName=patientName;
            }
            deletePatient();
            Add_Patient(view);
            AddPatientActivity.this.finish();
        }
    }

    private void deletePatient(){
        String[] myParamsArr ={"DeletePatient",MainActivity.userID,MainActivity.patientName};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }

    private boolean isReady(){
        if(((TextView)findViewById(R.id.add_patient_name)).getText().toString().equals("")){
            if (mToast == null) {
                mToast=Toast.makeText(AddPatientActivity.this,
                        "请填写用药人姓名！",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
            return false;
        }
        else if(((TextView)findViewById(R.id.add_patient_age)).getText().toString().equals("")){
            if (mToast == null) {
                mToast=Toast.makeText(AddPatientActivity.this,
                        "请填写用药人年龄！",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
            return false;
        }
        else if(((Spinner)findViewById(R.id.add_patient_sex)).getSelectedItem().equals("")){
            if (mToast == null) {
                mToast=Toast.makeText(AddPatientActivity.this,
                        "请选择用药人性别！",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
            return false;
        }
        else if(((Spinner)findViewById(R.id.add_patient_blood_type)).getSelectedItem().equals("")){
            if (mToast == null) {
                mToast=Toast.makeText(AddPatientActivity.this,
                        "请选择用药人血型！",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
            return false;
        }else
            return true;
    }

    class VerifyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... myParams)
        {
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
            //查询结果为成功，则跳转到主页面
            if(myResult.equals("[{\"msg\":\"ok\"}]")){
            }
            else {
                if(myResult.equals("[{\"msg\":\"error\"}]")){
                    if (mToast == null) {
                        mToast=Toast.makeText(AddPatientActivity.this,
                                "添加失败，请稍后重试！",Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                    } else {
                        mToast.setDuration(Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                }else{
                    Gson myGson = new Gson();
                    List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                    Map<String,String> myMap=myList.get(0);
                    ((TextView)findViewById(R.id.add_patient_name)).setText(MainActivity.patientName);
                    ((TextView)findViewById(R.id.add_patient_age)).setText(myMap.get("PatientAge"));
                    ((TextView)findViewById(R.id.add_patient_address)).setText(myMap.get("PatientAddress"));
                    ((TextView)findViewById(R.id.add_patient_medical_history)).setText(myMap.get("PatientMedicalHistory"));
                    ((TextView)findViewById(R.id.add_patient_sex)).setText(myMap.get("PatientSex"));
                    ((TextView)findViewById(R.id.add_patient_blood_type)).setText(myMap.get("PatientBloodType"));
                    ((TextView)findViewById(R.id.add_patient_allergy)).setText(myMap.get("PatientAllergy"));
                }
            }
        }
    }
}
