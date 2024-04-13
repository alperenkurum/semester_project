package com.example.semester_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordEmailSent extends AppCompatActivity {
    ImageView image;
    private Button SendButton;
    private EditText Editemail;
    private FirebaseAuth auth;
    String stremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password_email_sent);
        SendButton = findViewById(R.id.SendButton);
        Editemail = findViewById(R.id.email);
        image = findViewById(R.id.LoginImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordEmailSent.this,LoginActivity.class));

            }
        });
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stremail = Editemail.getText().toString();
                if(TextUtils.isEmpty(stremail)){
                    Toast.makeText(ChangePasswordEmailSent.this,"Please enter registered email address.",Toast.LENGTH_SHORT).show();
                    Editemail.setError("Email is required");
                    Editemail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(stremail).matches()) {
                    Toast.makeText(ChangePasswordEmailSent.this,"Please enter valid email address.",Toast.LENGTH_SHORT).show();
                    Editemail.setError("Valid email is required");
                    Editemail.requestFocus();
                }else{
                    sendResetMail();
                }
            }
        });


    }
    private void sendResetMail() {
        auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(stremail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(ChangePasswordEmailSent.this,"Reset password link has been sent to the registered email.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangePasswordEmailSent.this,"Reset password link can't sent to the entered email.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}