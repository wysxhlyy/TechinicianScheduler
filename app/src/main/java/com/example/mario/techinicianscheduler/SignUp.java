package com.example.mario.techinicianscheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.Manager.ManagerDashboard;
import com.example.mario.techinicianscheduler.Manager.ManagerLogin;
import com.example.mario.techinicianscheduler.Technician.TechnicianDashboard;
import com.example.mario.techinicianscheduler.Technician.TechnicianLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Allow the user to sign up an account by themselves.
 * After sign up, their information will be inserted into the database.
 */

public class SignUp extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText email;
    private EditText phone;
    private EditText firstName;
    private EditText surname;
    private ImageButton quitSignUp;
    private ImageView showRole;
    private TextView signUpTitle;

    private Spinner  role;
    private String chosenJob;
    private TextView goSignIn;

    private Button signUp;

    private RequestQueue requestQueue;
    private JSONObject jsonObject;
    private int retCode;
    private static final String TAG = ManagerLogin.class.getSimpleName();

    private Bundle techInfo;
    private Bundle managerInfo;


    private static final String[] m_roles={"Manager","Technician"};
    private ArrayAdapter<String> adapter;

    Response.Listener<String> listener=new Response.Listener<String>(){

        @Override
        public void onResponse(String s) {
            try {
                jsonObject=new JSONObject(s);
                retCode=jsonObject.getInt("success");
            }catch (JSONException e){
                e.printStackTrace();
            }

            if(retCode==1){
                Toast.makeText(SignUp.this,"Sign up succeed!",Toast.LENGTH_SHORT).show();
                try {
                    Log.d("type",jsonObject.getInt("type")+"");
                    if(jsonObject.getInt("type")==0){
                        techInfo.putInt("techId",jsonObject.getInt("technicianId"));
                        Intent intent=new Intent(SignUp.this, TechnicianDashboard.class);
                        techInfo.putString("technicianName",firstName.getText().toString());
                        techInfo.putString("username", username.getText().toString());
                        techInfo.putString("password", password.getText().toString());
                        techInfo.putString("email", email.getText().toString());
                        techInfo.putString("phone", phone.getText().toString());
                        techInfo.putString("firstName", firstName.getText().toString());
                        techInfo.putString("surname", surname.getText().toString());
                        techInfo.putString("skillLevel", "1");
                        techInfo.putString("workHour", "100");
                        intent.putExtras(techInfo);
                        startActivity(intent);
                        finish();
                    }else{
                        managerInfo.putString("managerId",jsonObject.getInt("managerId")+"");
                        Intent intent=new Intent(SignUp.this, ManagerDashboard.class);
                        managerInfo.putString("managerName",firstName.getText().toString());
                        managerInfo.putString("managerEmail", email.getText().toString());
                        managerInfo.putString("managerPhone", phone.getText().toString());
                        managerInfo.putString("managerSurname", surname.getText().toString());
                        managerInfo.putString("managerPass", password.getText().toString());
                        intent.putExtras(managerInfo);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(SignUp.this,"Sign up not succeed!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(SignUp.this,"Database not connected",Toast.LENGTH_SHORT).show();
            Log.e(TAG,volleyError.getMessage(),volleyError);
        }
    };




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialize();
        chooseRole();
        spinnerHandle();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("")||password.getText().toString().equals("")||!email.getText().toString().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")||!phone.getText().toString().matches("\\d+")||firstName.getText().toString().equals("")||surname.getText().toString().equals("")){
                    Toast.makeText(SignUp.this, "Please input valid information", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if(chosenJob.equals("Manager")){
                        createNewManager();
                    }else {
                        createNewTechnician();
                    }
                }
            }
        });

        quitSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * Input the technician data created into database.
     */
    private void createNewTechnician() {
        requestQueue=Volley.newRequestQueue(SignUp.this);

        StringRequest stringRequest=new StringRequest(Request.Method.POST,DBHelper.DB_ADDRESS+"createTechnician.php",listener,errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", username.getText().toString());
                map.put("password", password.getText().toString());
                map.put("email",email.getText().toString());
                map.put("phone",phone.getText().toString());
                map.put("firstName",firstName.getText().toString());
                map.put("surname",surname.getText().toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);


    }


    /**
     * Input the manager data created into database.
     */
    private void createNewManager() {
        requestQueue= Volley.newRequestQueue(SignUp.this);

        StringRequest stringRequest=new StringRequest(Request.Method.POST,DBHelper.DB_ADDRESS+"createManager.php",listener,errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", username.getText().toString());
                map.put("password", password.getText().toString());
                map.put("email",email.getText().toString());
                map.put("phone",phone.getText().toString());
                map.put("firstName",firstName.getText().toString());
                map.put("surname",surname.getText().toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);

    }


    private void chooseRole() {
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,m_roles);
        role.setAdapter(adapter);

        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                chosenJob=adapterView.getItemAtPosition(pos).toString();
                Log.d("chosenJob",chosenJob);
                if(chosenJob.equals("Manager")){
                    showRole.setImageDrawable(getResources().getDrawable(R.drawable.manager4));
                }else {
                    showRole.setImageDrawable(getResources().getDrawable(R.drawable.bike));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Initial the components.
     */
    public void initialize(){
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        email=(EditText)findViewById(R.id.email);
        phone=(EditText)findViewById(R.id.phoneNumber);
        firstName=(EditText)findViewById(R.id.firstName);
        surname=(EditText)findViewById(R.id.surname);
        role=(Spinner)findViewById(R.id.job);
        signUp=(Button)findViewById(R.id.signUp);
        goSignIn=(TextView)findViewById(R.id.goSignIn);
        quitSignUp=(ImageButton)findViewById(R.id.quitSignUp);
        showRole=(ImageView)findViewById(R.id.showRole);
        signUpTitle=(TextView)findViewById(R.id.signUpTitle);
        techInfo=new Bundle();
        managerInfo=new Bundle();

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Handle the chosen between manager and technician.
     */
    public void spinnerHandle(){
        String prompt=goSignIn.getText().toString();
        SpannableString spannedString=new SpannableString(prompt);
        spannedString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this, TechnicianLogin.class);
                startActivity(intent);
                finish();
            }
        },0,prompt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        goSignIn.setText(spannedString);
        goSignIn.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
