package com.gace.app;

import android.app.Person;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class EventRegistration extends AppCompatActivity {

    EditText name, email, telephone, occcupation;
    FirebaseAuth mAuth;
    Button registerforEvent;
    String eventid, sname, semail, stelephone, soccupation;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emil);
        telephone = (EditText) findViewById(R.id.telephone);
        occcupation = (EditText) findViewById(R.id.occupation);
        registerforEvent = (Button) findViewById(R.id.register);
        loading = (ProgressBar) findViewById(R.id.loading);


        registerforEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();

                //getting the user id
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

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

                if(!sname.equals("")){
                    if(!semail.equals("")){
                        if(!stelephone.equals("")){
                            if(!soccupation.equals("")){
                                loading.setVisibility(View.VISIBLE);
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("eventgoers").child(eventid);
                                userRef.child("userid").setValue(userId);
                                userRef.child("name").setValue(sname);
                                userRef.child("email").setValue(semail);
                                userRef.child("telephone").setValue(stelephone);
                                userRef.child("occupation").setValue(soccupation);
                                loading.setVisibility(View.GONE);
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
}
