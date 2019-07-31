package com.gace.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Continue_Ticketing extends BaseActivity {

    EditText first_name,last_name,email,phone;
    TextView event_or_ticketName,ticket_type, success_message;
    String firstString,lastString,emailString,phoneString, id;

    Button save;
    Intent thecontinueIntent;
    String event_name, event_ID, event_prize, event_ticket_quantity,event_ticket_type;

    //    database reference object
    DatabaseReference reference;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue__ticketing);
        getSupportActionBar().setTitle("Finish Ticketing");

        first_name = (EditText)findViewById(R.id.first);
        last_name = (EditText)findViewById(R.id.last);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);
        event_or_ticketName = (TextView)findViewById(R.id.event_title);
        ticket_type = (TextView)findViewById(R.id.ticket_type);
        success_message = (TextView)findViewById(R.id.sucess_message);
        save = (Button)findViewById(R.id.save);

        thecontinueIntent = getIntent();

        event_name = thecontinueIntent.getStringExtra("event_name");
        event_ID = thecontinueIntent.getStringExtra("eventID");
        event_prize = thecontinueIntent.getStringExtra("event_prize");
        event_ticket_quantity = thecontinueIntent.getStringExtra("ticket_quantity");
        event_ticket_type = thecontinueIntent.getStringExtra("ticket_type");

        //        reference to the ticket node
        reference = FirebaseDatabase.getInstance().getReference("ticket");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        try {
            event_or_ticketName.setText(event_name);
            ticket_type.setText(event_ticket_type);
        }catch (NullPointerException e){

        }

        //setting onclick for the save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_to_register();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent();
        intent.putExtra("event_name",event_name);
        intent.putExtra("event_prize",event_prize);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void save_to_register() {
        firstString = first_name.getText().toString().trim();
        lastString = last_name.getText().toString().trim();
        emailString = email.getText().toString().trim();
        phoneString = phone.getText().toString().trim();

        if(!firstString.equals("")){
            if(!lastString.equals("")){
                if(!emailString.equals("")){
                    if(!phoneString.equals("")){
                        if(isNetworkAvailable()){
                            if(finish_registration()){
                                startActivity(new Intent(Continue_Ticketing.this,OrderComplete.class));
                            }
                        }else{
                            success_message.setTextColor(getResources().getColor(R.color.red));
                            success_message.setText("No Internet Connection");
                        }
                    }else{
                        phone.setError("Required");
                    }
                }else{
                    email.setError("Required");
                }
            }else{
                last_name.setError("Required");
            }
        }else{
            first_name.setError("Required");
        }
    }

    private boolean finish_registration() {
        showProgressDialog();
        try {
            id = reference.push().getKey();
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("quantity").setValue(event_ticket_quantity);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("type").setValue(event_ticket_type);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("prize").setValue(event_prize);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("firstname").setValue(firstString);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("lastname").setValue(lastString);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("email").setValue(emailString);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("phone_number").setValue(phoneString);
            hideProgressDialog();
            success_message.setTextColor(getResources().getColor(R.color.green));
            success_message.setText("Registration Complete");
        }catch (NullPointerException e){

        }
        return true;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
