package com.volcano.holsansys.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.MD5;
import com.volcano.holsansys.tools.WebServiceAPI;

public class RegisterActivity extends AppCompatActivity {

    private String phone;
    private EditText username;
    private EditText user_pass;
    private CheckBox protocol;
    private EditText user_pass_again;
    private LinearLayout progress_login;
    private Toast mToast=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phone= LoginActivity.phone;
        progress_login =findViewById(R.id.progress_Register);
        protocol=findViewById(R.id.checkbox_protocol);
        username=(EditText)findViewById(R.id.fra_user_name);
        user_pass=(EditText)findViewById(R.id.user_pass);
        user_pass_again=(EditText)findViewById(R.id.user_pass_again);

    }

    public void Reg_Click(View view) {
        if(username.getText().toString().equals("") ||user_pass.getText().toString().equals("")
                ||user_pass_again.getText().toString().equals("")) {
            Toast toast= Toast.makeText(RegisterActivity.this, "请将所有内容补充完整", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } else if (!user_pass.getText().toString().equals(user_pass_again.getText().toString())) {
            Toast toast=Toast.makeText(RegisterActivity.this, "两次登录密码输入不一致，请重新输入", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } else if (user_pass.getText().toString().length()!=8 || user_pass_again.getText().toString().length()!=8){
            Toast toast=Toast.makeText(RegisterActivity.this, "请输入完整8位登录密码", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }  else if (!protocol.isChecked()){
            Toast toast=Toast.makeText(RegisterActivity.this, "请阅读并同意服务协议，才可注册", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else {
            String[] myParamsArr={"UserRegister",phone, username.getText().toString(),(new MD5()).EncryptToMD5(user_pass.getText().toString())};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
    }


    class VerifyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progress_login.setVisibility(View.VISIBLE);
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
                MainActivity.admin_flag=true;
                Intent data = new Intent();
                data.putExtra("userName", username.getText().toString());
                setResult(30, data); //设置返回数据
                progress_login.setVisibility(View.GONE);
                RegisterActivity.this.finish();
            }
            else {
                if (mToast == null) {
                    mToast=Toast.makeText(RegisterActivity.this,
                            "注册失败，请稍后重试！",Toast.LENGTH_SHORT);
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
