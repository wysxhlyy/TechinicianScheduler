package com.example.mario.techinicianscheduler.Manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;

public class ManagerDashboard extends AppCompatActivity {

    private TextView username;
    private Bundle managerInfo;

    private Button schedule;
    private Button manageTech;
    private Button settings;
    private Button quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        initialize();

        username.setText("Welcome Back: "+ managerInfo.getString("managerName"));

    }

    private void initialize() {
        username=(TextView)findViewById(R.id.loggedManagerUsername);
        schedule=(Button)findViewById(R.id.schedule);
        manageTech=(Button)findViewById(R.id.manageTech);
        settings=(Button)findViewById(R.id.managerSettings);
        quit=(Button)findViewById(R.id.managerQuit);
        managerInfo=getIntent().getExtras();

    }


}
