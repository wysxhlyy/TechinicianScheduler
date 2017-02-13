package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;
import com.example.mario.techinicianscheduler.TaskListAdapter;

import java.util.ArrayList;

public class chooseTask extends AppCompatActivity implements View.OnClickListener, TaskListAdapter.CheckedAllListener {

    private Button addTaskBtn;
    private Button next2;

    private CheckBox cbButtonAll;
    private SparseBooleanArray isChecked;
    boolean flag;
    private TaskListAdapter adapter;
    private ListView availableTask;
    private SparseBooleanArray checkedTask;

    private Bundle managerInfo;

    private ArrayList<Task> tasks;
    private ArrayList<Task> chosenTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_choosetask);

        initialize();
        managerInfo=getIntent().getExtras();
        tasks=managerInfo.getParcelableArrayList("availableTask");
        Log.d("number of task",""+tasks.size());

        adapter=new TaskListAdapter(tasks,this);
        adapter.setCheckedAllListener(this);
        availableTask.setAdapter(adapter);
        availableTask.setDivider(null);



        //根据numOfTasks展示task输入框

        addTaskBtn.setOnClickListener(this);
        next2.setOnClickListener(this);
    }


    private void initialize() {
        tasks=new ArrayList<>();
        chosenTasks=new ArrayList<>();
        addTaskBtn=(Button)findViewById(R.id.addTaskBtn);
        next2=(Button)findViewById(R.id.next2);

        cbButtonAll=(CheckBox)findViewById(R.id.cb_all_button2);
        isChecked=new SparseBooleanArray();
        availableTask=(ListView)findViewById(R.id.availableTasks);
        checkedTask=new SparseBooleanArray();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addTaskBtn:
                break;
            case R.id.next2:
                Intent intent=new Intent(chooseTask.this,chooseTechnician.class);
                for(int i=0;i<tasks.size();i++){
                    if(checkedTask.valueAt(i)){
                        chosenTasks.add(tasks.get(i));
                    }
                }
                managerInfo.putParcelableArrayList("chosenTask",chosenTasks);
                intent.putExtras(managerInfo);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void CheckAll(SparseBooleanArray checkall) {
        if (checkall.indexOfValue(false) < 0) {
            if (!cbButtonAll.isChecked()) {
                this.flag = false;
                cbButtonAll.setChecked(true);
            }                                   //if all the checkbox is selected, the select all button should be set to true.
        } else if (checkall.indexOfValue(false) >= 0 && checkall.indexOfValue(true) >= 0) {
            if (cbButtonAll.isChecked()) {
                this.flag = true;
                cbButtonAll.setChecked(false);
            }                                   //if some of the checkbox is true, some is false, the selct all button will be set to false.
        }
        checkedTask=checkall;
    }

    public void allSelectTask(View v){
        if(cbButtonAll.isChecked()){
            flag=true;
        }else {
            flag=false;
        }

        if(flag){
            for(int i=0;i<tasks.size();i++){
                isChecked.put(i,true);
                TaskListAdapter.setIsSelected(isChecked);
            }
        }else{
            for(int i=0;i<tasks.size();i++){
                isChecked.put(i,false);
                TaskListAdapter.setIsSelected(isChecked);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
