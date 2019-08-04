package com.gace.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gace.app.objects.Post;
import com.gace.app.objects.RelatedPostAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    TextView eventtitle, eventdescription, eventlocation, dateandtime, prize, see_all_posts;
    ImageView eventimage;
    Toolbar goBack;

    ImageLoader loader = ImageLoader.getInstance();
    Button gotoEvent;
    String seventid, simage, stitle, sdescription, slocation,stheuser ,sdatetime,
            srate_of_event,sprize_of_event,sdate_of_event,stime_of_event;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    //related Items variables here
    ArrayList relatedParts = new ArrayList<Post>();
    RecyclerView related_items_RecyclerView;
    RecyclerView.Adapter related_items_mPostAdapter;
    String title,description,user,location,imageurl,rate,sprize,the_date,the_time;

    private float downXpos = 0;
    private float downYpos = 0;
    private boolean touchcaptured = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_info_menu,menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventinfo);
        Intent intent = getIntent();

        //settings for google maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        eventtitle = (TextView) findViewById(R.id.eventtitle);
        eventdescription = (TextView) findViewById(R.id.description);
        eventlocation = (TextView) findViewById(R.id.location);
        dateandtime = (TextView) findViewById(R.id.timeanddate);
        eventimage = (ImageView) findViewById(R.id.eventimage);
//        goBack = (Toolbar) findViewById(R.id.goback);
        gotoEvent = (Button) findViewById(R.id.going);
        prize = (TextView) findViewById(R.id.prize);
        see_all_posts = (TextView) findViewById(R.id.see_all_posts);

        seventid = intent.getStringExtra("eventid");
        simage = intent.getStringExtra("theimage");
        stitle = intent.getStringExtra("thetitle");
        sdescription = intent.getStringExtra("thedescription");
        slocation = intent.getStringExtra("thelocation");
        stheuser = intent.getStringExtra("theuser");
        srate_of_event = intent.getStringExtra("therate");
        sprize_of_event = intent.getStringExtra("theprize");
        sdate_of_event = intent.getStringExtra("thedate");
        stime_of_event = intent.getStringExtra("thetime");

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
        dateandtime.setText(sdate_of_event + " at " + stime_of_event + "GMT");

        see_all_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPostActivity.class);
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

                            Post obj = new Post(eventid,imageurl,description,location,title,user,rate,sprize,the_date,the_time);
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


}
