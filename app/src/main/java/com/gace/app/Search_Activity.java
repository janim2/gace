package com.gace.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gace.app.objects.Post;
import com.gace.app.objects.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;

//import com.algolia.search.saas.Query;

public class Search_Activity extends AppCompatActivity{ //implements AbsListView.OnScrollListener  {

    // Constants
    private static final int LOAD_MORE_THRESHOLD = 5;
    private static final int HITS_PER_PAGE = 20;

    String imageurl, name, description, price, sellersNumber;

    RecyclerView PostRecyclerView;
    RecyclerView.Adapter mPostAdapter;
    RecyclerView.LayoutManager mPostLayoutManager;
    ArrayList resultPost = new ArrayList<Post>();
    String title,user,location,rate,prize,the_date,the_time;
    ProgressBar loading;
    FirebaseUser firebaseUser;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        MenuItem searchMenuItem = menu.findItem( R.id.action_search ); // get my MenuItem with placeholder submenu

//        automatically open search View when activity starts
        searchMenuItem.expandActionView();
//        ends here
        SearchView searchView = (SearchView)searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toLowerCase();
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("carparts").child("Offers");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("post");
                Query query1 = reference.orderByKey().startAt(query);//.endAt(query+"\uf8ff");
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                FetchPosts(child.getKey());                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


//        try{
//            helper.registerSearchView(this, menu, R.id.actionn_search);
//        }catch (NullPointerException e){
//
//        }
        return super.onCreateOptionsMenu(menu);
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

                            if(child.getKey().equals("rate")){
                                rate = child.getValue().toString();
                            }

                            if(child.getKey().equals("prize")){
                                prize = child.getValue().toString();
                            }

                            if(child.getKey().equals("date")){
                                the_date = child.getValue().toString();
                            }

                            if(child.getKey().equals("time")){
                                the_time = child.getValue().toString();
                            }
                            else{
//                            Toast.makeText(MainActivity.this,"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                            }
                        }

                        String eventid = key;

                        Post obj = new Post(eventid,imageurl,description,location,title,user,rate,prize,the_date,the_time);
                        resultPost.add(obj);
                        PostRecyclerView.setAdapter(mPostAdapter);
                        mPostAdapter.notifyDataSetChanged();
                        loading.setVisibility(GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Search_Activity.this,"Cancelled",Toast.LENGTH_LONG).show();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Search");
        loading  = (ProgressBar)findViewById(R.id.loading);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        PostRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        PostRecyclerView.setHasFixedSize(true);

        mPostLayoutManager = new LinearLayoutManager(Search_Activity.this);
        PostRecyclerView.setLayoutManager(mPostLayoutManager);

        mPostAdapter = new PostAdapter(getPosts(), Search_Activity.this);
        PostRecyclerView.setAdapter(mPostAdapter);

    }

    public ArrayList<Post> getPosts(){
        return  resultPost;
    }

}
