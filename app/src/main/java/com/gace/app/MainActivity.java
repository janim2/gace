package com.gace.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gace.app.objects.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;


public class MainActivity extends BaseActivity {

    RecyclerView PostRecyclerView;
    RecyclerView.Adapter mPostAdapter;
    RecyclerView.LayoutManager mPostLayoutManager;
    ArrayList resultPost = new ArrayList<Post>();
    String title,description,user,location,imageurl;
    ProgressBar loading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading  = (ProgressBar)findViewById(R.id.loading);

        PostRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        PostRecyclerView.setHasFixedSize(true);

        mPostLayoutManager = new LinearLayoutManager(MainActivity.this);
        PostRecyclerView.setLayoutManager(mPostLayoutManager);

        getPostIds();

        mPostAdapter = new PostAdapter(getPosts(), MainActivity.this);
        PostRecyclerView.setAdapter(mPostAdapter);


    }

    private void getPostIds() {
        DatabaseReference Postdatabase = FirebaseDatabase.getInstance().getReference().child("post");

        Postdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        FetchPosts(child.getKey());
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Doesnt",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Cancelled",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void FetchPosts(final String key) {
        DatabaseReference postData = FirebaseDatabase.getInstance().getReference().child("post").child(key);
        postData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("image")){
                            imageurl = child.getValue().toString();
                        }

                        if(child.getKey().equals("title")){
                            title = child.getValue().toString();
                        }

                        if(child.getKey().equals("description")){
                            description = child.getValue().toString();
                        }

                        if(child.getKey().equals("user")){
                            user = child.getValue().toString();
                        }

                        if(child.getKey().equals("location")){
                            location = child.getValue().toString();
                        }

                        else{
//                            Toast.makeText(MainActivity.this,"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }

                    String eventid = key;

                    Post obj = new Post(eventid,imageurl,description,location,title,user);
                    resultPost.add(obj);
                    PostRecyclerView.setAdapter(mPostAdapter);
                    mPostAdapter.notifyDataSetChanged();
                    loading.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });

    }


    public ArrayList<Post> getPosts(){
        return  resultPost;
    }

}
