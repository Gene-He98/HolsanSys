package com.volcano.holsansys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText tv_username;
    private EditText tv_user_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void Admin_Click(View view) {
        this.tv_username = findViewById(R.id.user_name);
        this.tv_user_pass = findViewById(R.id.user_pass);
        //检测账号密码是否正确
        if(tv_username.getText().toString().equals(tv_user_pass.getText().toString())){
            MainActivity.admin_flag=true;
            setResult(30); //设置返回数据
            this.finish();
        }else
            MainActivity.admin_flag=false;
    }

    public void text_forget(View view) {
    }

    public void text_register(View view) {
    }
}
