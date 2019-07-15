package com.gace.app;

import android.app.Person;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class EventRegistration extends AppCompatActivity {

    EditText name, email, telephone, occcupation, institution;
    FirebaseAuth mAuth;
    Button registerforEvent;
    String eventid, sname, semail, stelephone, soccupation, sinstitution,userId;
    ProgressBar loading;
    TextView result;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);
        getSupportActionBar().setTitle("Registration");

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emil);
        telephone = (EditText) findViewById(R.id.telephone);
        occcupation = (EditText) findViewById(R.id.occupation);
        institution = (EditText) findViewById(R.id.institution);
        result = (TextView) findViewById(R.id.result);
        registerforEvent = (Button) findViewById(R.id.register);
        loading = (ProgressBar) findViewById(R.id.loading);
        mAuth = FirebaseAuth.getInstance();

        if(user!=null) {
            try {
                user = mAuth.getCurrentUser();
                email.setText(user.getEmail());
            }catch (NullPointerException e){

            }
        }
        registerforEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);

                try {
                    //getting the user id
                    userId = user.getUid();

                }catch (NullPointerException e){

                }

                //getting theevent id
                Random random = new Random();
                int number = random.nextInt(999999);
                String eventidhh = "Ev"+number+""+"day";

                eventid = getIntent().getStringExtra("usethis_id");

                //getting others from editText
                sname = name.getText().toString().trim();
                semail = email.getText().toString().trim();
                stelephone = telephone.getText().toString().trim();
                soccupation = occcupation.getText().toString().trim();
                sinstitution = institution.getText().toString().trim();

                if(!sname.equals("")){
                    if(!semail.equals("")){
                        if(!stelephone.equals("")){
                            if(!soccupation.equals("")){
                                if(!sinstitution.equals("")){
                                    if(uploadtoDatabase()){
                                        loading.setVisibility(View.GONE);
                                        result.setVisibility(View.VISIBLE);
                                        result.setText("Success");
                                    }else{
                                        loading.setVisibility(View.GONE);
                                        result.setVisibility(View.VISIBLE);
                                        result.setTextColor(getResources().getColor(R.color.red));
                                        result.setText("Failure");
                                    }

                                }else{
                                    institution.setError("Required");
                                }
                            }else{
                                occcupation.setError("Required");
                            }
                        }else{
                            telephone.setError("Required");
                        }
                    }else{
                        email.setError("Required");
                    }
                }else{
                    name.setError("Required");
                }

            }
        });

    }

    private boolean uploadtoDatabase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("eventgoers").child(eventid);
        userRef.child("userid").setValue(userId);
        userRef.child("name").setValue(sname);
        userRef.child("email").setValue(semail);
        userRef.child("telephone").setValue(stelephone);
        userRef.child("occupation").setValue(soccupation);
        userRef.child("institution").setValue(sinstitution);
        return true;
    }
}
