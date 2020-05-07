package com.volcano.holsansys.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.MD5;
import com.volcano.holsansys.tools.WebServiceAPI;

public class ForgetPasswordActivity extends AppCompatActivity {

    private LinearLayout progress_changing;
    private EditText newPassword;
    private EditText newPasswordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        progress_changing =findViewById(R.id.progress_forget_password);
        newPassword=findViewById(R.id.new_password);
        newPasswordAgain=findViewById(R.id.new_password_again);
    }

    public void forgetClick(View view) {
        if(newPassword.getText().toString().equals("")){
            Toast toast = Toast.makeText(ForgetPasswordActivity.this,"请输入新的密码！",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL , 0, 0);  //设置显示位置
            toast.show();
        }else if(newPassword.getText().length()<8){
            Toast toast = Toast.makeText(ForgetPasswordActivity.this,"请输入至少八位密码！",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL , 0, 0);  //设置显示位置
            toast.show();
            newPassword.setText("");
            newPasswordAgain.setText("");
        }else if(!newPassword.getText().toString().equals(newPasswordAgain.getText().toString())){
            Toast toast = Toast.makeText(ForgetPasswordActivity.this,"两次密码输入不同！",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL , 0, 0);  //设置显示位置
            toast.show();
            newPassword.setText("");
            newPasswordAgain.setText("");
        }else {
            String[] myParamsArr={"ForgetPassword", LoginActivity.phone,(new MD5()).EncryptToMD5(newPassword.getText().toString())};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
    }

    class VerifyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progress_changing.setVisibility(View.VISIBLE);
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
            if(myResult.equals("[{\"msg\":\"ok\"}]")){
                setResult(40); //设置返回数据
                progress_changing.setVisibility(View.GONE);
                ForgetPasswordActivity.this.finish();
            }
            else {
                Toast toast = Toast.makeText(ForgetPasswordActivity.this,"修改失败",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL , 0, 0);  //设置显示位置
                toast.show();
            }
        }
    }
}
