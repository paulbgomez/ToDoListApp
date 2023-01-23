package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Login extends AppCompatActivity {
    Button loginBtn;
    TextView registerBtn;
    EditText emailText, passwordText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize the variables from the view
        emailText = findViewById(R.id.emailBox);
        passwordText = findViewById(R.id.passwordBox);

        // Log in method
        loginBtn = findViewById(R.id.loginButton);

        loginBtn.setOnClickListener(view -> mAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    startActivity(new Intent(Login.this, MainActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(Login.this.getApplicationContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                }
            })
        );

        // Registering a new user
        registerBtn = findViewById(R.id.registerLink);
        registerBtn.setOnClickListener(view -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            boolean valid = true;
            // check email
            if (email.isEmpty()) {
                Drawable errorIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_error_outline_24);
                emailText.setError("Email cannot be empty", errorIcon);
                valid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Drawable errorIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_error_outline_24);
                emailText.setError("Invalid email format", errorIcon);
                valid = false;
            }

            // check password
            if (password.isEmpty()) {
                Drawable errorIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_error_outline_24);
                passwordText.setError("Password cannot be empty", errorIcon);
                valid = false;
            } else if (password.length() < 6) {
                Drawable errorIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_error_outline_24);
                passwordText.setError("Password must be at least 6 characters long", errorIcon);
                valid = false;
            }

            if (valid) {
                mAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this.getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this.getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    };
}