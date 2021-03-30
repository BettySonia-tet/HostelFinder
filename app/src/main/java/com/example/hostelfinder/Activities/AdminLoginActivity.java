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

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etEmail,etPass;
    private Button btnLogin;
    private TextView back;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_login);

        etEmail = findViewById(R.id.etEmail);
        etPass =findViewById(R.id.etPass);
        btnLogin =findViewById(R.id.btnLogin);
        back = findViewById(R.id.back);
        mAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAdminLogin();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminLoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    private void performAdminLogin() {
        String email, password;

        email = etEmail.getText().toString();
        password = etPass.getText().toString();


        if (TextUtils.isEmpty(email)){
            etEmail.setError("Email required!");
            return;
        }

        if (TextUtils.isEmpty(password)){
            etPass.setError("Password Required!");
            return;
        }

        if (!(email.equals("admin@gmail.com") && password.equals("123456"))){
            Toast.makeText(this, "wrong password or email", Toast.LENGTH_SHORT).show();
        }

        if ((email.equals("admin@gmail.com") && password.equals("123456"))){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminLoginActivity.this, AddHostelActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(AdminLoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}