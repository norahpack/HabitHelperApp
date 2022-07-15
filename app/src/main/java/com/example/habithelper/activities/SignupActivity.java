package com.example.habithelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habithelper.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnCreate;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(new View.OnClickListener() {

            /**
             * Attempts to sign up a ParseUser with the username and password user input
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();

                checkValid();
            }
        });
    }

    /**
     * Attempts to login the newly-created ParseUser and navigate to the AccountCreationActivity
     */
    private void login() {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(SignupActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(SignupActivity.this, AccountSetupActivity.class));
                finish();
            }
        });
    }

    /**
     * Ensures the user inputted a valid username and password
     * If so, attempts to create a new ParseUser with the given username and password
     */
    private void checkValid() {
        if (username.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            newUser();
        } catch (ParseException e) {
            Toast.makeText(SignupActivity.this, "That username is already taken", Toast.LENGTH_SHORT).show();
            etUsername.setText("");
            etPassword.setText("");
        }
    }

    /**
     * Attempts to create a new ParseUser object with the user-inputted fields
     *
     * @throws ParseException
     */
    private void newUser() throws ParseException {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUp();

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(SignupActivity.this, "Issue with signing up", Toast.LENGTH_SHORT).show();
                    return;
                }
                // clears the username and password fields as a visual indication of signup success
                etUsername.setText("");
                etPassword.setText("");
                login();
            }
        });
    }
}