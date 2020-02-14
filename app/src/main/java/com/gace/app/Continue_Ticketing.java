package com.gace.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Continue_Ticketing extends BaseActivity {

    EditText first_name,last_name,email,phone;
    TextView ticket_prize,event_or_ticketName,ticket_type, success_message;

    Button save;
    Intent thecontinueIntent;
    String id,event_name, event_ID, event_prize, event_ticket_quantity,event_ticket_type,
    firstname_string,lastname_string, email_string, phone_number_string;

    //    database reference object
    DatabaseReference reference;

    FirebaseUser firebaseUser;
    Accessories accessories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue__ticketing);
        getSupportActionBar().setTitle("Finish Ticketing");

        accessories = new Accessories(Continue_Ticketing.this);

        first_name = (EditText)findViewById(R.id.first);
        last_name = (EditText)findViewById(R.id.last);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);
        ticket_prize = (TextView)findViewById(R.id.ticket_prize);
        event_or_ticketName = (TextView)findViewById(R.id.event_title);
        ticket_type = (TextView)findViewById(R.id.ticket_type);
        success_message = (TextView)findViewById(R.id.sucess_message);
        save = (Button)findViewById(R.id.save);

        thecontinueIntent = getIntent();

        event_name = accessories.getString("thetitle");
        event_ID = accessories.getString("eventid");
        event_prize = accessories.getString("event_prize");
        event_ticket_quantity = accessories.getString("ticket_quantity");
        event_ticket_type = accessories.getString("ticket_type");

        //        reference to the ticket node
        reference = FirebaseDatabase.getInstance().getReference("ticket");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        try {
            FetchUser_Information();
            ticket_prize.setText(event_prize);
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

    private void FetchUser_Information() {
        try {
            DatabaseReference userinformation = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userinformation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){

                            //using the rate as the related items judging criteria
                            if(child.getKey().equals("firstname")){
                                firstname_string = child.getValue().toString();
                            }

                            if(child.getKey().equals("lastname")){
                                lastname_string = child.getValue().toString();
                            }

                            if(child.getKey().equals("email")){
                                email_string = child.getValue().toString();
                            }

                            if(child.getKey().equals("phone")){
                                phone_number_string = child.getValue().toString();
                            }
                        }
                        if(firstname_string != null){
                            first_name.setText(firstname_string);
                        }
                        if(lastname_string != null){
                            last_name.setText(lastname_string);
                        }
                        if(email_string != null){
                            email.setText(email_string);
                        }
                        if(phone_number_string != null){
                            phone.setText(phone_number_string);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void save_to_register() {
        firstname_string = first_name.getText().toString().trim();
        lastname_string = last_name.getText().toString().trim();
        email_string = email.getText().toString().trim();
        phone_number_string = phone.getText().toString().trim();

        if(!firstname_string.equals("")){
            if(!lastname_string.equals("")){
                if(!email_string.equals("")){
                    if(!phone_number_string.equals("")){
                        if(isNetworkAvailable()){
//                            if(finish_registration()){
                                FlutterwavePayment();
//                                startActivity(new Intent(Continue_Ticketing.this,OrderComplete.class));
//                            }
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

    private void FlutterwavePayment() {
        try{
            Random d = new Random();
            int ss = d.nextInt(454545454);
            String refid = ss+"";

            new RavePayManager(Continue_Ticketing.this).setAmount(Double.valueOf(event_prize.replace("GHC ","")))
                    .setCountry("GH")
                    .setCurrency("GHS")
                    .setPublicKey("FLWPUBK_TEST-0740441b742746440652b58a3145d715-X")//test
                    .setEncryptionKey("FLWSECK_TEST50024d4396a5")//test
////
//                    having a tracnscation fee error with this live key
//                    .setPublicKey("FLWPUBK-9f910be2cca606f52d4d0914badb51ec-X")//live
//                    .setEncryptionKey("cdfdb7d775ffbd5216cf6884")//live
//
//                .setPublicKey(emma_PUBLIC_KEY)
//                .setEncryptionKey(emma_ENCRYPTION_KEY)
//                    .isPreAuth(true)
                    .setfName(firstname_string)
                    .setlName(lastname_string)
                    .setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .setNarration("Gace Payment")
                    .setTxRef(refid)
                    .acceptGHMobileMoneyPayments(true)
                    .acceptCardPayments(false)
                    .allowSaveCardFeature(false)
                    .acceptCardPayments(true)
                    .allowSaveCardFeature(true)
                    .onStagingEnv(true)
                    .initialize();
        }catch (NullPointerException e){

        }
    }

    private boolean save_registration_data() {
        showProgressDialog();
        try {
            id = reference.push().getKey();
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("quantity").setValue(event_ticket_quantity);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("type").setValue(event_ticket_type);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("prize").setValue(event_prize);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("firstname").setValue(firstname_string);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("lastname").setValue(lastname_string);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("email").setValue(email_string);
            reference.child(firebaseUser.getUid()).child(event_ID).child(id).child("phone_number").setValue(phone_number_string);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
//                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "SUCCESS ", Toast.LENGTH_SHORT).show();
                save_registration_data();
                Intent summaryIntent = new Intent(Continue_Ticketing.this, OrderComplete.class);
                startActivity(summaryIntent);

            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
