package com.example.mario.techinicianscheduler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mario.techinicianscheduler.Manager.ManagerLogin;
import com.example.mario.techinicianscheduler.Technician.TechnicianLogin;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button manager;
    private Button technician;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        manager.setOnClickListener(this);
        technician.setOnClickListener(this);

    }

    private void initialize() {
        manager=(Button)findViewById(R.id.manager);
        technician=(Button)findViewById(R.id.technician);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.manager:
                Intent intent=new Intent(MainActivity.this, ManagerLogin.class);
                startActivity(intent);
                break;
            case R.id.technician:
                Intent intent2=new Intent(MainActivity.this, TechnicianLogin.class);
                startActivity(intent2);
                break;
        }
    }
}
