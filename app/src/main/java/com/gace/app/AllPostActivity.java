package com.gace.app;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gace.app.objects.Post;
import com.gace.app.objects.RecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllPostActivity extends AppCompatActivity {
    private static String TAG = AllPostActivity.class.getSimpleName();
    DatabaseReference databaseReference;
    List<Post> postList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_post);
//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        this.setTitle("All Events");

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        databaseReference = FirebaseDatabase.getInstance().getReference("post");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Post post = dataSnapshot1.getValue(Post.class);
                    postList.add(post);
                }

                adapter = new RecyclerViewAdapter(AllPostActivity.this,postList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG,"failed in reading value",databaseError.toException());
            }
        });

    }
}