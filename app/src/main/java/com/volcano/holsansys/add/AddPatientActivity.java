package com.volcano.holsansys.add;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

public class AddPatientActivity extends AppCompatActivity {
    private Toast mToast=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
    }

    public void Add_Patient(View view) {
        String patientName = ((TextView)findViewById(R.id.add_patient_name))
                .getText().toString();
        String patientAge = ((TextView)findViewById(R.id.add_patient_age))
                .getText().toString();
        String patientSex = ((TextView)findViewById(R.id.add_patient_sex))
                .getText().toString();
        String patientAddress = ((TextView)findViewById(R.id.add_patient_address))
                .getText().toString();
        String patientBloodType = ((TextView)findViewById(R.id.add_patient_blood_type))
                        .getText().toString();
        String patientMedicalHistory = ((TextView)findViewById(R.id.add_patient_medical_history))
                        .getText().toString();
        String patientAllergy = ((TextView)findViewById(R.id.add_patient_allergy))
                        .getText().toString();
        String[] myParamsArr={"AddPatient",MainActivity.userID,patientName,patientAge,
                patientSex,patientAddress,patientBloodType,patientMedicalHistory,patientAllergy};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
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
                AddPatientActivity.this.finish();
            }
            else {
                if (mToast == null) {
                    mToast=Toast.makeText(AddPatientActivity.this,
                            "添加失败，请稍后重试！",Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    TextView v = (TextView) mToast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);     //设置字体颜色
                    v.setTextSize(20);
                } else {
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        }
    }
}
