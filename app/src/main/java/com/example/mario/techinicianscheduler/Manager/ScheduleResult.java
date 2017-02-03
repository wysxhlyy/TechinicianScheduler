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
    private ArrayList<TechnicianInfo> chosenTechs;
    private ArrayList<Task> tasks;
    private int numOfTasks;
    private int chosenTechNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_result);

        initialize();


        Bundle planInfo=getIntent().getExtras();
        chosenTechs= planInfo.getParcelableArrayList("chosenTech");
        tasks=planInfo.getParcelableArrayList("addedTask");


        numOfTasks=planInfo.getInt("numOfTask");
        chosenTechNum=planInfo.getInt("numOfChosenTech");

        showScheduleInfo();

        for(int i=0;i<numOfTasks;i++){
            Task t= (Task) planInfo.getSerializable("addedTask"+i);
        }


//        showData+="number of task: "+numOfTasks+", chosen technicain number: "+chosenTechNum+"\n";
//
//        for(int i=0;i<tasks.size();i++){
////            String[] string=planInfo.getString("task"+i).split(",");
////            Task t=new Task();
////            t.setSkillRequirement(string[0]);
////            t.setStationId(string[1]);
////            tasks.add(t);
//            showData+="skill requirement"+tasks.get(i).getSkillRequirement()+", latitude："+tasks.get(i).getPosition().latitude+", longitude: "+tasks.get(i).getPosition().longitude;
//        }
//
////        for(int i=1;i<chosenTechNum+1;i++){
////            TechnicianInfo tInfo=new TechnicianInfo();
////            tInfo.setFirstName(planInfo.getString("chosenTech"+i));
////            tInfo.setSkillLevel(planInfo.getString("chosenTechLevel"+i));
////            tInfo.setWorkHour(planInfo.getString("chosenTechWorkHour"+i));
////            chosenTechs.add(tInfo);
////            showData+="chosen technician name: "+planInfo.getString("chosenTech"+i)+",skill level: "+planInfo.getString("chosenTechLevel"+i)+", work hour: "+planInfo.getString("chosenTechWorkHour"+i)+ "\n";
////        }
//
//        getPlanInfo.setText(showData);
    }

    private void showScheduleInfo() {
        showData+="number of tasks:"+numOfTasks+", number of technicians:"+chosenTechNum+"\n\n";
        for(int i=0;i<tasks.size();i++){
            Task t=tasks.get(i);
            showData+="task"+i+":\n task skill requirement: "+t.getSkillRequirement()+", station Name: "+t.getStationName() + ", task position: "+t.getPosition().latitude+","+t.getPosition().longitude+", task Duration: "+t.getDuration()+"\n\n";
        }

        for(int i=0;i<chosenTechs.size();i++){
            TechnicianInfo t=chosenTechs.get(i);
            showData+="technician"+i+":\n technician name: "+t.getFirstName()+", technician Skill"+t.getSkillLevel()+", technician work hour:"+t.getWorkHour()+"\n\n";
        }

        getPlanInfo.setText(showData);
    }


    private void initialize() {
        chosenTechs=new ArrayList<>();
        tasks=new ArrayList<>();
        getPlanInfo=(TextView)findViewById(R.id.getPlanInfo);
    }


}
