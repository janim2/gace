package com.gace.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.gace.app.objects.ChatMessage;
import com.gace.app.objects.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class Chat_details extends AppCompatActivity {

    private String group_id,group_name;
    private Intent details_intent;

    FirebaseListAdapter<ChatMessage> adapter;
    private ArrayList messagesArray = new ArrayList<ChatMessage>();
    private RecyclerView messages_RecyclerView;
    private RecyclerView.Adapter messages_mAdapter;

    private String messagess, message_text, message_time, message_user;
    private EditText message_;
    private ImageView attach,send_message;
    private CardView attachment_cardView;
    private int show_attach = 0;
    private FrameLayout chats_framelayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_details_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cdetails:
                Intent chat_details = new Intent(Chat_details.this, Chat_information.class);
                chat_details.putExtra("group_id",group_id);
                chat_details.putExtra("group_name",group_name);
                startActivity(chat_details);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        details_intent = getIntent();

        group_id = details_intent.getStringExtra("group_id");
        group_name = details_intent.getStringExtra("group_name");

        getSupportActionBar().setTitle(group_name);

        messages_RecyclerView = findViewById(R.id.messages_Recyclerview);
        message_ = findViewById(R.id.message_);
        attach = findViewById(R.id.attach);
        attachment_cardView = findViewById(R.id.attachment_layout);
        send_message = findViewById(R.id.send_message);
        chats_framelayout = findViewById(R.id.chat_frame);

        //getiing the messages variable initialization starts here
        if(isNetworkAvailable()){
            getTheMessagesKeys();
        }
        messages_RecyclerView.setHasFixedSize(true);

        messages_mAdapter = new MessageAdapter(getMessagesFromDatabase(),Chat_details.this);
        messages_RecyclerView.setAdapter(messages_mAdapter);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagess = message_.getText().toString().trim();
                if(!messagess.equals("")){
                    if(isNetworkAvailable()){
                        DatabaseReference get_messsages = FirebaseDatabase.getInstance().getReference("groups");//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        FirebaseDatabase.getInstance()
                                .getReference("group_chats")
                                .child(group_id)
                                .push()
                                .setValue(new ChatMessage(messagess,new Date().getTime(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()));

                        // Clear the input
                        message_.setText("");
                        getTheMessagesKeys();
                    }else{
                        Toast.makeText(Chat_details.this, "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Chat_details.this, "Message required", Toast.LENGTH_LONG).show();

                }
            }
        });

        chats_framelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(attachment_cardView.getVisibility() == View.VISIBLE){
                    show_attach = 0;
                    attachment_cardView.setVisibility(View.GONE);
                }
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show_attach == 0){
                    show_attach = 1;
                    attachment_cardView.setVisibility(View.VISIBLE);
                }else{
                    show_attach = 0;
                    attachment_cardView.setVisibility(View.GONE);
                }
            }
        });


    }

    private void getTheMessagesKeys() {
        DatabaseReference get_messsages = FirebaseDatabase.getInstance().getReference("group_chats")//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        .child(group_id);
        get_messsages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey() != null){
                            getTheMessages(child.getKey());
                        }
                    }
                }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chat_details.this,"Cancelled",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getTheMessages(String key) {
        DatabaseReference get_messsages = FirebaseDatabase.getInstance().getReference("group_chats")//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                .child(group_id)
                .child(key);
        get_messsages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("messageText")) {
                            message_text = child.getValue().toString();
                        }

                        if (child.getKey().equals("messageTime")) {
                            message_time = child.getValue().toString();
                        }

                        if (child.getKey().equals("messageUser")) {
                            message_user = child.getValue().toString();
                        }

                        else {
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }

                    ChatMessage obj = new ChatMessage(message_text,Long.valueOf(message_time),message_user);
                    messagesArray.add(obj);
                    messages_RecyclerView.setAdapter(messages_mAdapter);
                    messages_mAdapter.notifyDataSetChanged();
//                    no_chats.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chat_details.this, "Cancelled", Toast.LENGTH_LONG).show();

            }
        });
    }

    public ArrayList<ChatMessage> getMessagesFromDatabase(){
        return  messagesArray;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
