package com.example.mario.techinicianscheduler.Manager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;

public class ScheduleResult extends AppCompatActivity {

    private TextView getPlanInfo;
    private String showData="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_result);

        Bundle planInfo=getIntent().getExtras();
        int numOfTasks=planInfo.getInt("numOfTasks");
        int chosenTechNum=planInfo.getInt("chosenTechNum");
        showData+="number of task: "+numOfTasks+", chosen technicain number: "+chosenTechNum+"\n";

        for(int i=1;i<4;i++){
            showData+="task skill level and station id: "+planInfo.getString("task"+i)+"\n";
        }

        for(int i=1;i<chosenTechNum+1;i++){
            showData+="chosen technician name: "+planInfo.getString("chosenTech"+i)+"\n";
        }

        getPlanInfo=(TextView)findViewById(R.id.getPlanInfo);
        getPlanInfo.setText(showData);
    }
}
