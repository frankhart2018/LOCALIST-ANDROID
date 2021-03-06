package com.vastukosh.app.localist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        if(sp.getBoolean("logged", false)) {
            Intent gotoHome = new Intent(this, HomeActivity.class);
            startActivity(gotoHome);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(sp.getBoolean("logged", false));
    }

    public void loginBtnClicked(View view) {
        final EditText email = findViewById(R.id.logEmailText);
        final String emailString = email.getText().toString();
        EditText password = findViewById(R.id.logPasswordText);
        String passwordString = password.getText().toString();

        if(emailString.matches("") || passwordString.matches("")) {
            Toast.makeText(getApplicationContext(), "Complete the form!", Toast.LENGTH_SHORT).show();
        } else {

            String errorString = "";

            if(!isEmailValid(emailString)) {
                errorString += "Invalid email address!\r\n";
            }

            if(passwordString.length() < 8) {
                errorString += "Password cannot be less than 8 characters!";
            }

            if(errorString.length() == 0) {

                Toast.makeText(getApplicationContext(), "Logging you in...", Toast.LENGTH_SHORT).show();

                final String url = "http://<website-link>/?login=1&email=" + emailString + "&password=" + passwordString;

                // Request a string response
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");

                                    if(status.matches("1")) {
                                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                                        Intent gotoHome = new Intent(getApplicationContext(), HomeActivity.class);
                                        sp.edit().putBoolean("logged", true).apply();
                                        sp.edit().putString("email", emailString).apply();
                                        startActivity(gotoHome);
                                    } else if(status.matches("0")) {
                                        Toast.makeText(getApplicationContext(), "Account does not exist!", Toast.LENGTH_SHORT).show();
                                    } else if(status.matches("2")) {
                                        Toast.makeText(getApplicationContext(), "Incorrect credentials!", Toast.LENGTH_SHORT).show();
                                    } else if(status.matches("-1")) {
                                        Toast.makeText(getApplicationContext(), "Oops, there was some error. Please come back later!", Toast.LENGTH_SHORT).show();
                                    } else if(status.matches("3")) {
                                        Toast.makeText(getApplicationContext(), "Email not verified!", Toast.LENGTH_SHORT).show();
                                        Intent emailVerifyIntent = new Intent(getApplicationContext(), EmailVerifyActivity.class);
                                        emailVerifyIntent.putExtra("email", emailString);
                                        startActivity(emailVerifyIntent);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Error handling
                        System.out.println("Something went wrong!");
                        error.printStackTrace();

                    }
                });

                // Add the request to the queue
                Volley.newRequestQueue(this).add(stringRequest);

            } else {
                Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void logForgotBtnClicked(View view) {

        Intent gotoForgot = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(gotoForgot);

    }

    public void logSignupBtnClicked(View view) {

        Intent gotoSignup = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(gotoSignup);

    }

    public Boolean isEmailValid(String email) {
        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches();
    }
}
