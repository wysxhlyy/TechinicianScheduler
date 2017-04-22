package com.example.mario.techinicianscheduler.Manager.HandleTechnician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.mario.techinicianscheduler.Manager.ManagerDashboard;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.TechnicianInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Show the information of technician, the manager is allowed to edit these information.
 */

public class DisplayTechnician extends AppCompatActivity {

    private EditText skillLevel;
    private EditText workHour;
    private TextView techName;
    private ImageButton update;
    private Button remove;
    private ImageButton quit;
    private int selectedId;
    private TechnicianInfo selectedTech;
    private ArrayList<TechnicianInfo> bindTech;

    private int retCode;
    private JSONObject jsonObject;
    private RequestQueue requestQueue;

    private Bundle managerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_technician);
        initialize();

        managerInfo = getIntent().getExtras();
        selectedId = managerInfo.getInt("selectedTech");
        bindTech = managerInfo.getParcelableArrayList("availableTechnician");
        selectedTech = bindTech.get(selectedId);
        skillLevel.setText(selectedTech.getSkillLevel() + "");
        workHour.setText(selectedTech.getWorkHour() + "");
        techName.setText(selectedTech.getFirstName() + " " + selectedTech.getSurname());


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTechInfo();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unBindTech();
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void unBindTech() {
        requestQueue = Volley.newRequestQueue(DisplayTechnician.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "unbindTech.php", listener, errorListener) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("techId", selectedTech.getId() + "");
                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateTechInfo() {
        requestQueue = Volley.newRequestQueue(DisplayTechnician.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "editTech.php", listener, errorListener) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("techSkill", skillLevel.getText().toString());
                map.put("techWorkHour", workHour.getText().toString());
                map.put("techId", selectedTech.getId() + "");
                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void initialize() {
        skillLevel = (EditText) findViewById(R.id.editTechSkill);
        workHour = (EditText) findViewById(R.id.editTechWorkHour);
        update = (ImageButton) findViewById(R.id.editTechSubmit);
        remove = (Button) findViewById(R.id.removeTech);
        techName = (TextView) findViewById(R.id.editedTechName);
        quit = (ImageButton) findViewById(R.id.quitEditTech);
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                jsonObject = new JSONObject(s);
                retCode = jsonObject.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (retCode == 1) {
                Toast.makeText(DisplayTechnician.this, "Success", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(DisplayTechnician.this, ManagerDashboard.class);
                intent.putExtras(managerInfo);
                startActivity(intent);

            } else {
                Toast.makeText(DisplayTechnician.this, "Failed to Update", Toast.LENGTH_SHORT).show();
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(DisplayTechnician.this, "Database not connected", Toast.LENGTH_SHORT).show();

        }
    };
}
