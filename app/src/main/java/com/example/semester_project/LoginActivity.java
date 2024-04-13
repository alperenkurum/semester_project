package com.example.semester_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button loginButton;
    Button signupButton;
    Button Change_password;
    FirebaseAuth myAuth;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            loginButton = findViewById(R.id.loginButton);
            signupButton = findViewById(R.id.signupButton);
            Change_password = findViewById(R.id.resetPassword);
            myAuth = FirebaseAuth.getInstance();
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginUser();
                }
            });
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LoginActivity.this,signUp.class));
                }
            });
            Change_password.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    startActivity(new Intent(LoginActivity.this,ChangePasswordEmailSent.class));
                }
            });

    }

    private void LoginUser() {
        String strEmail = email.getText().toString().trim();
        String strpassword = password.getText().toString().trim();
        if (TextUtils.isEmpty(strEmail)){
            email.setError( "Email cannot be empty" ) ;
            email.requestFocus( ) ;
        }else if (TextUtils.isEmpty(strpassword)){
            password.setError( "Password cannot be empty");
            password.requestFocus( );
        }else{
            myAuth.signInWithEmailAndPassword(strEmail,strpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"User logged in",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, PersonalInfo.class));
                    }else{
                        Toast.makeText(LoginActivity.this,"LOG IN ERROR: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}
