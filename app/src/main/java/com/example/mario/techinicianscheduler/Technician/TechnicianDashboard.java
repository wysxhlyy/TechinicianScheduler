package com.example.mario.techinicianscheduler.Technician;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;

public class TechnicianDashboard extends AppCompatActivity {

    private TextView loggedTechUsername;

    private Button workArrangement;
    private Button route;
    private Button techSettings;
    private Button techQuit;

    private Bundle techInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        initialize();

        loggedTechUsername.setText("Welcome Back: "+ techInfo.getString("technicianName"));
    }

    private void initialize() {
        loggedTechUsername=(TextView)findViewById(R.id.loggedTechUsername);
        workArrangement=(Button)findViewById(R.id.workArrangement);
        route=(Button)findViewById(R.id.route);
        techSettings=(Button)findViewById(R.id.techSettings);
        techQuit=(Button)findViewById(R.id.techQuit);
        techInfo=getIntent().getExtras();
    }
}
