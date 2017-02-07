package com.example.mario.techinicianscheduler.Manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTasks extends AppCompatActivity {

    private Button addNewTask;
    private ListView taskListView;
    private SimpleAdapter dataAdapter;
    private ArrayList<Task> existTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tasks);
        initialize();

        existTasks=getIntent().getExtras().getParcelableArrayList("availableTask");
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();

        map.put("id","Task ID");
        map.put("name","    Task Name");
        map.put("duration","Estimate Duration");
        list.add(map);

        for(int i=0;i<existTasks.size();i++){
            map=new HashMap<>();
            map.put("id",existTasks.get(i).getId());
            map.put("name",existTasks.get(i).getName());
            map.put("duration",existTasks.get(i).getDuration());
            list.add(map);
        }
        dataAdapter=new SimpleAdapter(this,list,R.layout.db_item_layout,new String[]{"id","name","duration"},new int[]{R.id.value1,R.id.value2,R.id.value3});
        taskListView.setAdapter(dataAdapter);

    }

    private void initialize() {
        addNewTask=(Button)findViewById(R.id.addNewTask);
        taskListView=(ListView)findViewById(R.id.taskListView);
    }
}
