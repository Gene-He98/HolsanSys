package com.volcano.holsansys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mob.MobSDK;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class LoginActivity extends AppCompatActivity {

    static String phone;

    private EditText tv_username;
    private EditText tv_user_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void Admin_Click(View view) {
        this.tv_username = view.findViewById(R.id.user_name_login);
        this.tv_user_pass = view.findViewById(R.id.user_pass_login);
        //检测账号密码是否正确
        if(true){
            MainActivity.admin_flag=true;
            setResult(30); //设置返回数据
            this.finish();
        }else
            MainActivity.admin_flag=false;
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

                    Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
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
