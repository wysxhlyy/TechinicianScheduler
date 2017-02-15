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

import com.example.mario.techinicianscheduler.MyListAdapter;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.TechnicianInfo;

import java.util.ArrayList;

public class chooseTechnician extends AppCompatActivity implements View.OnClickListener, MyListAdapter.CheckedAllListener {


    private Bundle managerInfo;
    private Button generate;

    private CheckBox cbButtonAll;
    private SparseBooleanArray isChecked;
    boolean flag;
    private MyListAdapter adapter;
    private ListView availableTechs;
    private SparseBooleanArray checkedTech;

    private ArrayList<TechnicianInfo> techs;
    private ArrayList<TechnicianInfo> chosenTechs;

    private ArrayList<TechnicianInfo> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_choosetech);
        initialize();

        managerInfo=getIntent().getExtras();

        techs=managerInfo.getParcelableArrayList("availableTechnician");
        Log.d("numOfTechs:",techs.size()+"");

        adapter=new MyListAdapter(techs,this);
        adapter.setCheckedAllListener(this);
        availableTechs.setAdapter(adapter);
        availableTechs.setDivider(null);


        generate.setOnClickListener(this);

    }

    private void initialize() {
        techs=new ArrayList<>();
        chosenTechs=new ArrayList<>();

        cbButtonAll=(CheckBox)findViewById(R.id.cb_all_button);
        isChecked=new SparseBooleanArray();
        availableTechs=(ListView)findViewById(R.id.availableTechs);
        checkedTech=new SparseBooleanArray();


        generate=(Button)findViewById(R.id.generate);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.generate:
                Intent intent2=new Intent(chooseTechnician.this,ScheduleResult.class);
                if(cbButtonAll.isChecked()){
                    for(int i=0;i<techs.size();i++){
                        chosenTechs.add(techs.get(i));
                    }
                }else {
                    for(int i=0;i<techs.size();i++){
                        if(checkedTech.valueAt(i)){
                            chosenTechs.add(techs.get(i));
                        }
                    }
                }

                managerInfo.putParcelableArrayList("chosenTech",chosenTechs);
                intent2.putExtras(managerInfo);
                startActivity(intent2);
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
        checkedTech=checkall;
    }

    /**
     * Handle the Select All checkbox
     * @param v
     */
    public void allSelect(View v){
        if(cbButtonAll.isChecked()){
            flag=true;
        }else {
            flag=false;
        }

        if(flag){
            for(int i=0;i<techs.size();i++){
                isChecked.put(i,true);
                MyListAdapter.setIsSelected(isChecked);
            }
        }else{
            for(int i=0;i<techs.size();i++){
                isChecked.put(i,false);
                MyListAdapter.setIsSelected(isChecked);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
