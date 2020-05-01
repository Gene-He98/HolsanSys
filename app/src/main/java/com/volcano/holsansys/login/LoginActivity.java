package com.volcano.holsansys.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.MobSDK;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.MD5;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class LoginActivity extends AppCompatActivity {

    static String phone;

    private EditText tv_username;
    private EditText tv_user_pass;
    private LinearLayout progress_login;
    private Toast mToast=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void Admin_Click(View view) {
        progress_login =findViewById(R.id.progress_login);
        tv_username = findViewById(R.id.user_name_login);
        tv_user_pass = findViewById(R.id.user_pass_login);

        String myUserID=tv_username.getText().toString();
        String myPassword=(new MD5()).EncryptToMD5(tv_user_pass.getText().toString());
        if(myUserID.equals("")||tv_user_pass.getText().toString().equals("")){
            if (mToast == null) {
                mToast=Toast.makeText(LoginActivity.this,
                        "请输入完整的用户名密码！",Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                TextView v = (TextView) mToast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.RED);     //设置字体颜色
                v.setTextSize(20);
            } else {
                mToast.setText("请输入完整的用户名密码！");
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }else {
            String[] myParamsArr={"UserLogin",myUserID, myPassword};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
    }

    //后台执行登录查询功能
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
            //Showing the returned result from WebService


            //查询结果若为空，则登录失败，否则登录成功
            if(myResult.equals("[]")){
                MainActivity.admin_flag=false;
                if (mToast == null) {
                    mToast=Toast.makeText(LoginActivity.this,
                            "用户名或密码错误，请重新登录！",Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    TextView v = (TextView) mToast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);     //设置字体颜色
                    v.setTextSize(20);
                } else {
                    mToast.setText("用户名或密码错误，请重新登录！");
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                progress_login.setVisibility(View.GONE);
                mToast.show();
                tv_username.setText("");
                tv_user_pass.setText("");
            }else if(myResult.equals("操作失败")){
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(LoginActivity.this);
                normalDialog.setTitle("网络连接似乎出了问题");
                normalDialog.setMessage("是否重新尝试？");
                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(404);
                        LoginActivity.this.finish();
                    }
                });
                normalDialog.setCancelable(false);
                // 显示
                normalDialog.show();
            }
            else {
                Gson myGson = new Gson();
                List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                try {
                    Map<String,String> myMap=myList.get(0);
                    String myUserID=myMap.get("UserID");
                    String myUserName=myMap.get("UserName");
                    MainActivity.admin_flag=true;
                    Intent data = new Intent();
                    data.putExtra("userID", myUserID);
                    data.putExtra("userName", myUserName);
                    setResult(30, data); //设置返回数据
                    progress_login.setVisibility(View.GONE);
                    LoginActivity.this.finish();

                }
                catch (Exception ex){}
            }
        }
    }

    public void text_forget(View view) {
    }

    public void text_register(View view) {

        MobSDK.submitPolicyGrantResult(true,null);

        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    // 手机号码，如“13800138000”
                    phone = (String) phoneMap.get("phone");
                    // TODO 利用国家代码和手机号码进行后续的操作

                    Intent intent =new Intent(LoginActivity.this, RegisterActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 100); //两个参数：第一个是意图对象，第二个是请求码requestCode
                } else{
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(LoginActivity.this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name;
        if(resultCode == 30){  //判断返回码是否是30
            name = data.getStringExtra("userName");
            Intent a = new Intent();
            a.putExtra("userName", name);
            a.putExtra("userID",phone);
            setResult(30, data); //设置返回数据
            this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
