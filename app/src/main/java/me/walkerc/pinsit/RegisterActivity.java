package me.walkerc.pinsit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = "RegisterActivity";

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPasswordText;
    private Button registerButton;
    private TextView loginText;

    private FirebaseAuth auth;

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

        auth = FirebaseAuth.getInstance();
    }

    /**
     * Click handler for registration button
     * @param v Clicked view
     */
    protected void onRegisterClick(View v) {
        Validator validator = new Validator(RegisterActivity.this);
        if (validator.validateRegistration(nameText.getText().toString(),
                passwordText.getText().toString(), confirmPasswordText.getText().toString())) {
            Log.i(TAG, "Registration credentials passed offline validation");

            auth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Validator.sendEmailVerification();

                                Intent ai = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(ai);
                            } else {
                                Log.w(TAG, "Failed to register user", task.getException());

                                if (task.getException() != null)
                                    Toast.makeText(RegisterActivity.this, (task.getException())
                                            .getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Click handler for login button
     * @param v Clicked view
     */
    protected void onLoginClick(View v) {
        Intent ai = new Intent(this, LoginActivity.class);
        startActivity(ai);
    }
}
