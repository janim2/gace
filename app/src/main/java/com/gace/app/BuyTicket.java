package com.gace.app;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;

public class BuyTicket extends AppCompatActivity {

    //    View object
    TextView title,price,sucess_message;
    Button registerButton;
    Spinner typeSpinner,quantitySpinner;
    String type,quantity,id,eventPrice,eventTitle,eventID,total_prize;

    Intent buyticketIntent;
    String[] type_list = {"General Admission", "VIP", "Reserved Seats","Early Bird Discount",
    "Coded Discount","Multi-day Pass"};
    String[] quantity_list = {"1","2","3","4"};
    Accessories accessories;

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("therate",getIntent().getStringExtra("the_rate"));
//        returnIntent.putExtra("eventid",eventID);
//        returnIntent.putExtra("thetitle",eventTitle);
//        returnIntent.putExtra("theprize",eventPrice);
//        setResult(Activity.RESULT_OK,returnIntent);
//        finish();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);
        getSupportActionBar().setTitle("Ticketing");

        accessories = new Accessories(BuyTicket.this);
        buyticketIntent = getIntent();
        title = (TextView)findViewById(R.id.event_title);
        price = (TextView)findViewById(R.id.event_price);
        sucess_message = (TextView)findViewById(R.id.sucess_message);


        eventID = accessories.getString("eventid");
        eventTitle = accessories.getString("thetitle");
        eventPrice = accessories.getString("theprize");

//        getting views
        typeSpinner = (Spinner)findViewById(R.id.ticket_spinner);
        quantitySpinner = (Spinner)findViewById(R.id.quantity_spinner);
        registerButton = (Button)findViewById(R.id.ticket_register);

        try{
            title.setText(eventTitle);
            price.setText(eventPrice);
        }catch (NullPointerException e){}

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = type_list[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = "Regular";
            }
        });
        ArrayAdapter<String> type_of_event = new ArrayAdapter<String>(BuyTicket.this,android.R.layout.simple_list_item_1,type_list);
        typeSpinner.setAdapter(type_of_event);

        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quantity = quantity_list[position];
                if(quantity.equals("1")){
                    total_prize = eventPrice;
                    price.setText(total_prize);
                }
                if(quantity.equals("2")){
                    total_prize = String.valueOf(Float.valueOf(eventPrice.replace("GHC ",""))*2);
                    price.setText("GHC " + total_prize+"0");
                }

                if(quantity.equals("3")){
                    total_prize = String.valueOf(Float.valueOf(eventPrice.replace("GHC ",""))*3);
                    price.setText("GHC " +total_prize+"0");
                }

                if(quantity.equals("4")){
                    total_prize = String.valueOf(Float.valueOf(eventPrice.replace("GHC ",""))*4);
                    price.setText("GHC " +total_prize+"0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                quantity = "1";
            }
        });
        final ArrayAdapter<String> the_quantity = new ArrayAdapter<String>(BuyTicket.this,android.R.layout.simple_list_item_1,quantity_list);
        quantitySpinner.setAdapter(the_quantity);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent continue_registration = new Intent(BuyTicket.this, Continue_Ticketing.class);
//                    continue_registration.putExtra("eventID",eventID);
//                    continue_registration.putExtra("event_name",eventTitle);
                    accessories.put("event_prize",total_prize);
                    accessories.put("ticket_quantity",quantity);
                    accessories.put("ticket_type",type);
                    startActivityForResult(continue_registration,100);
            }
        });

    }

}
