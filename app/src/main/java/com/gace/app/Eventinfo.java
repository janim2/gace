package com.gace.app;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gace.app.objects.Post;
import com.gace.app.objects.RelatedPostAdapter;
import com.gace.app.objects.ReviewAdapter;
import com.gace.app.objects.Reviews;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


public class Eventinfo extends AppCompatActivity implements OnMapReadyCallback {

    TextView eventtitle, eventdescription, eventlocation, eventlikes,dateandtime, prize,
            see_all_posts,numbergoingTextView, see_all_reviewsTextView;
    ImageView eventimage;
    Toolbar goBack;

    ImageLoader loader = ImageLoader.getInstance();
    Button gotoEvent, no_reviews;
    String seventid, simage, stitle, sdescription, slocation,stheuser ,sdatetime,
            srate_of_event,sprize_of_event,sdate_of_event,stime_of_event;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    //related Items variables here
    private ArrayList relatedParts = new ArrayList<Post>();
    private RecyclerView related_items_RecyclerView;
    private RecyclerView.Adapter related_items_mPostAdapter;
    private String title,description,user,location,slikes,imageurl,rate,sprize,the_date,the_time;

    private float downXpos = 0;
    private float downYpos = 0;
    private boolean touchcaptured = false;
    String check_uncheck_favourites = "0";

    Accessories accessories;
    int counter = 0;

    //variables for loading reviews from database
    private ArrayList reviewsArray = new ArrayList<Reviews>();
    private RecyclerView review_RecyclerView;
    private RecyclerView.Adapter reviewAdapter;
    private RecyclerView.LayoutManager review_mPostLayoutManager;
    private String review_name, review_message, review_title;

    //variables ends here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_info_menu,menu);
        String checking_if_liked = accessories.getString(seventid+"i_have_liked_it");
        MenuItem favour = menu.findItem(R.id.favourites);

