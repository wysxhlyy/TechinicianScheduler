package com.example.mario.techinicianscheduler.Manager;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;

public class ManagerDashboard extends AppCompatActivity {

    private TextView username;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        username=(TextView)findViewById(R.id.loggedManagerUsername);
        sharedPreferences=getSharedPreferences("managerLogin",MODE_PRIVATE);

        username.setText(sharedPreferences.getString("username","username"));
    }
}
