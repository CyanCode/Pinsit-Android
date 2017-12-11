package me.walkerc.pinsit;

import android.content.Intent;
import android.nfc.Tag;
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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView registerText;
    private TextView recoverPassText;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.createAccountText);
        recoverPassText = findViewById(R.id.forgotPasswordText);

        //Shared firebase instance
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser curUser = auth.getCurrentUser();
    }

    /**
     * Called when the login button is clicked
     * @param v clicked view
     */
    protected void onLoginClick(View v) {
        //Attempt login
        auth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Successful authentication, start MainActivity
                            Intent ai = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(ai);
                        } else {
                            Log.w(TAG, "Failed to sign in", task.getException());

                            if (task.getException() != null)
                                Toast.makeText(LoginActivity.this, (task.getException())
                                                .getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Called when create account button is clicked
     * @param v clicked view
     */
    protected void onRegisterClick(View v) {
        Intent ai = new Intent(this, RegisterActivity.class);
        startActivity(ai);
    }

    /**
     * Called when recover password is clicked
     * @param v clicked view
     */
    protected void onRecoverClick(View v) {

    }
}
