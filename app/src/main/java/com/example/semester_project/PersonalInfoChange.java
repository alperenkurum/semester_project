package com.example.semester_project;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PersonalInfoChange extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    EditText name_layout,surname_layout,phoneNo_layout,schoolNAme_layout,department_layout;
    ImageView pp;
    TextView accountType,schoolNo,email_layout,password;
    EditText igTxt,xTxt;
    SwitchCompat ig_sw,x_sw, phone_sw, email_sw;
    Button pp_delete,pp_change;
    Button Save,Delete,Back;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    String UserID;
    String userEmail;
    int index,index2;
    Boolean pp_changed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_change);

        igTxt = findViewById(R.id.Instagram_link);
        xTxt = findViewById(R.id.Twitter_link);
        ig_sw = findViewById(R.id.ig_switch);
        email_sw = findViewById(R.id.email_switch);
        x_sw = findViewById(R.id.x_switch);
        phone_sw = findViewById(R.id.phone_switch);
        password = findViewById(R.id.password);
        name_layout = findViewById(R.id.name);
        surname_layout = findViewById(R.id.surname);
        schoolNo = findViewById(R.id.schoolNO);
        phoneNo_layout = findViewById(R.id.phoneNo);
        schoolNAme_layout = findViewById(R.id.name_school);
        department_layout = findViewById(R.id.departmant);
        accountType = findViewById(R.id.AccountType);
        email_layout = findViewById(R.id.email);
        Save = findViewById(R.id.SaveAcButton);
        Delete = findViewById(R.id.DeleteAcButton);
        Back = findViewById(R.id.BackButton);
        Spinner spinner = findViewById(R.id.Semester);
        Spinner spinner2 = findViewById(R.id.Education);
        String[] string_s = new String[]{"","Preparatory","1","2","3","4","5","6","7","8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,string_s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String[] string_s2 = new String[]{"","Undergraduate","Master","PhD"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,string_s2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        pp = findViewById(R.id.pp);
        pp_change = findViewById(R.id.ChangePic);
        pp_delete = findViewById(R.id.DeletePic);

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        UserID = auth.getCurrentUser().getUid();

        firestore.collection("People").document(UserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot!= null && documentSnapshot.exists()){
                                name_layout.setText(documentSnapshot.getString("Name"));
                                surname_layout.setText(documentSnapshot.getString("Surname"));
                                phoneNo_layout.setText(documentSnapshot.getString("Phone"));
                                schoolNo.setText(documentSnapshot.getString("SchoolNo"));
                                igTxt.setText(documentSnapshot.getString("Instagram Account"));
                                xTxt.setText(documentSnapshot.getString("X Account"));
                                schoolNAme_layout.setText(documentSnapshot.getString("School Name"));
                                if (user != null) {
                                    userEmail = user.getEmail();
                                    email_layout.setText(userEmail);
                                }
                                if(Objects.equals(documentSnapshot.getString("Private Email"), "true")){
                                    email_sw.setChecked(true);
                                }else{
                                    email_sw.setChecked(false);
                                }
                                if(Objects.equals(documentSnapshot.getString("Private Instagram"), "true")){
                                    ig_sw.setChecked(true);
                                }else{
                                    ig_sw.setChecked(false);
                                }
                                if(Objects.equals(documentSnapshot.getString("Private X"), "true")){
                                    x_sw.setChecked(true);
                                }else{
                                    x_sw.setChecked(false);
                                }
                                if(Objects.equals(documentSnapshot.getString("Private Wp"), "true")){
                                    phone_sw.setChecked(true);
                                }else{
                                    phone_sw.setChecked(false);
                                }
                                if(!documentSnapshot.getString("Profile Picture Url").isEmpty()){
                                    Picasso.get().load(documentSnapshot.getString("Profile Picture Url")).into(pp);
                                }
                                accountType.setText(documentSnapshot.getString("Account level"));
                                department_layout.setText(documentSnapshot.getString("Department"));
                                index = getIndex(spinner,documentSnapshot.getString("Semester"));
                                spinner.setSelection(index);
                                index2 = getIndex(spinner2,documentSnapshot.getString("Ongoing Educational Info"));
                                spinner2.setSelection(index2);
                                if(Objects.equals(documentSnapshot.getString("Account level"), "Instructor")){
                                    schoolNo.setHint("Instructors does not have a school no");
                                    spinner.setEnabled(false);
                                    spinner2.setEnabled(false);
                                }else{
                                    spinner.setEnabled(true);
                                    spinner2.setEnabled(true);
                                }
                            }
                        }
                    }
                });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoChange.this);
                builder.setTitle("Reset Password");
                builder.setMessage("Are you sure that you want to change the password?");
                builder.setNegativeButton("Cancel",null);
                builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(PersonalInfoChange.this,ChangePasswordEmailSent.class));
                    }
                });
                builder.show();
            }
        });
        email_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                Map<String, Object> People = new HashMap<>();

                if(isChecked){//public
                    People.put("Private Email","true");
                }else{
                    People.put("Private Email","false");
                }
                FirebaseFirestore.getInstance().collection("People").document(userID).set(People, SetOptions.merge());
            }
        });
        ig_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                Map<String, Object> People = new HashMap<>();

                if(isChecked){//public
                    People.put("Private Instagram","true");
                }else{
                    People.put("Private Instagram","false");
                }
                FirebaseFirestore.getInstance().collection("People").document(userID).set(People, SetOptions.merge());
            }
        });
        x_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                Map<String, Object> People = new HashMap<>();

                if(isChecked){//public
                    People.put("Private X","true");
                }else{
                    People.put("Private X","false");
                }
                FirebaseFirestore.getInstance().collection("People").document(userID).set(People, SetOptions.merge());
            }
        });
        phone_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                Map<String, Object> People = new HashMap<>();

                if(isChecked){//public
                    People.put("Private Wp","true");
                }else{
                    People.put("Private Wp","false");
                }
                FirebaseFirestore.getInstance().collection("People").document(userID).set(People, SetOptions.merge());
            }
        });

        pp_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoChange.this);
                builder.setTitle("Delete Profile Picture");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get back to the default picture
                        pp.setImageResource(R.drawable.pp);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userID = user.getUid();
                        Map<String, Object> People = new HashMap<>();
                        saveImage2Database(userID,People,"People");
                        pp_changed = true;

                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });
        pp_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkAndRequestPermissions(PersonalInfoChange.this)){
                    chooseImage(PersonalInfoChange.this);
                    pp_changed = true;
                }
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firestore Update
                String name = name_layout.getText().toString().trim();
                String surname = surname_layout.getText().toString().trim();
                String phoneNo = phoneNo_layout.getText().toString().trim();
                String schoolName = schoolNAme_layout.getText().toString().trim();
                String department = department_layout.getText().toString().trim();
                String email_s = email_layout.getText().toString().trim();
                String ig_acc = igTxt.getText().toString().trim();
                String x_acc = xTxt.getText().toString().trim();

                Map<String, Object> People = new HashMap<>();
                People.put("Name", name);
                People.put("Surname", surname);
                People.put("Phone", phoneNo);
                People.put("School Name", schoolName);
                People.put("Department", department);
                People.put("Instagram Account", ig_acc);
                People.put("X Account",x_acc);
                People.put("Ongoing Educational Info",spinner2.getSelectedItem().toString());
                People.put("Semester",spinner.getSelectedItem().toString());
                FirebaseFirestore.getInstance().collection("People").document(UserID).set(People, SetOptions.merge());
                if (!email_s.isEmpty()) {
                    if (!email_s.equals(user.getEmail())) {
                        user.verifyBeforeUpdateEmail(email_s);
                    }
                }
                //Update Image
                if(pp_changed) {
                    saveImage2Database(UserID, People, "People");
                }
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalInfoChange.this, PersonalInfo.class));
            }
        });
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete User Part
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

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(PersonalInfoChange.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(PersonalInfoChange.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(PersonalInfoChange.this);
                }
                break;
        }
    }
    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        pp.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                pp.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }
    private void saveImage2Database(String userID, Map<String, Object> People, String CollectionPath){
        String email = email_layout.getText().toString();

        StorageReference ppRef = storage.getReference().child("ProfilePhotos").child(email+".png");
        pp.setDrawingCacheEnabled(true);
        pp.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) pp.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ppRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ppRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        People.put("Profile Picture Url",imageUrl);
                        FirebaseFirestore.getInstance().collection(CollectionPath).document(userID).set(People, SetOptions.merge());
                    }
                });
            }
        });
    }

}