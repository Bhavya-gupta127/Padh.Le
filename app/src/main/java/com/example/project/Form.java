package com.example.project;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Form extends AppCompatActivity {


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText age;
    TextView age_field;
    RadioGroup gender, activities, freetime, goout, health;
    Button Continue;
    String Gender, Age, Activities, FreeTime, GoOut, Health;
    String url = "https://category.pythonanywhere.com/predict";
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        age = (EditText) findViewById(R.id.age);
        age_field = (TextView) findViewById(R.id.age_field);
        gender = (RadioGroup) findViewById(R.id.q1);
        activities = (RadioGroup) findViewById(R.id.q2);
        freetime = (RadioGroup) findViewById(R.id.q3);
        goout = (RadioGroup) findViewById(R.id.q4);
        health = (RadioGroup) findViewById(R.id.q5);
        Continue = (Button) findViewById(R.id.cont);

    }

    public void onClick(View v) {
        if (TextUtils.isEmpty(age.getText())) {
            age_field.setVisibility(View.VISIBLE);
        } else {
            age_field.setVisibility(View.INVISIBLE);
            Age = age.getText().toString();
            Gender = ((RadioButton) findViewById(gender.getCheckedRadioButtonId())).getContentDescription().toString();
            Activities = ((RadioButton) findViewById(activities.getCheckedRadioButtonId())).getContentDescription().toString();
            FreeTime = ((RadioButton) findViewById(freetime.getCheckedRadioButtonId())).getContentDescription().toString();
            GoOut = ((RadioButton) findViewById(goout.getCheckedRadioButtonId())).getContentDescription().toString();
            Health = ((RadioButton) findViewById(health.getCheckedRadioButtonId())).getContentDescription().toString();
            Predict();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public void Predict() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // created a JSON object of the response we got
                            JSONObject jsonObject = new JSONObject(response);
                            // we stored the category we got into result
                            result = jsonObject.getString("category");
                            Log.d("TAG", "onCreate: ");
                            // details is storing details of user from database
                            Map<String, Object> details = new HashMap<>();
                            details.put("Name", user.getDisplayName());
                            details.put("Email", user.getEmail());
                            details.put("Uid", user.getUid());
                            details.put("Category", result);
                            db.collection("users").document(user.getUid()).collection("Details").document(user.getUid())
                                    .set(details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Firestore", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error writing document", e);
                                        }
                                    });
                            Intent i=new Intent();
                            setResult(RESULT_OK, i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sex", String.valueOf(Gender));
                params.put("age", String.valueOf(Age));
                params.put("activities", String.valueOf(Activities));
                params.put("freetime", String.valueOf(FreeTime));
                params.put("goout", String.valueOf(GoOut));
                params.put("health", String.valueOf(Health));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(Form.this);
        queue.add(stringRequest);
    }
}