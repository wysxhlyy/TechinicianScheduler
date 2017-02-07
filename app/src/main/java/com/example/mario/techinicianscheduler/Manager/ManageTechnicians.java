package com.example.mario.techinicianscheduler.Manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.TechnicianInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTechnicians extends AppCompatActivity {

    private ListView techListView;
    private Button addNewTech;
    private SimpleAdapter dataAdapter;
    private ArrayList<TechnicianInfo> existTechs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_technicians);
        initialize();

        existTechs=getIntent().getExtras().getParcelableArrayList("availableTechnician");
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();

        map.put("id","Technician ID");
        map.put("name","Name");
        map.put("skillLevel","Skill Level");
        list.add(map);

        for(int i=0;i<existTechs.size();i++){
            map=new HashMap<>();
            map.put("id",existTechs.get(i).getId());
            map.put("name",upperFirstChar(existTechs.get(i).getFirstName())+" "+upperFirstChar(existTechs.get(i).getSurname()));
            map.put("skillLevel",existTechs.get(i).getSkillLevel()+"");
            list.add(map);
        }
        dataAdapter=new SimpleAdapter(this,list,R.layout.db_item_layout,new String[]{"id","name","skillLevel"},new int[]{R.id.value1,R.id.value2,R.id.value3});
        techListView.setAdapter(dataAdapter);
    }

    private void initialize() {
        techListView=(ListView)findViewById(R.id.techListView);
        addNewTech=(Button)findViewById(R.id.addNewTech);
    }

    private String upperFirstChar(String str){
        return str.replaceFirst(str.substring(0,1),str.substring(0,1).toUpperCase());
    }
}
