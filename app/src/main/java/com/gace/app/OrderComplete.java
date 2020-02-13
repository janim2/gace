package com.gace.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OrderComplete extends AppCompatActivity {

    private Button view_ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        getSupportActionBar().setTitle("Order Complete");

        view_ticket = findViewById(R.id.view_ticket);

        view_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderComplete.this, "Coming soon", Toast.LENGTH_LONG).show();
            }
        });
    }
}
