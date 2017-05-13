package com.example.mario.techinicianscheduler.Manager.HandleTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.mario.techinicianscheduler.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Allow the manager to edit the task, the edited information could be put into the database.
 */

public class EditTask extends AppCompatActivity {

    private EditText editName;
    private EditText editSkillReq;
    private EditText editStationName;
    private EditText editDuration;
    private EditText editDescription;
    private EditText editStatus;
    private ImageButton update;
    private Button delete;
    private ImageButton quitEditTask;

    private Bundle managerInfo;
    private int chosenId;
    private ArrayList<Task> existTasks;
    private Task selectedTask;

    private JSONObject jsonObject;
    private int retCode;
    private int deleteCode;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit_task);
        initialize();

        chosenId = managerInfo.getInt("selectedTask");
        existTasks = managerInfo.getParcelableArrayList("availableTask");
        selectedTask = existTasks.get(chosenId - 1);

        //First show the information of task.
        editName.setText(selectedTask.getName());
        Log.d("TEST", selectedTask.getName() + "");
        editSkillReq.setText(selectedTask.getSkillRequirement() + "");
        editStationName.setText(selectedTask.getStationName());
        editDuration.setText(selectedTask.getDuration() + "");
        editDescription.setText(selectedTask.getDescription());
        editStatus.setText(selectedTask.getFinished());


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editName.getText().toString().equals("")||!editSkillReq.getText().toString().matches("\\d+")||editStationName.getText().toString().equals("")||!editDuration.getText().toString().matches("\\d+")||editDescription.getText().toString().equals("")||!editStatus.getText().toString().matches("^true|false$")){
                    Toast.makeText(EditTask.this, "Please input valid information", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    updateTask();
                    Intent intent = new Intent(EditTask.this, ManagerDashboard.class);
                    intent.putExtras(managerInfo);
                    startActivity(intent);
                    finish();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTask();
            }
        });

        quitEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * If the user click the delete,use taskId to delete this task.
     */
    private void deleteTask() {
        requestQueue = Volley.newRequestQueue(EditTask.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "deleteTask.php", listener, errorListener) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("taskId", selectedTask.getId() + "");
                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * If the user click the update, then insert these information to the database.
     */
    private void updateTask() {
        requestQueue = Volley.newRequestQueue(EditTask.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "editTask.php", listener, errorListener) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("taskName", editName.getText().toString());
                map.put("taskSkillReq", editSkillReq.getText().toString());
                map.put("taskStation", editStationName.getText().toString());
                map.put("taskDuration", editDuration.getText().toString());
                map.put("taskDescription", editDescription.getText().toString());
                map.put("taskId", selectedTask.getId() + "");
                map.put("taskStatus",editStatus.getText().toString());
                return map;
            }
        };

        requestQueue.add(stringRequest);

    }

    /**
     * Initial the components.
     */
    private void initialize() {
        editName = (EditText) findViewById(R.id.editTaskName);
        editSkillReq = (EditText) findViewById(R.id.editTaskSkillReq);
        editStationName = (EditText) findViewById(R.id.editTaskStation);
        editDuration = (EditText) findViewById(R.id.editTaskDuration);
        editDescription = (EditText) findViewById(R.id.editTaskDescrip);
        managerInfo = getIntent().getExtras();
        update = (ImageButton) findViewById(R.id.editTaskSubmit);
        delete = (Button) findViewById(R.id.deleteTask);
        quitEditTask = (ImageButton) findViewById(R.id.quitEditTask);
        editStatus= (EditText)findViewById(R.id.editTaskStatus);
    }

    /**
     * Used for database communication.
     */
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
                Toast.makeText(EditTask.this, "Success", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EditTask.this, ManagerDashboard.class);
                intent.putExtras(managerInfo);
                startActivity(intent);

            } else {
                Toast.makeText(EditTask.this, "Failed to Update", Toast.LENGTH_SHORT).show();
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(EditTask.this, "Database not connected", Toast.LENGTH_SHORT).show();

        }
    };
}
