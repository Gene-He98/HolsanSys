package com.volcano.holsansys.login;

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

import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.MD5;

import java.util.HashMap;

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
        Login();
    }

    private void Login() {
        tv_username = findViewById(R.id.user_name_login);
        tv_user_pass = findViewById(R.id.user_pass_login);
        String myUserID=tv_username.getText().toString();
        String myPassword=(new MD5()).EncryptToMD5(tv_user_pass.getText().toString());
        if(myUserID.equals("")||myPassword.equals("d41d8cd98f00b204e9800998ecf8427e")){
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
            String[] myParamsArr={myUserID, myPassword};
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
        //String..., it denotes a list with alterable number of params, and the type of the params are String
        protected String doInBackground(String... myParams)
        {
            String myResult="";

            try
            {
                myResult = (new UserLoginVerification()).ConnectingWebService("UserLogin"
                        ,myParams[0],myParams[1]);
                System.out.println("myResult"+myResult);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //Assigning the returned result to method onPostExecute()
            return myResult;
        }

        @Override
        protected void onProgressUpdate(Integer... myValues)
        {
            super.onProgressUpdate(myValues);
        }

        @Override
        //this method can modify UI in main thread
        protected void onPostExecute(String myResult)
        {
            //Showing the returned result from WebService
            /*Gson myGson = new Gson();
            List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
            System.out.println("myList"+myList);*/

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
            }
            else {
                MainActivity.admin_flag=true;
                setResult(30); //设置返回数据
                progress_login.setVisibility(View.GONE);
                LoginActivity.this.finish();
            }

            /*try
            {
                Map<String,String> myMap=myList.get(0);
            String myUserID=myMap.get("UserID");
            String myUserName=myMap.get("UserName");
            String myUserAuthorityIDStr=myMap.get("UserAuthorityIDStr");

            //Jump from page UserLogin to page MainPage
            Intent myIntent=new Intent(LoginActivity.this, MainActivity.class);
            myIntent.putExtra("UserID", myUserID);
            myIntent.putExtra("UserName", myUserName);
            myIntent.putExtra("UserAuthorityIDStr",myUserAuthorityIDStr);
            startActivity(myIntent);


        }
            catch (Exception ex){}*/


        }
    }

    public void text_forget(View view) {
    }

    public void text_register(View view) {
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
            name = data.getStringExtra("name");
            Intent a = new Intent();
            a.putExtra("name", name);
            setResult(30, data); //设置返回数据
            this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
