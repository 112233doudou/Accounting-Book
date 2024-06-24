package com.example.zuoye;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        usernameEditText = findViewById(R.id.forgot_password_username);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                // 这里可以添加你的重置密码逻辑，比如发送重置链接到用户的邮箱
                Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent", Toast.LENGTH_SHORT).show();
                // 发送完成后跳转回登录界面
                finish();
            }
        });
    }
}
