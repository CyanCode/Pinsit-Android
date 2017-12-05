package me.walkerc.pinsit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView registerText;
    private TextView recoverPassText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.createAccountText);
        recoverPassText = findViewById(R.id.forgotPasswordText);
    }

    /**
     * Called when the login button is clicked
     * @param v clicked view
     */
    protected void onLoginClick(View v) {

    }

    /**
     * Called when create account button is clicked
     * @param v clicked view
     */
    protected void onRegisterClick(View v) {

    }

    /**
     * Called when recover password is clicked
     * @param v clicked view
     */
    protected void onRecoverClick(View v) {

    }
}
