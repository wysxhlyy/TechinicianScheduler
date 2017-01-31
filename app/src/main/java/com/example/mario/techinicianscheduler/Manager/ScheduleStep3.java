package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.example.mario.techinicianscheduler.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScheduleStep3 extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;

    private Bundle planInfo;
    private int chosenCount=1;
    private Button editTech;
    private Button generate;

    private int retCode;
    private static final String TAG = ManagerLogin.class.getSimpleName();
    private JSONObject jsonObject;
    private int techNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_step3);
        initialize();


        RequestQueue requestQueue= Volley.newRequestQueue(ScheduleStep3.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,"http://10.132.201.46/technicianScheduler/getAvailableTech.php",listener,errorListener){
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                map.put("managerId",getSharedPreferences("managerSession",MODE_PRIVATE).getString("managerId",null));
                return map;
            }
        };

        requestQueue.add(stringRequest);

        planInfo=getIntent().getExtras();

        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        cb4.setOnCheckedChangeListener(this);
        cb5.setOnCheckedChangeListener(this);
        editTech.setOnClickListener(this);
        generate.setOnClickListener(this);

    }

    private void initialize() {
        cb1=(CheckBox)findViewById(R.id.cb1);
        cb2=(CheckBox)findViewById(R.id.cb2);
        cb3=(CheckBox)findViewById(R.id.cb3);
        cb4=(CheckBox)findViewById(R.id.cb4);
        cb5=(CheckBox)findViewById(R.id.cb5);

        editTech=(Button)findViewById(R.id.editTech);
        generate=(Button)findViewById(R.id.generate);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked){
            planInfo.putString("chosenTech"+chosenCount,compoundButton.getText().toString());
            chosenCount++;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editTech:
                Intent intent=new Intent(ScheduleStep3.this,ManageTechnicians.class);
                startActivity(intent);
                break;
            case R.id.generate:
                Intent intent2=new Intent(ScheduleStep3.this,ScheduleResult.class);
                planInfo.putInt("chosenTechNum",chosenCount-1);
                intent2.putExtras(planInfo);
                startActivity(intent2);
                break;
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
                try {
                    techNum=Integer.parseInt(jsonObject.getString("techNum"))-1;
                    if(techNum==0){
                        Toast.makeText(ScheduleStep3.this,"Cannot find available technicians",Toast.LENGTH_SHORT).show();
                    }

                    if(jsonObject.getString("techId1")!=null){
                        cb1.setVisibility(View.VISIBLE);
                        cb1.setText(jsonObject.getString("techName1"));
                    }

                    if(jsonObject.getString("techId2")!=null){
                        cb2.setVisibility(View.VISIBLE);
                        cb2.setText(jsonObject.getString("techName2"));
                    }

                    if(jsonObject.getString("techId3")!=null){
                        cb3.setVisibility(View.VISIBLE);
                        cb3.setText(jsonObject.getString("techName3"));
                    }

                    if(jsonObject.getString("techId4")!=null){
                        cb4.setVisibility(View.VISIBLE);
                        cb4.setText(jsonObject.getString("techName4"));
                    }

                    if(jsonObject.getString("techId5")!=null){
                        cb5.setVisibility(View.VISIBLE);
                        cb5.setText(jsonObject.getString("techName5"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                Toast.makeText(ScheduleStep3.this,"Cannot find available technicians",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ScheduleStep3.this,"Database not connected",Toast.LENGTH_SHORT).show();
            Log.e(TAG,volleyError.getMessage(),volleyError);
        }
    };

}
