package com.example.MADPROJECT;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText username, password;
    private Button loginBtn;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.etuser);
        password = findViewById(R.id.etpass);
        loginBtn = findViewById(R.id.btn);

        dbHelper = new DBHelper(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (dbHelper.checkUser(user, pass)) {
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    // Start the main activity
                    startActivity(new Intent(Login.this, MainActivity.class));//change this Mainactivity intent to any other page

                    // Finish the current activity to prevent going back to it via back button
                    finish();
                } else {
                    Toast.makeText(Login.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}