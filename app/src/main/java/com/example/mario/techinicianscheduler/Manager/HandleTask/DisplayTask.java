package com.example.mario.techinicianscheduler.Manager.HandleTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;

import java.util.ArrayList;

public class DisplayTask extends AppCompatActivity {

    private TextView displayName;
    private TextView displaySkillReq;
    private TextView displayStationName;
    private TextView displayDuration;
    private TextView displayDescription;

    private ImageButton quitDisplayTask;

    private Bundle techInfo;
    private int chosenId;
    private ArrayList<Task> existTasks;
    private Task selectedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_task);
        initialize();

        chosenId=techInfo.getInt("selectedTask");
        existTasks=techInfo.getParcelableArrayList("arrangedTasks");
        selectedTask=existTasks.get(chosenId-1);

        displayName.setText(selectedTask.getName());
        displaySkillReq.setText(selectedTask.getSkillRequirement()+"");
        displayStationName.setText(selectedTask.getStationName());
        displayDuration.setText(selectedTask.getDuration()+"");
        displayDescription.setText(selectedTask.getDescription());

        quitDisplayTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initialize() {
        displayName=(TextView) findViewById(R.id.displayTaskName);
        displaySkillReq=(TextView) findViewById(R.id.displayTaskSkillReq);
        displayStationName=(TextView) findViewById(R.id.displayTaskStation);
        displayDuration=(TextView) findViewById(R.id.displayTaskDuration);
        displayDescription=(TextView) findViewById(R.id.displayTaskDescrip);
        techInfo=getIntent().getExtras();
        quitDisplayTask=(ImageButton)findViewById(R.id.quitDisplayTask);
    }
}
