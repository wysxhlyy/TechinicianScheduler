package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mario.techinicianscheduler.R;

public class ScheduleStep2 extends AppCompatActivity implements View.OnClickListener {

    private EditText skillLevel1;
    private EditText stationId1;
    private EditText skillLevel2;
    private EditText stationId2;
    private EditText skillLevel3;
    private EditText stationId3;
    private EditText skillLevel4;
    private EditText stationId4;
    private EditText skillLevel5;
    private EditText stationId5;

    private Button addTaskBtn;
    private Button next2;

    private Bundle tasks;
    private int taskCount=1;
    private int numOfTask=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_step2);

        initialize();
        numOfTask=Integer.parseInt(getIntent().getStringExtra("numOfTasks"));
        tasks.putInt("numOfTasks",numOfTask);

        //根据numOfTasks展示task输入框

        addTaskBtn.setOnClickListener(this);
        next2.setOnClickListener(this);
    }

    private void initialize() {
        skillLevel1=(EditText)findViewById(R.id.skillLevel1);
        skillLevel2=(EditText)findViewById(R.id.skillLevel2);
        skillLevel3=(EditText)findViewById(R.id.skillLevel3);
        skillLevel4=(EditText)findViewById(R.id.skillLevel4);
        skillLevel5=(EditText)findViewById(R.id.skillLevel5);
        stationId1=(EditText)findViewById(R.id.stationId1);
        stationId2=(EditText)findViewById(R.id.stationId2);
        stationId3=(EditText)findViewById(R.id.stationId3);
        stationId4=(EditText)findViewById(R.id.stationId4);
        stationId5=(EditText)findViewById(R.id.stationId5);
        addTaskBtn=(Button)findViewById(R.id.addTaskBtn);
        next2=(Button)findViewById(R.id.next2);
        tasks=new Bundle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addTaskBtn:
                addTask();
                break;
            case R.id.next2:
                getTaskData();
                Intent intent=new Intent(ScheduleStep2.this,ScheduleStep3.class);
                intent.putExtras(tasks);
                startActivity(intent);
                break;
        }
    }

    private void getTaskData() {
        if(skillLevel1.getText().toString()==null||stationId1.getText().toString()==null){
        }else {
            tasks.putString("task"+taskCount,skillLevel1.getText().toString()+","+stationId1.getText().toString());
            taskCount++;
        }

        if(skillLevel2.getText().toString()==null||stationId2.getText().toString()==null){
        }else {
            tasks.putString("task"+taskCount,skillLevel2.getText().toString()+","+stationId2.getText().toString());
            taskCount++;
        }

        if(skillLevel3.getText().toString()==null||stationId3.getText().toString()==null){
        }else {
            tasks.putString("task"+taskCount,skillLevel3.getText().toString()+","+stationId3.getText().toString());
            taskCount++;
        }
    }

    private void addTask() {
        //add task when tasks' number is larger than 5.
    }
}
