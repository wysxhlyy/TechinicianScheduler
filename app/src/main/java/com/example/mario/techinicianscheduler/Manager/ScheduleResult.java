package com.example.mario.techinicianscheduler.Manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.DBHelper;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;
import com.example.mario.techinicianscheduler.TechnicianInfo;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleResult extends AppCompatActivity {

    private static int UNASSIGNEDTASKPENALTY =3;

    private TextView getPlanInfo;
    private String showData="";
    private ArrayList<TechnicianInfo> chosenTechs;
    private ArrayList<Task> chosentasks;
    private ArrayList<Task> sortedTask;
    private ArrayList<TechnicianInfo> sortedTech;
    private LatLng startEnd=new LatLng(37.331629,-121.8923151);
    private Double initialCost;
    private Double improveCost=10000000.0;
    private Double minimumCost=10000.0;
    private Bundle managerInfo;

    private Button hillClimbing;
    private Button guidedLocalSearch;
    private Button back;
    private Button confirm;

    private Map<Task,TechnicianInfo> initialResult;
    private Map<Task,TechnicianInfo> improveResult;
    private Map<Task,TechnicianInfo> GLSresult;
    private Map<Task,TechnicianInfo> localOptima;

    private ArrayList<Integer> penalty;

    private JSONObject jsonObject;
    private int retCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_result);

        initialize();


        managerInfo=getIntent().getExtras();
        chosenTechs= managerInfo.getParcelableArrayList("chosenTech");
        chosentasks=managerInfo.getParcelableArrayList("chosenTask");


        sortTaskBySkill();
        sortTechnicianBySkill();

        basicSchedule();
        initialCost=calculateCost(initialResult);
            //calculate the cost of initial schedule (before hill climbing)
        //improveCost=calculateCost(improveResult);


        hillClimbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hillClimbing(initialResult);
                showScheduleInfo();
            }
        });

        guidedLocalSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CostTimeTask costTimeTask=new CostTimeTask(ScheduleResult.this);
                costTimeTask.execute();
               // guidedLS();
                //showScheduleInfo();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ScheduleResult.this,ManagerDashboard.class);
                intent.putExtras(managerInfo);
                startActivity(intent);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestQueue requestQueue= Volley.newRequestQueue(ScheduleResult.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+ "schedule.php",listener,errorListener){
                    protected Map<String,String> getParams() throws AuthFailureError {
                        Map<String,String> map=new HashMap<String, String>();

                        ArrayList<Task> tasks=new ArrayList<>();
                        tasks.addAll(GLSresult.keySet());
                        ArrayList<TechnicianInfo> techs=new ArrayList<>();
                        techs.addAll(GLSresult.values());

                        map.put("scheduleSize",GLSresult.size()+"");
                        for(int i=0;i<GLSresult.size();i++){
                            map.put("taskId"+i,tasks.get(i).getId()+"");
                            map.put("techId"+i,techs.get(i).getId()+"");
                        }
                        return map;
                    }
                };

                requestQueue.add(stringRequest);

            }
        });


        showScheduleInfo();

    }


    private void initialize() {
        chosenTechs=new ArrayList<>();
        chosentasks=new ArrayList<>();
        getPlanInfo=(TextView)findViewById(R.id.getPlanInfo);
        initialResult=new HashMap<>();
        improveResult=new HashMap<>();
        localOptima=new HashMap<>();
        sortedTask=new ArrayList<>();
        sortedTech=new ArrayList<>();
        GLSresult=new HashMap<>();
        hillClimbing=(Button)findViewById(R.id.hillClimbing);
        guidedLocalSearch=(Button)findViewById(R.id.guidedLoacalSearch);
        back=(Button)findViewById(R.id.backDashboard);
        confirm=(Button)findViewById(R.id.confirmSchedule);
        penalty=new ArrayList<Integer>();
        penalty.add(0,0);
        penalty.add(1,0);
        penalty.add(2,0);
    }


    private class CostTimeTask extends AsyncTask<String,Integer,String>{
        private ProgressDialog dialog;


        public CostTimeTask(Context context){
            dialog=new ProgressDialog(context,0);

            dialog.setCancelable(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            guidedLS();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            showScheduleInfo();
            dialog.dismiss();
        }
    }


    private void guidedLS(){
        int i=0;

        while (i<100){
            modifiedHillClimbing(initialResult);
            ArrayList<Double> localOptimaCost=modifiedCost(localOptima);

            double util1=localOptimaCost.get(1)/(1+penalty.get(0));
            double util2=localOptimaCost.get(2)/(1+penalty.get(1));
            double util3=localOptimaCost.get(3)/(1+penalty.get(2));
            Log.d("util:",util1+","+util2+","+util3);

            if(util1<=util2){
                if(util2<=util3){
                    penalty.add(2,penalty.get(2)+1);
                }else {
                    penalty.add(1,penalty.get(1)+1);
                }
            }else if(util1<=util3){
                penalty.add(2,penalty.get(2)+1);
            }else {
                penalty.add(0,penalty.get(0)+1);
            }

            if(calculateCost(localOptima)<minimumCost){
                minimumCost=calculateCost(localOptima);
                GLSresult=localOptima;
                Log.d("Alert:","Find minimum:"+minimumCost);
            }

            i++;
        }
    }



    /**
     * 需要异步
     * @param schedule
     */
    private void modifiedHillClimbing(Map<Task,TechnicianInfo> schedule) {
        ArrayList<Double> originCostResult=modifiedCost(schedule);
        Map<Task,TechnicianInfo> newNeighbor=new HashMap<>();

        boolean find=false;

        Log.d("schedule length: ",schedule.size()+"");
        newNeighbor.clear();
        newNeighbor=swapToFindNeighbor(schedule);

        if(newNeighbor!=null){
            ArrayList<Double> newCostResult=modifiedCost(newNeighbor);
            if(newCostResult.get(0)<=originCostResult.get(0)){
                modifiedHillClimbing(newNeighbor);
                find=true;
            }
        }

        if(!find){
            newNeighbor=assignToFindNeighbor(schedule);
            if(newNeighbor!=null) {
                ArrayList<Double> newCostResult = modifiedCost(newNeighbor);
                if (newCostResult.get(0) <= originCostResult.get(0)) {
                    modifiedHillClimbing(newNeighbor);
                    find=true;
                }
            }
        }

        if(!find){
            if (schedule.size()<sortedTask.size()){
                newNeighbor=addUnassignedToFindNeighbor(schedule);
                if(newNeighbor!=null){
                    ArrayList<Double> newCostResult = modifiedCost(newNeighbor);
                    Log.d("Test",newCostResult.get(0)+","+originCostResult.get(0));
                    if(newCostResult.get(0) <= originCostResult.get(0)){
                        find=true;
                        Log.d("Test","come");
                        modifiedHillClimbing(newNeighbor);
                    }
                }
            }
        }


        if(!find){
            Log.d("Alert: ","find local optima");
            localOptima=schedule;
        }


    }


    /**
     * 找两个random的数，互换位置上的
     */
    private void hillClimbing(Map<Task,TechnicianInfo> schedule) {
        double originCost=calculateCost(schedule);
        Map<Task,TechnicianInfo> newNeighbor=new HashMap<>();

        boolean find=false;


        newNeighbor=swapToFindNeighbor(schedule);
        if(newNeighbor!=null){
            if(calculateCost(newNeighbor)<=originCost){
                find=true;
                hillClimbing(newNeighbor);
            }
        }


        if(!find){
            newNeighbor=assignToFindNeighbor(schedule);
            if(newNeighbor!=null){
                if(calculateCost(newNeighbor)<=originCost){
                    find=true;
                    hillClimbing(newNeighbor);
                }
            }
        }

        if(!find){
            Log.d("Test",sortedTask.size()+","+schedule.size());
            if (schedule.size()<sortedTask.size()){
                Log.d("Test","Enter");
                newNeighbor=addUnassignedToFindNeighbor(schedule);
                if(newNeighbor!=null){
                    if(calculateCost(newNeighbor)<=originCost){
                        find=true;
                        hillClimbing(newNeighbor);
                    }
                }
            }
        }

        if(!find){
            improveResult=schedule;
        }
    }


    private Map<Task,TechnicianInfo> swapToFindNeighbor(Map<Task,TechnicianInfo> originSchedule){
        Map<Task,TechnicianInfo> neighbor=new HashMap<>();

        ArrayList<Task> tasks=new ArrayList<>();
        tasks.addAll(originSchedule.keySet());
        ArrayList<TechnicianInfo> techs=new ArrayList<>();
        techs.addAll(originSchedule.values());

        Boolean find=false;


        for(int k=0;k<100;k++){

            int i= (int) Math.round(Math.random()*(originSchedule.size()-1));
            int j= (int) Math.round(Math.random()*(originSchedule.size()-1));

            if(i!=j&&techs.get(i)!=techs.get(j)&&tasks.get(i).getSkillRequirement()<=techs.get(j).getSkillLevel()&&tasks.get(j).getSkillRequirement()<=techs.get(i).getSkillLevel()){
                Map<Task,TechnicianInfo> newResult=new HashMap<>();
                newResult.putAll(originSchedule);
                newResult.put(tasks.get(i),techs.get(j));
                newResult.put(tasks.get(j),techs.get(i));
                if(calculateWorkHour(newResult,techs.get(j))<=techs.get(j).getWorkHour()&&calculateWorkHour(newResult,techs.get(i))<=techs.get(i).getWorkHour()){
                    neighbor.putAll(newResult);
                    find=true;
                    break;
                }
            }
        }

        if(find){
            return neighbor;
        }else {
            return  null;
        }
    }

    private Map<Task,TechnicianInfo> assignToFindNeighbor(Map<Task,TechnicianInfo> originSchedule){
        Map<Task,TechnicianInfo> neighbor=new HashMap<>();
        ArrayList<Task> tasks=new ArrayList<>();
        tasks.addAll(originSchedule.keySet());
        ArrayList<TechnicianInfo> techs=new ArrayList<>();
        techs.addAll(sortedTech);

        Boolean find=false;

        for(int k=0;k<100;k++){
            int i= (int) Math.round(Math.random()*(originSchedule.size()-1));
            int j= (int) Math.round(Math.random()*(techs.size()-1));

            if(i!=j&&techs.get(i)!=techs.get(j)&&tasks.get(i).getSkillRequirement()<=techs.get(j).getSkillLevel()){
                Map<Task,TechnicianInfo> newResult=new HashMap<>();
                newResult.putAll(originSchedule);
                newResult.put(tasks.get(i),techs.get(j));
                if(calculateWorkHour(newResult,techs.get(j))<=techs.get(j).getWorkHour()){
                    neighbor.putAll(newResult);
                    find=true;
                    break;
                }
            }
        }

        if(find){
            return neighbor;
        }else {
            return  null;
        }
    }


    private Map<Task,TechnicianInfo> addUnassignedToFindNeighbor(Map<Task,TechnicianInfo> originSchedule){
        Map<Task,TechnicianInfo> neighbor=new HashMap<>();
        ArrayList<Task> tasks=new ArrayList<>();
        tasks.addAll(originSchedule.keySet());
        ArrayList<TechnicianInfo> techs=new ArrayList<>();
        techs.addAll(sortedTech);

        ArrayList<Task> unassignedTask=new ArrayList<>();
        unassignedTask.addAll(sortedTask);

        for(int i=0;i<tasks.size();i++){
            unassignedTask.remove(tasks.get(i));
        }
        Boolean find=false;

        for(int k=0;k<10;k++){
            int i= (int) Math.round(Math.random()*(unassignedTask.size()-1));
            int j= (int) Math.round(Math.random()*(techs.size()-1));

            if(unassignedTask.get(i).getSkillRequirement()<=techs.get(j).getSkillLevel()){
                Map<Task,TechnicianInfo> newResult=new HashMap<>();
                newResult.putAll(originSchedule);
                newResult.put(unassignedTask.get(i),techs.get(j));
                if(calculateWorkHour(newResult,techs.get(j))<=techs.get(j).getWorkHour()){
                    neighbor.putAll(newResult);
                    Log.d("Test","Found");
                    find=true;
                    break;
                }
            }
        }


        if(find){
            return neighbor;
        }else {
            return null;
        }
    }





    /**
     * Calculate the cost after the schedule.(hill climbing)
     */
    private Double calculateCost(Map<Task,TechnicianInfo> schedule) {
        ArrayList<Task> availableTask= new ArrayList<>();
        ArrayList<Task> unassignedTask=new ArrayList<>();
        unassignedTask.addAll(sortedTask);
        availableTask.addAll(schedule.keySet());
        //double maxCost=0;
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


            }

//            if(cost>maxCost){
//                maxCost=cost;
//            }
        }

        if(!unassignedTask.isEmpty()){
            for(int i=0;i<unassignedTask.size();i++){
                cost+=UNASSIGNEDTASKPENALTY*unassignedTask.get(i).getDuration();
            }

            //Log.d("unassigned Task:",unassignedTask.size()+"");
        }
        return cost;
    }


    private ArrayList<Double> modifiedCost(Map<Task,TechnicianInfo> schedule){
        ArrayList<Task> availableTask= new ArrayList<>();
        ArrayList<Task> unassignedTask=new ArrayList<>();
        unassignedTask.addAll(sortedTask);
        availableTask.addAll(schedule.keySet());
        //double maxCost=0;
        ArrayList<Double> result=new ArrayList<>();
        double cost=0;
        double taskCost=0;
        double travelCost=0;
        double unassignedTaskCost=0;


        for(int j=0;j<sortedTech.size();j++) {
            ArrayList<Task> assignedTask = new ArrayList<>();
            if (schedule.containsValue(sortedTech.get(j))) {  //There already exist some task assigned to the technician j.
                for (int k = 0; k < schedule.size(); k++) {
                    if (schedule.get(availableTask.get(k)) == sortedTech.get(j)) {
                        cost += calculateDur(availableTask.get(k).getSkillRequirement(), sortedTech.get(j).getSkillLevel(),availableTask.get(k).getDuration());    //calculate estimate time which is the sum of all the assigned task for this specific technician.
                        taskCost += calculateDur(availableTask.get(k).getSkillRequirement(), sortedTech.get(j).getSkillLevel(),availableTask.get(k).getDuration());;
                        assignedTask.add(availableTask.get(k));
                        unassignedTask.remove(availableTask.get(k));
                    }
                }


                if(assignedTask.size()>=1){
                    float dist = calculateTravelTime(assignedTask);
                    cost += (dist / 1000) * 10;  //assume drive speed is 6km/h.
                    travelCost+=(dist / 1000) * 10;
                }
            }

        }

        if(!unassignedTask.isEmpty()){
            for(int i=0;i<unassignedTask.size();i++){
                cost+=UNASSIGNEDTASKPENALTY*unassignedTask.get(i).getDuration();
                unassignedTaskCost+=UNASSIGNEDTASKPENALTY*unassignedTask.get(i).getDuration();
            }
        }


        cost+=taskCost*penalty.get(0)+travelCost*penalty.get(1)+unassignedTaskCost*penalty.get(2);

        result.add(0,cost);
        result.add(1,taskCost);
        result.add(2,travelCost);
        result.add(3,unassignedTaskCost);

        return result;

    }






    /**
     * Test
     */
    private void showScheduleInfo() {
        showData="";
        showData+="number of tasks:"+sortedTask.size()+", number of technicians:"+sortedTech.size()+"\n\n";

        ArrayList<Task> availableTask= new ArrayList<>();
        availableTask.addAll(initialResult.keySet());

        for(int i=0;i<availableTask.size();i++){
            showData+="task"+availableTask.get(i).getId()+",skill:"+availableTask.get(i).getSkillRequirement()+",duration:"+availableTask.get(i).getDuration()+": "+initialResult.get(availableTask.get(i)).getFirstName()+", tech skill:"+initialResult.get(availableTask.get(i)).getSkillLevel()+", work hour:"+initialResult.get(availableTask.get(i)).getWorkHour()+"\n";
        }

        showData+="cost:"+initialCost.shortValue()+"\n";

        if(!improveResult.isEmpty()){
            ArrayList<Task> improved=new ArrayList<>();
            improved.addAll(improveResult.keySet());
            for(int j=0;j<improved.size();j++){
                showData+="task"+improved.get(j).getId()+",skill:"+improved.get(j).getSkillRequirement()+",duration:"+improved.get(j).getDuration()+": "+improveResult.get(improved.get(j)).getFirstName()+", tech skill:"+improveResult.get(improved.get(j)).getSkillLevel()+", work hour:"+improveResult.get(improved.get(j)).getWorkHour()+"\n";
            }
            improveCost=calculateCost(improveResult);
            showData+="cost:"+improveCost.shortValue()+"\n";
        }

        if(!GLSresult.isEmpty()){
            ArrayList<Task> improved=new ArrayList<>();
            improved.addAll(GLSresult.keySet());
            for(int j=0;j<improved.size();j++){
                showData+="task"+improved.get(j).getId()+",skill:"+improved.get(j).getSkillRequirement()+",duration:"+improved.get(j).getDuration()+": "+GLSresult.get(improved.get(j)).getFirstName()+", tech skill:"+GLSresult.get(improved.get(j)).getSkillLevel()+", work hour:"+GLSresult.get(improved.get(j)).getWorkHour()+"\n";
            }
            Double finalCost=calculateCost(GLSresult);
            showData+="cost: "+minimumCost.shortValue()+"\n";
        }




        getPlanInfo.setText(showData);

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


    private float calculateWorkHour(Map<Task,TechnicianInfo> schedule,TechnicianInfo tech){
        ArrayList<Task> tasks=new ArrayList<>();
        ArrayList<Task> scheduledTask=new ArrayList<>();
        scheduledTask.addAll(schedule.keySet());
        for(int i=0;i<schedule.size();i++){
            if(schedule.get(scheduledTask.get(i))==tech){
                tasks.add(scheduledTask.get(i));
            }
        }
        float cost=0;


            for(int i=0;i<tasks.size();i++){
                cost+=calculateDur(tasks.get(i).getSkillRequirement(),tech.getSkillLevel(),tasks.get(i).getDuration());
            }
            float dist=calculateTravelTime(tasks);
            cost+=(dist/1000)*2;



        return cost;

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
        if(travelTasks.size()!=0){
            Location.distanceBetween(startEnd.latitude,startEnd.longitude,travelTasks.get(0).getPosition().latitude,travelTasks.get(0).getPosition().longitude,baseToStart);
            Location.distanceBetween(startEnd.latitude,startEnd.longitude,travelTasks.get(travelTasks.size()-1).getPosition().latitude,travelTasks.get(travelTasks.size()-1).getPosition().longitude,endToBase);
            total=total+baseToStart[0]+endToBase[0];
        }


        //Log.d("travel time:",(total/1000)*10+"");

        return total;
    }


    private double calculateDur(int taskSkillReq,int techSkillLevel,int taskDuration ){
        double result=(double) taskSkillReq/(double) techSkillLevel;

        return result*taskDuration;
    }

    private void sortTaskBySkill() {
        int minSkillRequire=100;
        int minCount=0;
        if(chosentasks.size()>0){
            for(int i=0;i<chosentasks.size();i++){
                int skillRequire=chosentasks.get(i).getSkillRequirement();
                if(skillRequire<=minSkillRequire){
                    minSkillRequire=skillRequire;
                    minCount=i;
                }
            }
            Task t=chosentasks.get(minCount);
            sortedTask.add(t);
            chosentasks.remove(minCount);
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

    Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                jsonObject=new JSONObject(s);
                retCode=jsonObject.getInt("success");
            }catch (JSONException e){
                e.printStackTrace();
            }
            if(retCode==1){
                Toast.makeText(ScheduleResult.this,"Distribute the tasks successfully!",Toast.LENGTH_SHORT).show();

            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ScheduleResult.this,"Database not connected",Toast.LENGTH_SHORT).show();

        }
    };


}
