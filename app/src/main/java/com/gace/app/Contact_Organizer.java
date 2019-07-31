package com.gace.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gace.app.objects.Organiser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Contact_Organizer extends AppCompatActivity {
    EditText name,email,message;
    Button save;
    String nameString,emailString,messageString,reasonString;
    Spinner reason;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    List<Organiser> organiserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__organizer);

        name = (EditText) findViewById(R.id.name_input);
        email = (EditText) findViewById(R.id.email_input);
        message = (EditText) findViewById(R.id.message_input);
        save = (Button) findViewById(R.id.organiser);
        reason = (Spinner) findViewById(R.id.reason_spinner);

        databaseReference = FirebaseDatabase.getInstance().getReference("organiser");

        List<String> list = new ArrayList<String>();
        list.add("Question about event");
        list.add("Question about my ticket");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reason.setAdapter(arrayAdapter);

        organiserList = new ArrayList<>();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameString = name.getText().toString().trim();
                emailString = email.getText().toString().trim();
                messageString = message.getText().toString().trim();
                reasonString = reason.getSelectedItem().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                emailString = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                email.setText(emailString);

                if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(emailString) || TextUtils.isEmpty(messageString)
                        || TextUtils.isEmpty(reasonString)) {
                    Toast.makeText(getApplicationContext(), "fields required", Toast.LENGTH_SHORT).show();
                } else if (!emailString.matches(emailPattern)) {

                    Toast.makeText(getApplicationContext(), "invalid email address", Toast.LENGTH_SHORT).show();
                } else {

                    //saving to database
                    String id = databaseReference.push().getKey();
                    Organiser organiser = new Organiser(id, nameString, emailString, reasonString, messageString);
                    databaseReference.child(id).setValue(organiser);

                    name.setText("");
                    email.setText("");
                    message.setText("");
                    Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    }
