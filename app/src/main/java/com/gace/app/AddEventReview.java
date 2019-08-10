package com.gace.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddEventReview extends AppCompatActivity {
    private TextView rating_text, done_rating, howimproveTextView, reviewTitleTextView, nameTextView;
    private Button submit_button;
    private FirebaseUser firebaseUser;
    private String theevent_id,sname,suggested_improve, review_title, fullname;
    private ProgressBar loading;

    private Accessories accessories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_review);
        getSupportActionBar().setTitle("Add Review");

        accessories = new Accessories(AddEventReview.this);

        done_rating =  findViewById(R.id.done_rating);
        howimproveTextView =  findViewById(R.id.how_improveTextView);
        reviewTitleTextView =  findViewById(R.id.review_titleTextView);
        nameTextView =  findViewById(R.id.name_textView);
        submit_button =  findViewById(R.id.submit_button);
        loading =  findViewById(R.id.loading);
        theevent_id = accessories.getString("eventid");

        //submit button logic starts here
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggested_improve = howimproveTextView.getText().toString().trim();
                fullname = nameTextView.getText().toString().trim();
                review_title = reviewTitleTextView.getText().toString().trim();

                if(!suggested_improve.equals("")){
                    if(!fullname.equals("")){
                        if(!review_title.equals("")){
                            if(isNetworkAvailable()){
                                loading.setVisibility(View.VISIBLE);
                                if(addToReview()){
                                    loading.setVisibility(View.GONE);
                                    done_rating.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Toast.makeText(AddEventReview.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            reviewTitleTextView.setError("Required");
                        }
                    }else{
                        nameTextView.setError("Required");
                    }
                }else{
                    howimproveTextView.setError("Required");
                }


            }
        });
//        submit button logic ends here

        if(isNetworkAvailable()){
            getUserInfo();
        }else{
            Toast.makeText(AddEventReview.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }


    private boolean addToReview() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference addtoReview = FirebaseDatabase.getInstance().getReference("review");
        String reviewid = addtoReview.push().getKey();
        addtoReview.child(theevent_id).child(firebaseUser.getUid()).child("title").setValue(review_title);
        addtoReview.child(theevent_id).child(firebaseUser.getUid()).child("message").setValue(suggested_improve);
        addtoReview.child(theevent_id).child(firebaseUser.getUid()).child("name").setValue(fullname);
        return true;
    }

    private void getUserInfo() {
        try {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference getUserInformation = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            getUserInformation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("name")){
                                sname = child.getValue().toString();
                            }

                        }
                        if(sname!=null){
                            nameTextView.setText(sname);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (NullPointerException e){

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
