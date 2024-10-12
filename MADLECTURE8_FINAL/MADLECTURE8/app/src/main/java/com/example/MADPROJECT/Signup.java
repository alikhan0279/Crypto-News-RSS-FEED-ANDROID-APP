package com.example.MADPROJECT;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends AppCompatActivity {

    private EditText username, email, password;
    private Button signupBtn;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.etuser);
        email = findViewById(R.id.etemail);
        password = findViewById(R.id.etpass);
        signupBtn = findViewById(R.id.btn);

        dbHelper = new DBHelper(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String mail = email.getText().toString();
                String pass = password.getText().toString();

                if (dbHelper.insertUser(user, mail, pass)) {
                    Toast.makeText(Signup.this, "User Registered", Toast.LENGTH_SHORT).show();

                    // Start the login activity
                    startActivity(new Intent(Signup.this, Login.class));

                    // Finish the current activity to prevent going back to it via back button
                    finish();
                } else {
                    Toast.makeText(Signup.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}