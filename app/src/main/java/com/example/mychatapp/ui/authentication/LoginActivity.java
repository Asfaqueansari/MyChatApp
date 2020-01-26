package com.example.mychatapp.ui.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychatapp.MainActivity;
import com.example.mychatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private Button mLogin_btn;
    private TextInputEditText mLoginEmail,mLoginPassword;


    private ProgressDialog mLoginProgress;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.login_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Log In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLogin_btn = findViewById(R.id.login_btn);
        mLoginEmail = findViewById(R.id.edt_login_email);
        mLoginPassword = findViewById(R.id.edt_login_Password);

        mLoginProgress = new ProgressDialog(this);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getEditableText().toString();
                String password = mLoginPassword.getEditableText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mLoginProgress.setTitle("Loging in");
                    mLoginProgress.setMessage("please wait...");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email,password);
                }

            }
        });

    }

    private void loginUser(String email, String password) {
    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if(task.isSuccessful()){

              mLoginProgress.dismiss();

              String  current_user_id = mAuth.getCurrentUser().getUid();
              String  deviceToken = FirebaseInstanceId.getInstance().getToken();

              mUserDatabase.child(current_user_id).child("device_token")
                      .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {

                      Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                      mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                      startActivity(mainIntent);
                      finish();
                  }
              });
          }
          else
          {
              mLoginProgress.hide();
              Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_LONG).show();
          }
        }
    });
    }
}
