package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.Manager.ManagerLogin;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.SignUp;

public class TechnicianLogin extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button signup;
    private TextView technician;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_login);

        initialize();
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        String prompt=technician.getText().toString();


        SpannableString spannedString=new SpannableString(prompt);
        spannedString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TechnicianLogin.this, ManagerLogin.class);
                startActivity(intent);
                finish();
            }
        },0,prompt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        technician.setText(spannedString);
        technician.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void initialize() {
        login=(Button)findViewById(R.id.techLogIn);
        signup=(Button)findViewById(R.id.techSignUp);
        technician=(TextView) findViewById(R.id.goManager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.techLogIn:
                //log in issue.
                break;
            case R.id.techSignUp:
                Intent intent=new Intent(TechnicianLogin.this, SignUp.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
