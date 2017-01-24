package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;

public class TechnicianDashboard extends AppCompatActivity implements View.OnClickListener {

    private TextView loggedTechUsername;

    private Button workArrangement;
    private Button route;
    private Button techSettings;
    private Button techQuit;

    private Bundle techInfo;
    private String technicianId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        initialize();

        loggedTechUsername.setText("Welcome Back: "+ techInfo.getString("technicianName"));
        workArrangement.setOnClickListener(this);
    }

    private void initialize() {
        loggedTechUsername=(TextView)findViewById(R.id.loggedTechUsername);
        workArrangement=(Button)findViewById(R.id.workArrangement);
        route=(Button)findViewById(R.id.route);
        techSettings=(Button)findViewById(R.id.techSettings);
        techQuit=(Button)findViewById(R.id.techQuit);
        techInfo=getIntent().getExtras();
        technicianId=techInfo.getString("technicianId");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.workArrangement:
                Intent intent=new Intent(TechnicianDashboard.this,TechnicianTasks.class);
                Bundle bundle=new Bundle();
                bundle.putString("technicianId",technicianId);
                bundle.putString("technicianName",techInfo.getString("technicianName"));
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
    }
}