        if(checking_if_liked!=null){
            if(checking_if_liked.equals("no")){
                favour.setIcon(getResources().getDrawable(R.drawable.done_favourites));
            }else{
                favour.setIcon(getResources().getDrawable(R.drawable.favourites));
            }
        }else{
            favour.setIcon(getResources().getDrawable(R.drawable.done_favourites));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
//                Toast.makeText(Eventinfo.this,"Share",Toast.LENGTH_LONG).show();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String sharebody = simage;
                share.putExtra(Intent.EXTRA_SUBJECT,stitle);
                share.putExtra(Intent.EXTRA_TEXT,sharebody);
                startActivity(Intent.createChooser(share,"Share via"));
                break;

            case R.id.contact_organizer:
                startActivity(new Intent(Eventinfo.this,Contact_Organizer.class));
                break;

            case R.id.report:
                startActivity(new Intent(Eventinfo.this, Report_Activity.class));
                break;

            case R.id.favourites:
                if(check_uncheck_favourites.equals("0")){
                    check_uncheck_favourites = "1";
                    if(add_to_favourites(slikes)){
                        item.setIcon(getResources().getDrawable(R.drawable.done_favourites));
                    }
                }
                else if(check_uncheck_favourites.equals("1")){
                    check_uncheck_favourites = "0";
                    if(remove_from_favourites(slikes)){
                        item.setIcon(getResources().getDrawable(R.drawable.favourites));
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean add_to_favourites(String number_of_likes){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post");
        number_of_likes = String.valueOf(Integer.valueOf(number_of_likes) + 1);
        accessories.put("thelikes",number_of_likes);
        eventlikes.setText(number_of_likes);
        accessories.put(seventid+"i_have_liked_it","yes");
        databaseReference.child(seventid).child("likes").setValue(number_of_likes);
      return true;
    }

    public boolean remove_from_favourites(String number_of_likes){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post");
            number_of_likes = String.valueOf(Integer.valueOf(accessories.getString("thelikes")) - 1);
            accessories.put("thelikes",number_of_likes);
            eventlikes.setText(number_of_likes);
            accessories.put(seventid+"i_have_liked_it","no");
            databaseReference.child(seventid).child("likes").setValue(number_of_likes);
//        }

        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventinfo);
        Intent intent = getIntent();

        accessories = new Accessories(Eventinfo.this);

        //settings for google maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        eventtitle = (TextView) findViewById(R.id.eventtitle);
        eventdescription = (TextView) findViewById(R.id.description);
        eventlocation = (TextView) findViewById(R.id.location);
        eventlikes = (TextView) findViewById(R.id.eventlikes);
        dateandtime = (TextView) findViewById(R.id.timeanddate);
        eventimage = (ImageView) findViewById(R.id.eventimage);
//        goBack = (Toolbar) findViewById(R.id.goback);
        gotoEvent = (Button) findViewById(R.id.going);
        no_reviews = (Button) findViewById(R.id.no_reviews);
        prize = (TextView) findViewById(R.id.prize);
        see_all_posts = (TextView) findViewById(R.id.see_all_posts);
        numbergoingTextView = (TextView) findViewById(R.id.number_going);
        see_all_reviewsTextView = (TextView) findViewById(R.id.see_all);

        seventid = accessories.getString("eventid");
        simage = accessories.getString("theimage");
        stitle = accessories.getString("thetitle");
        sdescription = accessories.getString("thedescription");
        slocation = accessories.getString("thelocation");
        slikes = accessories.getString("thelikes");
        stheuser = accessories.getString("theuser");
        srate_of_event = accessories.getString("therate");
        sprize_of_event = accessories.getString("theprize");
        sdate_of_event = accessories.getString("thedate");
        stime_of_event = accessories.getString("thetime");

        DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).
                defaultDisplayImageOptions(theImageOptions).build();
        ImageLoader.getInstance().init(config);
//
        String imagelink = simage;
        loader.displayImage(imagelink, eventimage);

        eventtitle.setText(stitle);
        eventdescription.setText(sdescription);
        eventlocation.setText(slocation);
        eventlikes.setText(slikes);
        dateandtime.setText(sdate_of_event + " at " + stime_of_event + "GMT");

        no_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Eventinfo.this,AddEventReview.class));
            }
        });

        see_all_reviewsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Eventinfo.this,EventReviews.class));
            }
        });

        see_all_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        if (srate_of_event != null) {
            if (srate_of_event.equals("Free")) {
                prize.setText("FREE!");
                gotoEvent.setText("Register");
                gotoEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent theintent = new Intent(Eventinfo.this, EventRegistration.class);
                        theintent.putExtra("usethis_id", seventid);
                        startActivity(theintent);
                    }
                });

            } else {
                gotoEvent.setText("Ticket");
                prize.setText(sprize_of_event);
                numbergoingTextView.setVisibility(View.GONE);
                gotoEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent theintent = new Intent(Eventinfo.this, BuyTicket.class);
                        theintent.putExtra("usethis_id", seventid);
                        theintent.putExtra("useevent_name", stitle);
                        theintent.putExtra("useeent_prize", sprize_of_event);
                        theintent.putExtra("the_rate", srate_of_event);
                        startActivity(theintent);
                    }
                });
            }

        } else {
            srate_of_event = "Paid";
        }


        //related items variables starts here
        related_items_RecyclerView = findViewById(R.id.recyclerView_relatedProducts);
        related_items_RecyclerView.setHasFixedSize(true);

//        related_items_mPostLayoutManager = new LinearLayoutManager(ItemDetailsActivity.this);
//        related_items_RecyclerView.setLayoutManager(related_items_mPostLayoutManager);
        try {
            getRelatedItems_ID();
            getNumberof_Persons_going();
            getCustomerReview_ID();
        }catch (NullPointerException e){

        }

        related_items_mPostAdapter = new RelatedPostAdapter(getrelatedParts(), Eventinfo.this);
        related_items_RecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downXpos = motionEvent.getX();
                        downYpos = motionEvent.getY();
                        touchcaptured = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float xdisplacement = Math.abs(motionEvent.getX() - downXpos);
                        float ydisplacement = Math.abs(motionEvent.getY() - downYpos);
                        if( !touchcaptured && xdisplacement > ydisplacement && xdisplacement > 10) {
                            recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                            touchcaptured = true;
                        }
                        break;
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
        related_items_RecyclerView.setAdapter(related_items_mPostAdapter);
