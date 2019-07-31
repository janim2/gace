package com.gace.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gace.app.objects.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Report_Activity extends AppCompatActivity {

    EditText email,reason,message;
    Button report;
    String emailString,reasonString,messageString;
    DatabaseReference databaseReference;
    List<Report_Activity> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        email = (EditText)findViewById(R.id.email_input);
        reason = (EditText)findViewById(R.id.reason_input);
        message = (EditText)findViewById(R.id.message_input);
        report = (Button)findViewById(R.id.report);

        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailString = email.getText().toString().trim();
                reasonString = reason.getText().toString().trim();
                messageString = message.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(reasonString) || TextUtils.isEmpty(messageString))
                {
                    Toast.makeText(getApplicationContext(),"fields are required",Toast.LENGTH_SHORT).show();
                }else if (!emailString.matches(emailPattern)){
                    Toast.makeText(getApplicationContext(),"invalid email address",Toast.LENGTH_SHORT).show();
                }else {
//                    saving to database
                    databaseReference = FirebaseDatabase.getInstance().getReference("reports");
                    String id = databaseReference.push().getKey();
                    Report report1 = new Report(id,emailString,reasonString,messageString);
//                    databaseReference.child(id).child("email").setValue(emailString);
//                    databaseReference.child(id).child("id").setValue(id);
//                    databaseReference.child(id).child("message").setValue(messageString);
//                    databaseReference.child(id).child("reason").setValue(reasonString);
                    databaseReference.child(id).setValue(report1);

                    email.setText("");
                    reason.setText("");
                    message.setText("");

                    Toast.makeText(getApplicationContext(),"report submitted",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
