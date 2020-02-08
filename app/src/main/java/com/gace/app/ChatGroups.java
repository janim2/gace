package com.gace.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.gace.app.objects.ChatGroupAdapter;
import com.gace.app.objects.Chat_group;
import com.gace.app.objects.Post;
import com.gace.app.objects.ReviewAdapter;
import com.gace.app.objects.Reviews;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatGroups extends AppCompatActivity {

    private FloatingActionButton add_group;
    private ArrayList groups_arraylist = new ArrayList<Chat_group>();
    private RecyclerView groups_RecyclerView;
    RecyclerView.LayoutManager mPostLayoutManager;
    private RecyclerView.Adapter groupsAdapter;
    private String g_image, g_name, g_description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);

        getSupportActionBar().setTitle("Groups");

        add_group = findViewById(R.id.add_group);
        groups_RecyclerView = findViewById(R.id.group_recycler);

        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatGroups.this, Add_Group.class));
            }
        });


        if(isNetworkAvailable()){
            Fetch_Groups_IDs();
        }else{

        }

        groups_RecyclerView.setHasFixedSize(true);

        mPostLayoutManager = new LinearLayoutManager(ChatGroups.this);
        groups_RecyclerView.setLayoutManager(mPostLayoutManager);

        groupsAdapter = new ChatGroupAdapter(getGroupsFromDatabase(),ChatGroups.this);
        groups_RecyclerView.setAdapter(groupsAdapter);
    }

    private void Fetch_Groups_IDs() {
        try{
            DatabaseReference fetch_groups_ID = FirebaseDatabase.getInstance().getReference("groups");//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            fetch_groups_ID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_Second_ID(child.getKey());
                        }
                    }else{
//                        Toast.makeText(ChatGroups.this,"Doesnt",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChatGroups.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });

        }catch (NullPointerException e){

        }
    }

    private void Fetch_Second_ID(final String key) {
        DatabaseReference confirm_presence = FirebaseDatabase.getInstance().getReference("groups")
                .child(key);
        confirm_presence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Check_if_a_group_member(key,child.getKey());
                    }
                }else{
                    Toast.makeText(ChatGroups.this, "No groups", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void Check_if_a_group_member(final String key, final String group_key) {
        try{
            DatabaseReference verify_ID = FirebaseDatabase.getInstance().getReference("group_participants")
            .child(group_key).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            verify_ID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Fetch_Groups(key,group_key);
                }else{
                    Toast.makeText(ChatGroups.this, "No groups", Toast.LENGTH_LONG).show();
                }

            }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChatGroups.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });

        }catch (NullPointerException e){

        }
    }

    private void Fetch_Groups(String group_owner_key, final String group_key) {
//        Toast.makeText(ChatGroups.this, group_owner_key, Toast.LENGTH_LONG).show();

        try{
            DatabaseReference fetch_groups = FirebaseDatabase.getInstance().getReference("groups")
                    .child(group_owner_key).child(group_key);//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            fetch_groups.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){

                            if(child.getKey().equals("image")){
                                g_image = child.getValue().toString();
                            }

                            if(child.getKey().equals("name")){
                                g_name = child.getValue().toString();
                            }

                            if(child.getKey().equals("description")){
                                g_description = child.getValue().toString();
                            }

                            else{
//                            Toast.makeText(MainActivity.this,"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                            }
                        }

                            Chat_group obj = new Chat_group(group_key,g_image,g_name,g_description);
                            groups_arraylist.add(obj);
                            groups_RecyclerView.setAdapter(groupsAdapter);
                            groupsAdapter.notifyDataSetChanged();
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChatGroups.this,"Cancelled",Toast.LENGTH_LONG).show();

                }
            });

        }catch(NullPointerException e){

        }

    }

    public ArrayList<Chat_group> getGroupsFromDatabase(){
        return  groups_arraylist;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
