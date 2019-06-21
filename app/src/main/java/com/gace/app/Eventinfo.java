package com.gace.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Eventinfo extends AppCompatActivity {

    TextView eventtitle, eventdescription, eventlocation, dateandtime;
    ImageView eventimage;
    Toolbar goBack;

    ImageLoader loader = ImageLoader.getInstance();
    Button gotoEvent;
    String seventid, simage, stitle, sdescription, slocation, sdatetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventinfo);
        Intent intent = getIntent();


        eventtitle = (TextView)findViewById(R.id.eventtitle);
        eventdescription = (TextView)findViewById(R.id.description);
        eventlocation = (TextView)findViewById(R.id.location);
        dateandtime = (TextView)findViewById(R.id.timeanddate);
        eventimage = (ImageView)findViewById(R.id.eventimage);
        goBack = (Toolbar) findViewById(R.id.goback);
        gotoEvent = (Button) findViewById(R.id.going);

        seventid = intent.getStringExtra("eventid");
        simage = intent.getStringExtra("theimage");
        stitle = intent.getStringExtra("thetitle");
        sdescription = intent.getStringExtra("thedescription");
        slocation = intent.getStringExtra("thelocation");
        sdatetime = intent.getStringExtra("thedateandtime");

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
        dateandtime.setText(sdatetime);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gotoEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent theintent = new Intent(Eventinfo.this,EventRegistration.class);
                theintent.putExtra("usethis_id",seventid);
                startActivity(theintent);
            }
        });


    }
}
