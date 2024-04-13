package com.example.semester_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PersonalInfo extends AppCompatActivity {

    ImageView instagram,twitter,pp;
    Button EditPage, backButton;
    TextView name_layout,surname_layout,phoneNo_layout,schoolNAme_layout,department_layout,accountType,email_layout,schoolNO;
    TextView x_txt,ig_txt;
    ImageView ig_iamge,x_image, wp_image, phone_image,email_image;
    SwitchCompat ig_sw,x_sw,wp_sw, email_sw;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    String UserID;
    String userEmail;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_info);
        Spinner spinner = findViewById(R.id.Semester);
        Spinner spinner2 = findViewById(R.id.Education);
        backButton = findViewById(R.id.BackButton);
        ig_iamge = findViewById(R.id.ig_image);
        x_image = findViewById(R.id.x_image);
        wp_image = findViewById(R.id.wp_image);
        phone_image = findViewById(R.id.phone_image);
        email_image = findViewById(R.id.email_image);
        ig_sw = findViewById(R.id.ig_switch);
        x_sw = findViewById(R.id.x_switch);
        wp_sw = findViewById(R.id.phone_switch);
        email_sw = findViewById(R.id.email_switch);
        x_txt = findViewById(R.id.Twitter_link);
        ig_txt = findViewById(R.id.Instagram_link);

        String[] string_s = new String[]{" ","Preparatory","1","2","3","4","5","6","7","8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,string_s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String[] string_s2 = new String[]{" ","Undergraduate","Master","PhD"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,string_s2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        name_layout = findViewById(R.id.name);
        surname_layout = findViewById(R.id.surname);
        schoolNO = findViewById(R.id.schoolNO);
        phoneNo_layout = findViewById(R.id.phoneNo);
        schoolNAme_layout = findViewById(R.id.name_school);
        department_layout = findViewById(R.id.departmant);
        accountType = findViewById(R.id.AccountType);
        email_layout = findViewById(R.id.email);
        pp = findViewById(R.id.pp);
        EditPage = findViewById(R.id.EditButton);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserID = auth.getCurrentUser().getUid();

        EditPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalInfo.this, PersonalInfoChange.class));
            }
        });
        wp_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://api.whatsapp.com/send?phone="+phoneNo_layout.getText();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        phone_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + phoneNo_layout.getText());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
        email_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:" + email_layout.getText())
                        .buildUpon()
                        .build();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(emailIntent);
            }
        });
        firestore.collection("People").document(UserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot!= null && documentSnapshot.exists()){
                                name_layout.setText(documentSnapshot.getString("Name"));
                                surname_layout.setText(documentSnapshot.getString("Surname"));
                                schoolNO.setText(documentSnapshot.getString("SchoolNo"));
                                schoolNAme_layout.setText(documentSnapshot.getString("School Name"));

                                if(Objects.equals(documentSnapshot.getString("Private Instagram"), "true")){
                                    ig_sw.setChecked(true);
                                    ig_txt.setText("***");
                                }else{
                                    ig_sw.setChecked(false);
                                    ig_txt.setText(documentSnapshot.getString("Instagram Account"));
                                }
                                ig_sw.setEnabled(false);
                                if(Objects.equals(documentSnapshot.getString("Private X"), "true")){
                                    x_sw.setChecked(true);
                                    x_txt.setText("***");
                                }else{
                                    x_sw.setChecked(false);
                                    x_txt.setText(documentSnapshot.getString("X Account"));
                                }
                                x_sw.setEnabled(false);
                                if(Objects.equals(documentSnapshot.getString("Private Wp"), "true")){
                                    wp_sw.setChecked(true);
                                    phoneNo_layout.setText("***");
                                }else{
                                    wp_sw.setChecked(false);
                                    phoneNo_layout.setText(documentSnapshot.getString("Phone"));
                                }
                                wp_sw.setEnabled(false);
                                if(Objects.equals(documentSnapshot.getString("Private Email"), "true")){
                                    email_sw.setChecked(true);
                                    email_layout.setText("***");
                                }else{
                                    email_sw.setChecked(false);
                                    if (user != null) {
                                        userEmail = user.getEmail();
                                        email_layout.setText(userEmail);
                                    }
                                }
                               if(!documentSnapshot.getString("Profile Picture Url").isEmpty()){
                                    Picasso.get().load(documentSnapshot.getString("Profile Picture Url")).into(pp);
                                }
                                email_sw.setEnabled(false);
                                accountType.setText(documentSnapshot.getString("Account level"));
                                department_layout.setText(documentSnapshot.getString("Department"));
                                if(Objects.equals(documentSnapshot.getString("Account level"), "Instructor")){
                                    schoolNO.setHint("Instructors does not have a school no");
                                }
                                index = getIndex(spinner,documentSnapshot.getString("Semester"));
                                spinner.setSelection(index);
                                spinner.setEnabled(false);
                                index = getIndex(spinner2,documentSnapshot.getString("Ongoing Educational Info"));
                                spinner2.setSelection(index);
                                spinner2.setEnabled(false);
                            }
                        }
                    }
                });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalInfo.this, LoginActivity.class));
            }
        });


    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }
}
