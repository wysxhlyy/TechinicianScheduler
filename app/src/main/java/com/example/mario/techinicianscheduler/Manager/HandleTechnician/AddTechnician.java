package com.example.mario.techinicianscheduler.Manager.HandleTechnician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.DBHelper;
import com.example.mario.techinicianscheduler.Manager.ManagerDashboard;
import com.example.mario.techinicianscheduler.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddTechnician extends AppCompatActivity {

    private AutoCompleteTextView addTechnicianUsername;
    private Button addTech;

    private int retCode;
    private int addCode;
    private JSONObject jsonObject;
    private Bundle managerInfo;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_technician);
        initialize();

        setAutoComplete();

        addTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQueue= Volley.newRequestQueue(AddTechnician.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+ "addTechnician.php",listener,errorListener){
                    protected Map<String,String> getParams() throws AuthFailureError {
                        Map<String,String> map=new HashMap<String, String>();
                        map.put("username",addTechnicianUsername.getText().toString());
                        map.put("managerId",managerInfo.getString("managerId"));
                        return map;
                    }
                };

                Log.d("test","I am here");

                requestQueue.add(stringRequest);

            }
        });



    }

    private void initialize() {
        managerInfo=getIntent().getExtras();
        addTechnicianUsername=(AutoCompleteTextView)findViewById(R.id.addTechUsername);
        addTech=(Button)findViewById(R.id.addTechBtn);
    }

    private void setAutoComplete() {
        String[] availableTechs=new String[]{"abcde","abcdef","bcdefg","efgh","abcfjiosf"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,availableTechs);
        addTechnicianUsername.setAdapter(adapter);
    }

    Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                jsonObject=new JSONObject(s);
                retCode=jsonObject.getInt("success");
                addCode=jsonObject.getInt("addSuccess");
            }catch (JSONException e){
                e.printStackTrace();
            }
            if(retCode==1){
                if(addCode==1){
                    Toast.makeText(AddTechnician.this,"Add Successfully",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(AddTechnician.this,"Technician has already been assigned to other Manager",Toast.LENGTH_SHORT).show();
                }

                Intent intent=new Intent(AddTechnician.this, ManagerDashboard.class);
                intent.putExtras(managerInfo);
                startActivity(intent);

            }else{
                Toast.makeText(AddTechnician.this,"Cannot find the technician",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(AddTechnician.this,"Database not connected",Toast.LENGTH_SHORT).show();

        }
    };
}
