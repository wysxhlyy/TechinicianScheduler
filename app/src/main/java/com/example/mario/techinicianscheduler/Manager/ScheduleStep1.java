package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mario.techinicianscheduler.R;

public class ScheduleStep1 extends AppCompatActivity {

    private EditText numOfTasks;
    private Button next1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_step1);

        initialize();

        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ScheduleStep1.this,ScheduleStep2.class);
                intent.putExtra("numOfTasks",numOfTasks.getText().toString());
                startActivity(intent);
            }
        });

    }

    private void initialize() {
        numOfTasks=(EditText)findViewById(R.id.numOfTasks);
        next1=(Button)findViewById(R.id.next1);
    }
}
