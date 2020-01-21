package com.example.mychatapp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mychatapp.R;
import com.example.mychatapp.ui.authentication.LoginActivity;
import com.example.mychatapp.ui.authentication.RegisterActivity;

public class StartActivity extends AppCompatActivity {

    private Button mregbtn;
    private Button mloginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mloginbtn =(Button)findViewById(R.id.start_login_btn);
        mregbtn = (Button)findViewById(R.id.start_reg_btn);
        mregbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
