package com.example.mario.techinicianscheduler.Manager.HandleTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
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

/**
 * This class is used to handle the create task.
 */

public class CreateTask extends AppCompatActivity {

    private EditText name;
    private EditText skillReq;
    private AutoCompleteTextView stationName;
    private EditText description;
    private EditText duration;
    private ImageButton addTask;
    private Bundle managerInfo;
    private ImageButton quit;

    private JSONObject jsonObject;
    private RequestQueue requestQueue;
    private int retCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_create_task);
        initialize();

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String taskName, taskSkillReq, taskStation, taskDescrip, taskDuration;
                taskName = name.getText().toString();
                taskSkillReq = skillReq.getText().toString();
                taskStation = stationName.getText().toString();
                taskDescrip = description.getText().toString();
                taskDuration = duration.getText().toString();
                //enter all the task information

                if (taskName.equals("") || !taskSkillReq.matches("\\d+") || taskStation.equals("") || taskDescrip.equals("")||!taskDuration.matches("\\d+")) {
                    Toast.makeText(CreateTask.this, "Please input valid information", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Connect with the database.Insert the information of created task.

                requestQueue = Volley.newRequestQueue(CreateTask.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "createTask.php", listener, errorListener) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("taskName", taskName);
                        map.put("taskSkillReq", taskSkillReq);
                        map.put("taskStation", taskStation);
                        map.put("taskDescription", taskDescrip);
                        map.put("taskDuration", taskDuration);
                        map.put("managerId", managerInfo.getString("managerId"));
                        return map;
                    }
                };
                requestQueue.add(stringRequest);

                Intent intent = new Intent(CreateTask.this, ManagerDashboard.class);
                intent.putExtras(managerInfo);
                startActivity(intent);
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * Initial the components.
     */
    private void initialize() {
        name = (EditText) findViewById(R.id.createTaskName);
        skillReq = (EditText) findViewById(R.id.createTaskSkillReq);
        stationName = (AutoCompleteTextView) findViewById(R.id.createTaskStation);
        description = (EditText) findViewById(R.id.createTaskDescrip);
        addTask = (ImageButton) findViewById(R.id.createTaskSubmit);
        duration = (EditText) findViewById(R.id.createTaskDuration);
        managerInfo = getIntent().getExtras();
        quit = (ImageButton) findViewById(R.id.quitCreateTask);
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
                Toast.makeText(CreateTask.this, "New task has been added successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(CreateTask.this, "Database not connected", Toast.LENGTH_SHORT).show();
        }
    };
}