//        related ends here

        //values for fetching feeds from database
        review_RecyclerView = (RecyclerView) findViewById(R.id.customer_reviewRecycler);
        no_reviews = (Button) findViewById(R.id.no_reviews);

        review_RecyclerView.setHasFixedSize(true);

        reviewAdapter = new ReviewAdapter(getReviewsFromDatabase(),Eventinfo.this);
        review_RecyclerView.setAdapter(reviewAdapter);
        //values ends here

    }

    private void getNumberof_Persons_going() {
        DatabaseReference numberofpersons = FirebaseDatabase.getInstance().getReference("eventgoers");
        numberofpersons.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals(seventid)){
                            getCount(child.getKey());
                        }else{
                            Spannable befirst = new SpannableString("No persons going yet! BE THE FIRST");
                            befirst.setSpan(new ForegroundColorSpan(Color.BLACK), 22, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            numbergoingTextView.setText(befirst);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCount(String key) {
        DatabaseReference thereference =FirebaseDatabase.getInstance().getReference("eventgoers").child(key);
        thereference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey()!=null){
                            if(child.getKey().length() > 15){
                                counter += 1;
                            }
//                                Toast.makeText(Eventinfo.this,child.getKey(),Toast.LENGTH_LONG).show();
//                            numbergoingTextView.setText(child.getChildrenCount()+""+" persons going");
                        }
                    }
                    numbergoingTextView.setText(String.valueOf(counter)+" person(s) going");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRelatedItems_ID() {
        try{
            DatabaseReference relatedItemsreference = FirebaseDatabase.getInstance().getReference().child("post");

            //limiting number of items to be fetched
            Query query = relatedItemsreference.limitToLast(3);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            FetchPosts(child.getKey());
                        }
                    }else{
                        Toast.makeText(Eventinfo.this,"Doesnt",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Eventinfo.this,"Cancelled",Toast.LENGTH_LONG).show();
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

                            //using the rate as the related items judging criteria
                            if(child.getKey().equals("rate")){
                                rate = child.getValue().toString();
                            }

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

                            if(child.getKey().equals("likes")){
                                slikes = child.getValue().toString();
                            }

                            if(child.getKey().equals("prize")){
                                sprize = child.getValue().toString();
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

                        if(rate.equals(srate_of_event)){
                            String eventid = key;

                            Post obj = new Post(eventid,imageurl,description,location,slikes,title,user,rate,sprize,the_date,the_time);
                            relatedParts.add(obj);
                            related_items_RecyclerView.setAdapter(related_items_mPostAdapter);
                            related_items_mPostAdapter.notifyDataSetChanged();
                        }

//                        loading.setVisibility(GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Eventinfo.this,"Cancelled",Toast.LENGTH_LONG).show();

                }
            });

        }catch(NullPointerException e){

        }

    }

    private void getCustomerReview_ID() {
        DatabaseReference partdatabase = FirebaseDatabase.getInstance().getReference("review").child(seventid);

        partdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
//                        Toast.makeText(ItemReview.this,child.getKey(),Toast.LENGTH_LONG).show();
                        if(child.getKey().length() > 15){
                            get3ReviewsNow(child.getKey());
                        }
                    }
                }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Eventinfo.this,"Cancelled",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void get3ReviewsNow(String key) {
        DatabaseReference postData = FirebaseDatabase.getInstance().getReference("review").child(seventid).child(key);

        //restricting the number of reviews that are fetched from the database with a query
        Query getOnly_three = postData.limitToFirst(3);

        getOnly_three.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("message")){
                            review_message = child.getValue().toString();
                        }

                        if(child.getKey().equals("title")){
                            review_title = child.getValue().toString();
                        }

                        if(child.getKey().equals("name")){
                            review_name = child.getValue().toString();
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }

                    Reviews obj = new Reviews(review_message,review_title,review_name);
                    reviewsArray.add(obj);
                    review_RecyclerView.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();
                    no_reviews.setText("GIVE YOUR FEEDBACK");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Eventinfo.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
//
        // Add a marker in knust and move the camera
        LatLng hospital = new LatLng(6.686408, -1.574373);
//        LatLng police = new LatLng(6.688970, -1.564693);
        mMap.addMarker(new MarkerOptions().position(hospital).title("Knust Hospital"));
//        mMap.addMarker(new MarkerOptions().position(police).title("Police Station"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hospital));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 13.0f ) );

    }

    public ArrayList<Post> getrelatedParts(){
        return  relatedParts;
    }

    public ArrayList<Reviews> getReviewsFromDatabase(){
        return  reviewsArray;
    }
}
