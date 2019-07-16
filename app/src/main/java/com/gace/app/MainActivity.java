package com.gace.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gace.app.objects.Post;
import com.google.firebase.auth.FirebaseAuth;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("NitchApp");
        loading  = (ProgressBar)findViewById(R.id.loading);

        PostRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        PostRecyclerView.setHasFixedSize(true);

        mPostLayoutManager = new LinearLayoutManager(MainActivity.this);
        PostRecyclerView.setLayoutManager(mPostLayoutManager);


        try{

            getPostIds();

            mPostAdapter = new PostAdapter(getPosts(), MainActivity.this);
            PostRecyclerView.setAdapter(mPostAdapter);
        }catch (NoClassDefFoundError e){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile:
                startActivity(new Intent(MainActivity.this,MyProfile.class));

                break;
            case R.id.action_upload:
                startActivity(new Intent(MainActivity.this,UploadEvent.class));
                break;

            case R.id.action_logout:
                if(isNetworkAvailable()){
                    if(logout()){
                        Toast.makeText(MainActivity.this,"Logout Successfull",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean logout() {
        AlertDialog.Builder logout = new AlertDialog.Builder(MainActivity.this, R.style.Myalert);
        logout.setTitle("Logging Out?");
        logout.setMessage("Are you sure you want to logout");
        logout.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    FirebaseAuth.getInstance().signOut();
                }catch(NullPointerException e){

                }
            }
        });

        logout.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        logout.show();

        return true;
    }

    private void getPostIds() {
        try{
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

        }catch (NullPointerException e){

        }

    }

    private void FetchPosts(final String key) {
        try {
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

        }catch(NullPointerException e){

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public ArrayList<Post> getPosts(){
        return  resultPost;
    }

}