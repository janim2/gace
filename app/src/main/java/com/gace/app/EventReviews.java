package com.gace.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gace.app.objects.ReviewAdapter;
import com.gace.app.objects.Reviews;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventReviews extends AppCompatActivity {
    private TextView  no_reviewsTextView, rating_count;
    String rating_number, seventid;

    //    strings for loading the parts from the database
    private ArrayList review_result = new ArrayList<Reviews>();
    private RecyclerView reviewRecyclerView;
    private RecyclerView.Adapter mReviewAdapter;
    private RecyclerView.LayoutManager mReviewLayoutManager;
    private String name, message, title;
    private ProgressBar loading;
    private int rate_count_int = 0;
//    strings ends here

    Accessories accessories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_reviews);
        getSupportActionBar().setTitle("Customer Feedback");

        accessories = new Accessories(EventReviews.this);

        seventid = accessories.getString("eventid");

        no_reviewsTextView  = (TextView) findViewById(R.id.no_reviews);
        //items to load from the database starts here
        loading  = (ProgressBar)findViewById(R.id.loading);
        reviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewProducts);
        reviewRecyclerView.setHasFixedSize(true);

//        mReviewLayoutManager = new GridLayoutManager(MainActivity.this,2, LinearLayoutManager.VERTICAL,false);
        mReviewLayoutManager = new LinearLayoutManager(EventReviews.this);
        reviewRecyclerView.setLayoutManager(mReviewLayoutManager);

        if(isNetworkAvailable()){
            getReviewIds();
        }else {
            Toast.makeText(EventReviews.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
        mReviewAdapter = new ReviewAdapter(getReviews(),EventReviews.this);

        reviewRecyclerView.setAdapter(mReviewAdapter);
//        items ends here
    }

    private void getReviewIds() {
        loading.setVisibility(View.VISIBLE);
        DatabaseReference partdatabase = FirebaseDatabase.getInstance().getReference("review").child(seventid);

        partdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().length() > 15){
                            getReviewsNow(child.getKey());
                        }else{
                            loading.setVisibility(View.GONE);
                        }
                    }
                }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EventReviews.this,"Cancelled",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getReviewsNow(String key) {
        DatabaseReference postData = FirebaseDatabase.getInstance().getReference("review").child(seventid).child(key);
        postData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("message")){
                            message = child.getValue().toString();
                        }
                        if(child.getKey().equals("title")){
                            title = child.getValue().toString();
                        }
                        if(child.getKey().equals("name")){
                            name = child.getValue().toString();
                        }
                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }

                    Reviews obj = new Reviews(message,title,name);
                    review_result.add(obj);
                    reviewRecyclerView.setAdapter(mReviewAdapter);
                    mReviewAdapter.notifyDataSetChanged();
                    no_reviewsTextView.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EventReviews.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    public ArrayList<Reviews> getReviews(){
        return  review_result;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
