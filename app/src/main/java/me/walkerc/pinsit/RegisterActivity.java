package me.walkerc.pinsit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPasswordText;
    private Button registerButton;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        confirmPasswordText = findViewById(R.id.confirmPasswordText);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.createAccountText);
    }

    /**
     * Click handler for registration button
     * @param v Clicked view
     */
    protected void onRegisterClick(View v) {

    }

    /**
     * Click handler for login button
     * @param v Clicked view
     */
    protected void onLoginClick(View v) {

    }
}
