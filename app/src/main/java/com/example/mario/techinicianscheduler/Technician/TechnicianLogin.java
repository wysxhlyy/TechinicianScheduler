package com.example.mario.techinicianscheduler.Technician;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
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
import com.example.mario.techinicianscheduler.Manager.ManagerLogin;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.SignUp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Handle the login of the technicains. Once the technician input correct username and password, his information
 * will all be returned by database and they are stored in a bundle object. The bundle object can be transmitted
 * to each activity, which means the technician information can be accessed in each activity.
 */
public class TechnicianLogin extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ManagerLogin.class.getSimpleName();

    private Button login;
    private TextView technician;
    private Bundle techInfo;
    private ImageButton goManager;

    private EditText techUsername;
    private EditText techPassword;
    private SharedPreferences sharedPreferences;
    private int retCode;

    private JSONObject jsonObject;

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_login);

        initialize();
        login.setOnClickListener(this);

        spinnerHandle();

        sharedPreferences = getSharedPreferences("techLogin", MODE_PRIVATE);
        String getName = sharedPreferences.getString("username", null);
        if (getName == null) {
            Toast.makeText(TechnicianLogin.this, "No Stored Information,Input Again!", Toast.LENGTH_SHORT).show();
        } else {
            techUsername.setText(sharedPreferences.getString("username", null));
            techPassword.setText(sharedPreferences.getString("password", null));
        }

        goManager.setOnClickListener(this);
    }

    /**
     * Initial the components.
     */
    private void initialize() {
        login = (Button) findViewById(R.id.techLogIn);
        goManager = (ImageButton) findViewById(R.id.goManager);
        technician = (TextView) findViewById(R.id.goSignUp);
        techUsername = (EditText) findViewById(R.id.techUsername);
        techPassword = (EditText) findViewById(R.id.techPassword);
        techInfo = new Bundle();

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.techLogIn:
                username = techUsername.getText().toString();
                password = techPassword.getText().toString();

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(TechnicianLogin.this, "Missing username or password", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Start a Internet request
                RequestQueue requestQueue = Volley.newRequestQueue(TechnicianLogin.this);

                //Create a string request, set the POST method
                //DB_ADDRESS defines the address of the host,and the php file is used to connect the database.
                //listener and errorlistener is used to receive the returned result.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "technicianLogIn.php", listener, errorListener) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("username", username);
                        map.put("password", password);       //The data are packaged in a map to be transfer to the php file.
                        return map;
                    }
                };
                requestQueue.add(stringRequest);


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.commit();


                break;
            case R.id.goManager:
                Intent intent = new Intent(TechnicianLogin.this, ManagerLogin.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                jsonObject = new JSONObject(s);
                retCode = jsonObject.getInt("success");
                //The variable success equals 1 if the user logs in successfully,otherwise equals 0.
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
                If the technician log in successfully,all the personal information will be stored in a bundle,
                then as long as the user didn't log out, his information can be accessed in each activity of
                the application.
             */
            if (retCode == 1) {
                Intent intent = new Intent(TechnicianLogin.this, TechnicianDashboard.class);
                try {
                    techInfo.putInt("techId", jsonObject.getInt("technicianId"));
                    techInfo.putString("username", jsonObject.getString("username"));
                    techInfo.putString("password", jsonObject.getString("password"));
                    techInfo.putString("email", jsonObject.getString("email"));
                    techInfo.putString("phone", jsonObject.getString("phone"));
                    techInfo.putString("firstName", jsonObject.getString("firstName"));
                    techInfo.putString("surname", jsonObject.getString("surname"));
                    techInfo.putString("skillLevel", jsonObject.getString("skillLevel"));
                    techInfo.putString("workHour", jsonObject.getString("workHour"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtras(techInfo);
                startActivity(intent);
            } else {
                //If the log in is unsuccessful,a prompt will be sent to inform the technician.
                Toast.makeText(TechnicianLogin.this, "Wrong username or password!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //If the database is failed to connect, a prompt will be sent to inform the technician.
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianLogin.this, "Database not connected", Toast.LENGTH_SHORT).show();
            Log.e(TAG, volleyError.getMessage(), volleyError);
        }
    };

    /**
     * Handle the transfer between technician page and manager page.
     */
    private void spinnerHandle() {
        String prompt = technician.getText().toString();

        SpannableString spannedString = new SpannableString(prompt);
        spannedString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TechnicianLogin.this, SignUp.class);
                startActivity(intent);
            }
        }, 0, prompt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        technician.setText(spannedString);
        technician.setMovementMethod(LinkMovementMethod.getInstance());
    }


}

