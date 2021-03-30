package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPass;
    Button btnLogin;
    TextView txtReg, Skip, adminLogin;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        adminLogin = findViewById(R.id.adminLogin);

        btnLogin = findViewById(R.id.btnLogin);

        txtReg = findViewById(R.id.txtReg);
        Skip = findViewById(R.id.Skip);

        fAuth = FirebaseAuth.getInstance();

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });

        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password;

                email = etEmail.getText().toString();
                password = etPass.getText().toString();

                if (TextUtils.isEmpty(email)){
                    etEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    etPass.setError("Password is required");
                    return;
                }

                if (password.length() <8){
                    etPass.setError("Password Must be >=8 Characters");
                    return;
                }
                //Authentication

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()){
                          Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(getApplicationContext(), MainActivity.class));
                          finish();
                      }
                      else{
                          Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                      }
                    }
                });
            }
        });
        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent R = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(R);
            }
        });

    }
}