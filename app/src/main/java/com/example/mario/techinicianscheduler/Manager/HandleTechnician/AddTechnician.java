package com.example.mario.techinicianscheduler.Manager.HandleTechnician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
 * Used to bind the technician, after user input technician's username, the system will find the technician according
 * to its username, if the bind fails, a prompt will be shown according to the reason of failure, otherwise this technician
 * can be bind to this manager,which means he will present in the corresponding manager's technicianl ist.
 */
public class AddTechnician extends AppCompatActivity {

    private AutoCompleteTextView addTechnicianUsername;
    private Button addTech;
    private int retCode;
    private int addCode;
    private JSONObject jsonObject;
    private Bundle managerInfo;
    private RequestQueue requestQueue;
    private ImageButton quit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_technician);
        initialize();

        setAutoComplete();

        //Here, the "add" actually means bind the technician to manager using his username.
        addTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQueue = Volley.newRequestQueue(AddTechnician.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "addTechnician.php", listener, errorListener) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("username", addTechnicianUsername.getText().toString());
                        map.put("managerId", managerInfo.getString("managerId"));
                        return map;
                    }
                };

                requestQueue.add(stringRequest);

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
        managerInfo = getIntent().getExtras();
        addTechnicianUsername = (AutoCompleteTextView) findViewById(R.id.addTechUsername);
        addTech = (Button) findViewById(R.id.addTechBtn);
        quit = (ImageButton) findViewById(R.id.quitAddTechnician);
    }

    /**
     * To autocomplete the user input. The"abcde" should be the name of stations.
     */
    private void setAutoComplete() {
        String[] availableTechs = new String[]{"abcde", "abcdef", "bcdefg", "efgh", "abcfjiosf"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, availableTechs);
        addTechnicianUsername.setAdapter(adapter);
    }

    /**
     * If the technician is bound to the manager successfully, then show a prompt.
     */
    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                jsonObject = new JSONObject(s);
                retCode = jsonObject.getInt("success");
                addCode = jsonObject.getInt("addSuccess");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (retCode == 1) {
                if (addCode == 1) {
                    Toast.makeText(AddTechnician.this, "Bind Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AddTechnician.this, "Technician has already been assigned to other Manager", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(AddTechnician.this, ManagerDashboard.class);
                intent.putExtras(managerInfo);
                startActivity(intent);

            } else {
                Toast.makeText(AddTechnician.this, "Cannot find the technician", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(AddTechnician.this, "Database not connected", Toast.LENGTH_SHORT).show();

        }
    };
}
