package com.gace.app;

import android.content.Intent;
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

public class BuyTicket extends BaseActivity {

    //    View object
    TextView title,price,sucess_message;
    Button registerButton;
    Spinner typeSpinner,quantitySpinner;
    String type,quantity,id,eventPrice,eventTitle,eventID,total_prize;

    //    database reference object
    DatabaseReference reference;

    FirebaseUser firebaseUser;

    Intent buyticketIntent;
    String[] type_list = {"General Admission", "VIP", "Reserved Seats","Early Bird Discount",
    "Coded Discount","Multi-day Pass"};
    String[] quantity_list = {"1","2","3","4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        buyticketIntent = getIntent();
        title = (TextView)findViewById(R.id.event_title);
        price = (TextView)findViewById(R.id.event_price);
        sucess_message = (TextView)findViewById(R.id.sucess_message);

        eventID = buyticketIntent.getStringExtra("usethis_id");
        eventTitle = buyticketIntent.getStringExtra("useevent_name");
        eventPrice = buyticketIntent.getStringExtra("useeent_prize");

//        reference to the ticket node
        reference = FirebaseDatabase.getInstance().getReference("ticket");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
                showProgressDialog();

                try{
                    id = reference.push().getKey();
                    reference.child(id).child(firebaseUser.getUid()).child("quantity").setValue(quantity);
                    reference.child(id).child(firebaseUser.getUid()).child("type").setValue(type);
                    reference.child(id).child(firebaseUser.getUid()).child("prize").setValue(total_prize);
                    hideProgressDialog();
                    sucess_message.setTextColor(getResources().getColor(R.color.green));
                    sucess_message.setText("Registration Complete");
                }catch (NullPointerException e){

                }
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
