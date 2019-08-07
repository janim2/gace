package com.gace.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.gace.app.objects.RegisterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar loading;

    private EditText name,email,mobile,location,password,confirm_password;
    private Button register;
    private Spinner gender;
    String nameString,emailString,mobileString,locationString,passwordString,confirmString,genderString;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    List<RegisterModel> registerModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if(user != null){
//                    sendEmailVerification();
//                }
//            }
//        };

        loading = (ProgressBar)findViewById(R.id.loading);
        confirm_password = (EditText) findViewById(R.id.confirm_password_input);
        gender = (Spinner)findViewById(R.id.gender);
        name = (EditText)findViewById(R.id.name_input);
        email = (EditText)findViewById(R.id.email_input);
        mobile =  (EditText)findViewById(R.id.mobile_input);
        location  = (EditText)findViewById(R.id.location_input);
        password = (EditText)findViewById(R.id.password_input);
        register = (Button)findViewById(R.id.register);


        firebaseAuth = FirebaseAuth.getInstance();
        registerModelList = new ArrayList<>();

//        adapter for the gender spinner
        List<String> list = new ArrayList<String>();
        list.add("male");
        list.add("female");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(arrayAdapter);
//        adapter ends here

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                nameString = name.getText().toString().trim();
                emailString = email.getText().toString().trim();
                mobileString = mobile.getText().toString().trim();
                locationString = location.getText().toString().trim();
                genderString = gender.getSelectedItem().toString();
                passwordString = password.getText().toString();
                confirmString = confirm_password.getText().toString();


                if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(emailString) || TextUtils.isEmpty(mobileString)
                        || TextUtils.isEmpty(locationString) || TextUtils.isEmpty(genderString) || TextUtils.isEmpty(passwordString))
                {
                    Toast.makeText(getApplicationContext(),"Fields required",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordString.length() < 6){
                    Toast.makeText(getApplicationContext(),"Password Too short",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validEmail(emailString)){
                    Toast.makeText(getApplicationContext(),"invalid email",Toast.LENGTH_SHORT).show();

                }

                if(!confirmString.equals(passwordString)){
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }

                firebaseAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(RegisterActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                }else {
                                    saveUserDetails();
//                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                                    sendEmailVerification();
//                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                }
                            }
                        });
            }
        });

    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent

                            // after email is sent just logout the user and finish this activity
                            Toast.makeText(RegisterActivity.this,"Verification Email Sent", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

    private void saveUserDetails(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(),"user already exist",Toast.LENGTH_SHORT).show();
                }else {
//                   saving user into database
                    databaseReference = FirebaseDatabase.getInstance().getReference("users");
//                    String id = databaseReference.push().getKey();

                    RegisterModel registerModel = new RegisterModel(nameString,emailString,mobileString,locationString,genderString);
                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(registerModel);

                    name.setText("");
                    email.setText("");
                    mobile.setText("");
                    location.setText("");
                    mobile.setText("");
                    password.setText("");

                    loading.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Registration successful",Toast.LENGTH_SHORT).show();
//                    finish();
//                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
//                    sendEmailVerification();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static boolean validEmail(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

}
