package com.volcano.holsansys;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private String phone;
    private EditText username;
    private EditText user_pass;
    private EditText user_pass_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone=LoginActivity.phone;
        username=(EditText)findViewById(R.id.user_name);
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
        } else {
            Intent data = new Intent();
            data.putExtra("name", username.getText().toString());
            setResult(30, data); //设置返回数据
            this.finish();
        }
    }
}
