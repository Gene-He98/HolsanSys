package com.volcano.holsansys.add;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
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

public class AddMedicineActivity extends AppCompatActivity {

    private Toast mToast=null;
    private String oriMedicineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        Intent intent= getIntent();
        if(intent.getStringExtra("kind").equals("add")){
            setTitle("添加新药品");
            findViewById(R.id.add_medicine).setVisibility(View.VISIBLE);
            findViewById(R.id.change_medicine_ll).setVisibility(View.GONE);
        }
        else if(intent.getStringExtra("kind").equals("change")){
            setTitle("编辑药品");
            findViewById(R.id.add_medicine).setVisibility(View.GONE);
            findViewById(R.id.change_medicine_ll).setVisibility(View.VISIBLE);
            oriMedicineName=intent.getStringExtra("MedicineName");
            String[] myParamsArr={"MedicineDetail", MainActivity.userID,oriMedicineName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
    }

    public void Add_Medicine(View view) {
        if(((TextView)findViewById(R.id.add_medicine_name)).getText().toString().equals("")){
            if (mToast == null) {
                mToast=Toast.makeText(AddMedicineActivity.this,
                        "请填写药品名称！",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }else {
            String medicineName = ((TextView)findViewById(R.id.add_medicine_name))
                    .getText().toString();
            String medicineAnotherName = ((TextView)findViewById(R.id.add_medicine_another_name))
                    .getText().toString();
            String medicineUsage = ((TextView)findViewById(R.id.add_medicine_usage))
                    .getText().toString();
            String medicineDosage = ((TextView)findViewById(R.id.add_medicine_dosage))
                    .getText().toString();
            String medicineCautions = ((TextView)findViewById(R.id.add_medicine_cautions))
                    .getText().toString();
            DatePicker validity = findViewById(R.id.add_medicine_validity);
            String medicineValidity =validity.getYear()+"-"+(validity.getMonth()+1)+"-"+validity.getDayOfMonth();
            if(medicineAnotherName.equals("")){
                medicineAnotherName="无";
            }
            if(medicineUsage.equals("")){
                medicineUsage="未填写";
            }
            if(medicineDosage.equals("")){
                medicineDosage="未填写";
            }
            if(medicineCautions.equals("")){
                medicineCautions="未填写";
            }
            String[] myParamsArr={"AddMedicine",MainActivity.userID,medicineName,medicineAnotherName,
                    medicineUsage,medicineDosage,medicineCautions,medicineValidity};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
            MainActivity.refreshManageFlag =true;
            AddMedicineActivity.this.finish();
        }
    }

    public void Change_Medicine(View view) {
        if(((TextView)findViewById(R.id.add_medicine_name)).getText().toString().equals("")){
            if (mToast == null) {
                mToast=Toast.makeText(AddMedicineActivity.this,
                        "请填写药品名称！",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }else {
            String medicineName = ((TextView)findViewById(R.id.add_medicine_name))
                    .getText().toString();
            String medicineAnotherName = ((TextView)findViewById(R.id.add_medicine_another_name))
                    .getText().toString();
            String medicineUsage = ((TextView)findViewById(R.id.add_medicine_usage))
                    .getText().toString();
            String medicineDosage = ((TextView)findViewById(R.id.add_medicine_dosage))
                    .getText().toString();
            String medicineCautions = ((TextView)findViewById(R.id.add_medicine_cautions))
                    .getText().toString();
            DatePicker validity = findViewById(R.id.add_medicine_validity);
            String medicineValidity =validity.getYear()+"-"+(validity.getMonth()+1)+"-"+validity.getDayOfMonth();
            if(medicineAnotherName.equals("")){
                medicineAnotherName="无";
            }
            if(medicineUsage.equals("")){
                medicineUsage="未填写";
            }
            if(medicineDosage.equals("")){
                medicineDosage="未填写";
            }
            if(medicineCautions.equals("")){
                medicineCautions="未填写";
            }
            String[] myParamsArr={"ChangeMedicine",MainActivity.userID,medicineName,medicineAnotherName,
                    medicineUsage,medicineDosage,medicineCautions,medicineValidity,oriMedicineName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
            MainActivity.refreshManageFlag =true;
            AddMedicineActivity.this.finish();
        }
    }

    public void Delete_Medicine(View view){
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(AddMedicineActivity.this);
        normalDialog.setTitle("删除家居药物");
        normalDialog.setMessage("是否确定删除？");
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] myParamsArr ={"DeleteMedicine",MainActivity.userID,oriMedicineName};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
                MainActivity.refreshManageFlag =true;
                AddMedicineActivity.this.finish();
            }
        });
        normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        normalDialog.setCancelable(false);
        // 显示
        normalDialog.show();
    }

    class VerifyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress_add_medicine).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... myParams) {
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
        protected void onPostExecute(String myResult) {
            //查询结果为成功，则跳转到主页面
            if(myResult.equals("[{\"msg\":\"ok\"}]")){
                MainActivity.refreshManageFlag =true;
            }
            else if (myResult.equals("[{\"msg\":\"error\"}]")){
                if (mToast == null) {
                    mToast= Toast.makeText(AddMedicineActivity.this,
                            "添加失败，请稍后重试！",Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    TextView v = (TextView) mToast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);     //设置字体颜色
                    v.setTextSize(20);
                }
                else {
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
            else{
                Gson myGson = new Gson();
                List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                try {
                    Map<String,String> myMap=myList.get(0);
                    ((TextView)findViewById(R.id.add_medicine_name)).setText(myMap.get("MedicineName"));
                    ((TextView)findViewById(R.id.add_medicine_another_name)).setText(myMap.get("MedicineAnotherName"));
                    ((TextView)findViewById(R.id.add_medicine_usage)).setText(myMap.get("Usage"));
                    ((TextView)findViewById(R.id.add_medicine_dosage)).setText(myMap.get("Dosage"));
                    ((TextView)findViewById(R.id.add_medicine_cautions)).setText(myMap.get("Cautions"));
                    int year=Integer.parseInt(myMap.get("Validity").split("-")[0]);
                    int month=Integer.parseInt(myMap.get("Validity").split("-")[1]);
                    int day=Integer.parseInt(myMap.get("Validity").split("-")[2]);
                    ((DatePicker)findViewById(R.id.add_medicine_validity)).updateDate(year, month-1, day);
                }
                catch (Exception ex){}
            }
            findViewById(R.id.progress_add_medicine).setVisibility(View.GONE);
        }
    }
}
