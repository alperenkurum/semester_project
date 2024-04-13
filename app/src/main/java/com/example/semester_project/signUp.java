package com.example.semester_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class signUp extends AppCompatActivity {
    EditText name;
    EditText surname;
    EditText no;
    EditText email;
    EditText pass1;
    EditText pass2;
    Button signup;
    Button BackloginScreen;
    private FirebaseAuth myAuth;
    private FirebaseFirestore firestore;
    Button back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = findViewById(R.id.f_name);
        surname = findViewById(R.id.s_name);
        no = findViewById(R.id.no);
        no.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        email = findViewById(R.id.email);
        pass1 = findViewById(R.id.password);
        pass2 = findViewById(R.id.password2);
        signup = findViewById(R.id.buttonSign);
        BackloginScreen = findViewById(R.id.LoginScreenBack);
        myAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        no.setEnabled(false);
        email.addTextChangedListener(textWatcher);
        BackloginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUp.this,LoginActivity.class));
            }
        });
        signup.setOnClickListener(v->{
            String enteredName = name.getText().toString().trim();
            String enteredSName = surname.getText().toString().trim();
            String enteredno = no.getText().toString().trim();
            String enteredEmail = email.getText().toString().trim();
            String enteredPass1 = pass1.getText().toString().trim();
            String enteredPass2 = pass2.getText().toString().trim();
            if(enteredName.isEmpty()){
                Toast.makeText(signUp.this,"Please enter your name.",Toast.LENGTH_SHORT).show();
            } else if (enteredSName.isEmpty()) {
                Toast.makeText(signUp.this,"Please enter your surname.",Toast.LENGTH_SHORT).show();
            }  else if (enteredEmail.isEmpty()) {
                Toast.makeText(signUp.this,"Please enter your school email.",Toast.LENGTH_SHORT).show();
            }  else if(enteredEmail.endsWith("@std.yildiz.edu.tr")){
                if(enteredno.isEmpty() || enteredno.length() != 8) {
                    Toast.makeText(signUp.this, "Please enter your school number.", Toast.LENGTH_SHORT).show();
                }
                } else if (enteredPass1.isEmpty()) {
                Toast.makeText(signUp.this,"Please enter your password.",Toast.LENGTH_SHORT).show();
            }else if (enteredPass2.isEmpty()) {
                Toast.makeText(signUp.this,"Please confirm your password.",Toast.LENGTH_SHORT).show();
            }else if(!enteredPass1.equals(enteredPass2)){
                Toast.makeText(signUp.this,"Passwords are not same.",Toast.LENGTH_SHORT).show();
            }else if (!(enteredEmail.endsWith("@std.yildiz.edu.tr") || enteredEmail.endsWith("@yildiz.edu.tr"))){
                Toast.makeText(signUp.this,"Error!! You should use school mails.",Toast.LENGTH_SHORT).show();
            }else{
                myAuth.createUserWithEmailAndPassword(enteredEmail,enteredPass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(signUp.this,LoginActivity.class));
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = user.getUid();
                            Toast.makeText(signUp.this,"User Registered",Toast.LENGTH_SHORT).show();
                            Map<String, Object> People = new HashMap<>();
                            if(enteredEmail.endsWith("@std.yildiz.edu.tr")){
                                People.put("Account level", "Student");
                            }else if(enteredEmail.endsWith("@yildiz.edu.tr")){
                                People.put("Account level", "Instructors");
                            }
                            People.put("Name", enteredName);
                            People.put("Surname", enteredSName);
                            People.put("SchoolNo", enteredno);
                            People.put("Ongoing Educational Info","");
                            People.put("Phone","");
                            People.put("School Name","");
                            People.put("Semester","");
                            People.put("Department","");
                            People.put("Profile Picture Url","");
                            FirebaseFirestore.getInstance().collection("People").document(userID)
                                    .set(People)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(signUp.this,"ERROR->>> "+e.getMessage(),Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }else{
                            Toast.makeText(signUp.this,"ERROR: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }
    TextWatcher textWatcher = (new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String email_s = email.getText().toString().trim();
            if(!email_s.isEmpty()){
                if(email_s.endsWith("@yildiz.edu.tr")){
                    no.setEnabled(false);
                    no.setText("");
                    no.setHint("Intructors does not have a Student No");
                }else if (email_s.endsWith("@std.yildiz.edu.tr")){
                    no.setEnabled(true);
                    no.setHint("Student No");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
}