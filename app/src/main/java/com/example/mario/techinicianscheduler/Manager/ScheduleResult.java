package com.example.mario.techinicianscheduler.Manager;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;
import com.example.mario.techinicianscheduler.TechnicianInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleResult extends AppCompatActivity {

    private static int UNASSIGNEDTASKPENALTY =100;

    private TextView getPlanInfo;
    private String showData="";
    private ArrayList<TechnicianInfo> chosenTechs;
    private ArrayList<Task> tasks;
    private int numOfTasks;
    private int chosenTechNum;
    private ArrayList<Task> sortedTask;
    private ArrayList<TechnicianInfo> sortedTech;
    private LatLng startEnd=new LatLng(37.331629,-121.8923151);
    private ArrayList<Task> unassignedTask;
    private Double initialCost;
    private Double improveCost=10000000.0;

    private Button hillClimbing;

    private Map<Task,TechnicianInfo> initialResult;
    private Map<Task,TechnicianInfo> improveResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_result);

        initialize();


        Bundle planInfo=getIntent().getExtras();
        chosenTechs= planInfo.getParcelableArrayList("chosenTech");
        tasks=planInfo.getParcelableArrayList("addedTask");


        sortTaskBySkill();
        sortTechnicianBySkill();
        numOfTasks=planInfo.getInt("numOfTask");
        chosenTechNum=planInfo.getInt("numOfChosenTech");


        basicSchedule();
        initialCost=calculateCost(initialResult);      //calculate the cost of initial schedule (before hill climbing)
        //improveCost=calculateCost(improveResult);


        hillClimbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hillClimbing();
                showScheduleInfo();
            }
        });

        showScheduleInfo();

    }

    private void hillClimbing() {
        for(int i=0;i<initialResult.size()-1;i++){
            for(int j=i+1;j<initialResult.size();j++){
                ArrayList<Task> tasks=new ArrayList<>();
                tasks.addAll(initialResult.keySet());
                ArrayList<TechnicianInfo> techs=new ArrayList<>();
                techs.addAll(initialResult.values());

                if(tasks.get(i).getSkillRequirement()<=techs.get(j).getSkillLevel()&&tasks.get(j).getSkillRequirement()<=techs.get(i).getSkillLevel()){   //如果两者可以交换
                    Map<Task,TechnicianInfo> newResult=new HashMap<>();
                    newResult.putAll(initialResult);
                    newResult.remove(techs.get(i));
                    newResult.remove(techs.get(j));
                    newResult.put(tasks.get(i),techs.get(j));
                    newResult.put(tasks.get(j),techs.get(i));

                    Double cost= calculateCost(newResult);
                    if(cost<improveCost){
                        improveCost=cost;
                        Log.d("improved",improveCost+"");
                        improveResult.putAll(newResult);
                    }
                }
            }
        }
    }

    /**
     * Calculate the cost after the schedule.
     */
    private Double calculateCost(Map<Task,TechnicianInfo> schedule) {
        ArrayList<Task> availableTask= new ArrayList<>();
        unassignedTask.addAll(sortedTask);
        availableTask.addAll(schedule.keySet());

        double cost=0;

        for(int j=0;j<sortedTech.size();j++) {
            ArrayList<Task> assignedTask = new ArrayList<>();
            if (schedule.containsValue(sortedTech.get(j))) {  //There already exist some task assigned to the technician j.
                for (int k = 0; k < schedule.size(); k++) {
                    if (schedule.get(availableTask.get(k)) == sortedTech.get(j)) {
                        cost += calculateDur(availableTask.get(k).getSkillRequirement(), sortedTech.get(j).getSkillLevel(),availableTask.get(k).getDuration());    //calculate estimate time which is the sum of all the assigned task for this specific technician.
                        assignedTask.add(availableTask.get(k));
                        unassignedTask.remove(availableTask.get(k));
                    }
                }

                if(assignedTask.size()>=1){
                    float dist = calculateTravelTime(assignedTask);
                    cost += (dist / 1000) * 2;  //assume drive speed is 30km/h.
                }
            }
        }

        if(!unassignedTask.isEmpty()){
            cost+=UNASSIGNEDTASKPENALTY*unassignedTask.size();
            Log.d("unassigned Task:",unassignedTask.size()+"");
        }

        return cost;
    }

    /**
     * Test
     */
    private void showScheduleInfo() {
        showData="";
        showData+="number of tasks:"+numOfTasks+", number of technicians:"+chosenTechNum+"\n";

        for(int i=0;i<sortedTask.size();i++){
            Task t=sortedTask.get(i);
            showData+="task"+i+":\n task skill requirement: "+t.getSkillRequirement()+", station Name: "+t.getStationName() + ", task position: "+t.getPosition().latitude+","+t.getPosition().longitude+", task Duration: "+t.getDuration()+"\n";
        }

        for(int i=0;i<sortedTech.size();i++){
            TechnicianInfo t=sortedTech.get(i);
            showData+="technician"+i+":\n technician name: "+t.getFirstName()+", technician Skill"+t.getSkillLevel()+", technician work hour:"+t.getWorkHour()+"\n";
        }


        ArrayList<Task> availableTask= new ArrayList<>();
        availableTask.addAll(initialResult.keySet());

        for(int i=0;i<availableTask.size();i++){
            showData+="task"+availableTask.get(i).getSkillRequirement()+":"+initialResult.get(availableTask.get(i)).getFirstName()+"\n";
        }

        showData+="cost:"+initialCost.shortValue();

        if(!improveResult.isEmpty()){
            ArrayList<Task> improved=new ArrayList<>();
            improved.addAll(improveResult.keySet());
            for(int j=0;j<improved.size();j++){
                showData+="task"+improved.get(j).getSkillRequirement()+":"+improveResult.get(improved.get(j)).getFirstName()+"\n";
            }
        }



        getPlanInfo.setText(showData);

    }


    private void initialize() {
        chosenTechs=new ArrayList<>();
        tasks=new ArrayList<>();
        getPlanInfo=(TextView)findViewById(R.id.getPlanInfo);
        initialResult=new HashMap<>();
        improveResult=new HashMap<>();
        sortedTask=new ArrayList<>();
        sortedTech=new ArrayList<>();
        unassignedTask=new ArrayList<>();
        hillClimbing=(Button)findViewById(R.id.hillClimbing);
    }

    private void basicSchedule(){
        sortTaskBySkill();
        sortTechnicianBySkill();

        for(int i=0;i<sortedTask.size();i++){
            for(int j=0;j<sortedTech.size();j++){
                double estimateDur=0;
                ArrayList<Task> assignedTask=new ArrayList<>();

                if(sortedTask.get(i).getSkillRequirement()<=sortedTech.get(j).getSkillLevel()){     //if the estimate working duration < working hour,then assign this task to the technician.

                    if(initialResult.containsValue(sortedTech.get(j))){  //There already exist some task assigned to the technician j.
                        for(int k=0;k<i;k++){
                            if(initialResult.get(sortedTask.get(k))==sortedTech.get(j)){
                                estimateDur+=calculateDur(sortedTask.get(k).getSkillRequirement(),sortedTech.get(j).getSkillLevel(),sortedTask.get(k).getDuration());    //calculate estimate time which is the sum of all the assigned task for this specific technician.
                                assignedTask.add(sortedTask.get(k));
                            }
                        }
                        Log.d("estimate pre:",estimateDur+"");
                        estimateDur+=calculateDur(sortedTask.get(i).getSkillRequirement(),sortedTech.get(j).getSkillLevel(),sortedTask.get(i).getDuration());           //Add the new task being schedules.
                        assignedTask.add(sortedTask.get(i));

                        float dist=calculateTravelTime(assignedTask);
                        estimateDur+=(dist/1000)*2;  //assume drive speed is 30km/h.
                        Log.d("estimateDurAlready:"+j,estimateDur+" ");
                    }else {
                        estimateDur+=calculateDur(sortedTask.get(i).getSkillRequirement(),sortedTech.get(j).getSkillLevel(),sortedTask.get(i).getDuration());           //Add the new task being schedules.
                        assignedTask.add(sortedTask.get(i));

                        float dist=calculateTravelTime(assignedTask);
                        estimateDur+=(dist/1000)*2;  //assume drive speed is 30km/h.
                        Log.d("estimateDur:"+j,estimateDur+" ");
                    }


                    if(estimateDur<sortedTech.get(j).getWorkHour()){
                        initialResult.put(sortedTask.get(i),sortedTech.get(j));
                        break;
                    }
                }
            }
        }


    }

    private float calculateTravelTime(ArrayList<Task> travelTasks) {

            float total=0;
            for(int i=0;i<travelTasks.size()-1;i++){
                float[] distBetweenTwoNodes=new float[1];
                Location.distanceBetween(travelTasks.get(i).getPosition().latitude,travelTasks.get(i).getPosition().longitude
                ,travelTasks.get(i+1).getPosition().latitude,travelTasks.get(i+1).getPosition().longitude,distBetweenTwoNodes);
                total+=distBetweenTwoNodes[0];
            }
        float[] baseToStart=new float[1];
        float[] endToBase=new float[1];
        Location.distanceBetween(startEnd.latitude,startEnd.longitude,travelTasks.get(0).getPosition().latitude,travelTasks.get(0).getPosition().longitude,baseToStart);
        Location.distanceBetween(startEnd.latitude,startEnd.longitude,travelTasks.get(travelTasks.size()-1).getPosition().latitude,travelTasks.get(travelTasks.size()-1).getPosition().longitude,endToBase);
        total=total+baseToStart[0]+endToBase[0];

        Log.d("total",(total/1000)*2+"");

        return total;
    }


    private double calculateDur(int taskSkillReq,int techSkillLevel,int taskDuration ){
        double result=(double) taskSkillReq/(double) techSkillLevel;

        return result*taskDuration;
    }

    private void sortTaskBySkill() {
        int minSkillRequire=100;
        int minCount=0;
        if(tasks.size()>0){
            for(int i=0;i<tasks.size();i++){
                int skillRequire=tasks.get(i).getSkillRequirement();
                if(skillRequire<=minSkillRequire){
                    minSkillRequire=skillRequire;
                    minCount=i;
                }
            }
            Task t=tasks.get(minCount);
            sortedTask.add(t);
            tasks.remove(minCount);
            sortTaskBySkill();
        }
    }

    private void sortTechnicianBySkill(){
        int minSkillLevel=100;
        int minCount=0;
        if(chosenTechs.size()>0){
            for(int i=0;i<chosenTechs.size();i++){
                int skillLevel=chosenTechs.get(i).getSkillLevel();
                if(skillLevel<=minSkillLevel){
                    minSkillLevel=skillLevel;
                    minCount=i;
                }
            }
            TechnicianInfo t=chosenTechs.get(minCount);
            sortedTech.add(t);
            chosenTechs.remove(minCount);
            sortTechnicianBySkill();
        }
    }



}
