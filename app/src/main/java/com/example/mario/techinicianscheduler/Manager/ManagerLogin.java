package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.SignUp;
import com.example.mario.techinicianscheduler.Technician.TechnicianLogin;

public class ManagerLogin extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button signup;
    private TextView technician;

    private EditText managerUsername;
    private EditText managerPassword;
    private SharedPreferences sharedPreferences;
    private int retCode;

    private String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        initialize();
        login.setOnClickListener(this);
        signup.setOnClickListener(this);

        spinnerHandle();




    }

    private void initialize() {
        login=(Button)findViewById(R.id.managerLogIn);
        signup=(Button)findViewById(R.id.managerSignUp);
        technician=(TextView) findViewById(R.id.goTechnician);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.managerLogIn:
                //log in issue.
                break;
            case R.id.managerSignUp:
                Intent intent=new Intent(ManagerLogin.this, SignUp.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void spinnerHandle(){
        String prompt=technician.getText().toString();
        SpannableString spannedString=new SpannableString(prompt);
        spannedString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManagerLogin.this, TechnicianLogin.class);
                startActivity(intent);
                finish();
            }
        },0,prompt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        technician.setText(spannedString);
        technician.setMovementMethod(LinkMovementMethod.getInstance());

    }

}
