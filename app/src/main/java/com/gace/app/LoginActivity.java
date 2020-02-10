package com.gace.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button mSignUpButton, loginface, logingmail;
    public  Button resend_verfication;
    private TextView open_register,forgot_password;
    private String semail, sgender, slocation, getSgender, sphone, susername;
    private Accessories loginaccessor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginaccessor = new Accessories(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();

//        app was crushing here in the login because this database reference wasnt found
//        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mSignInButton = findViewById(R.id.buttonSignIn);
//        mSignUpButton = findViewById(R.id.buttonSignUp);
        open_register = findViewById(R.id.open_register);
        forgot_password = findViewById(R.id.forgot_password);
        resend_verfication = findViewById(R.id.resend_verification);
//        loginface = findViewById(R.id.facebooklogin);
//        logingmail = findViewById(R.id.gmaillogin);

        // Click listeners
        mSignInButton.setOnClickListener(this);
//        mSignUpButton.setOnClickListener(this);

        open_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Reset_password.class));
            }
        });

//        resend_verfication.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
//
//                    }else{
//                        firebaseAuth.getCurrentUser().sendEmailVerification()
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            // email sent
//
//                                            // after email is sent just logout the user and finish this activity
//                                            Toast.makeText(LoginActivity.this,"Verification Email ReSent", Toast.LENGTH_LONG).show();
////                                            FirebaseAuth.getInstance().signOut();
//                                        }
//
//                                        else
//                                        {
//                                            // email not sent, so display message and restart the activity or do whatever you wish to do
//
//                                            //restart this activity
//                                            overridePendingTransition(0, 0);
//                                            finish();
//                                            overridePendingTransition(0, 0);
//                                            startActivity(getIntent());
//
//                                        }
//                                    }
//                                });
//                    }
//
//                }catch (NullPointerException e){
//
//                }
//            }
//        });


    }
//
    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
//        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();

        FetchUserDetails();
    }

    private void FetchUserDetails() {
        try {
            FirebaseAuth user = FirebaseAuth.getInstance();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getCurrentUser().getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("email")){
                            semail = child.getValue().toString();
                        }
                        if(child.getKey().equals("gender")){
                            sgender = child.getValue().toString();
                        }
                        if(child.getKey().equals("location")){
                            slocation = child.getValue().toString();
                        }
                        if(child.getKey().equals("phone")){
                            sphone = child.getValue().toString();
                        }
                        if(child.getKey().equals("username")){
                            susername = child.getValue().toString();
                        }
                    }
                    loginaccessor.put("saved_email",semail);
                    loginaccessor.put("saved_gender",sgender);
                    loginaccessor.put("saved_location",slocation);
                    loginaccessor.put("saved_phone",sphone);
                    loginaccessor.put("saved_username",susername);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }catch (NullPointerException e){

        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {
            signIn();
        }
//        else if (i == R.id.buttonSignUp) {
//            signUp();
//        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
