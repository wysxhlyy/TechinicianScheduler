package com.example.mario.techinicianscheduler.Manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;
import com.example.mario.techinicianscheduler.TechnicianInfo;

import java.util.ArrayList;

public class ScheduleResult extends AppCompatActivity {

    private TextView getPlanInfo;
    private String showData="";
    private ArrayList<TechnicianInfo> techs;
    private ArrayList<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_result);

        initialize();


        Bundle planInfo=getIntent().getExtras();
        int numOfTasks=planInfo.getInt("numOfTasks");
        int chosenTechNum=planInfo.getInt("chosenTechNum");
        showData+="number of task: "+numOfTasks+", chosen technicain number: "+chosenTechNum+"\n";

        for(int i=1;i<4;i++){
            String[] string=planInfo.getString("task"+i).split(",");
            Task t=new Task();
            t.setSkillRequirement(string[0]);
            t.setStationId(string[1]);
            tasks.add(t);
            showData+="task skill level and station id: "+planInfo.getString("task"+i)+"\n";
        }

        for(int i=1;i<chosenTechNum+1;i++){
            TechnicianInfo tInfo=new TechnicianInfo();
            tInfo.setFirstName(planInfo.getString("chosenTech"+i));
            tInfo.setSkillLevel(planInfo.getString("chosenTechLevel"+i));
            tInfo.setWorkHour(planInfo.getString("chosenTechWorkHour"+i));
            techs.add(tInfo);
            showData+="chosen technician name: "+planInfo.getString("chosenTech"+i)+",skill level: "+planInfo.getString("chosenTechLevel"+i)+", work hour: "+planInfo.getString("chosenTechWorkHour"+i)+ "\n";
        }

        getPlanInfo.setText(showData);
    }


    private void initialize() {
        techs=new ArrayList<>();
        tasks=new ArrayList<>();
        getPlanInfo=(TextView)findViewById(R.id.getPlanInfo);
    }


}
