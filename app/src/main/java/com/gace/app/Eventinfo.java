package com.gace.app;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class Eventinfo extends FragmentActivity implements OnMapReadyCallback {

    TextView eventtitle, eventdescription, eventlocation, dateandtime, prize;
    ImageView eventimage;
    Toolbar goBack;

    ImageLoader loader = ImageLoader.getInstance();
    Button gotoEvent;
    String seventid, simage, stitle, sdescription, slocation,stheuser ,sdatetime, srate_of_event,sprize_of_event,
    sdate_of_event,stime_of_event;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

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

        eventtitle = (TextView)findViewById(R.id.eventtitle);
        eventdescription = (TextView)findViewById(R.id.description);
        eventlocation = (TextView)findViewById(R.id.location);
        dateandtime = (TextView)findViewById(R.id.timeanddate);
        eventimage = (ImageView)findViewById(R.id.eventimage);
//        goBack = (Toolbar) findViewById(R.id.goback);
        gotoEvent = (Button) findViewById(R.id.going);
        prize = (TextView) findViewById(R.id.prize);

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
        loader.displayImage(imagelink,eventimage);

        eventtitle.setText(stitle);
        eventdescription.setText(sdescription);
        eventlocation.setText(slocation);
        dateandtime.setText(sdate_of_event + " at " + stime_of_event + "GMT");


//        goBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        if(srate_of_event.equals("Free")){
            prize.setText("FREE!");
            gotoEvent.setText("Register");
            gotoEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent theintent = new Intent(Eventinfo.this,EventRegistration.class);
                    theintent.putExtra("usethis_id",seventid);
                    startActivity(theintent);
                }
            });

        }else{
            gotoEvent.setText("Ticket");
            prize.setText(sprize_of_event);
            gotoEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent theintent = new Intent(Eventinfo.this,BuyTicket.class);
                    theintent.putExtra("usethis_id",seventid);
                    theintent.putExtra("useevent_name",stitle);
                    theintent.putExtra("useeent_prize",sprize_of_event);
                    startActivity(theintent);
                }
            });
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
}
