package com.gace.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Calendar;

public class UploadEvent extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    FrameLayout imageframe_layout;
    EditText event_title,event_organizers,event_location,event_description,event_prize;
    Button addevent_button;
    String se_title, se_organizers, se_location, se_description, srate_of_event="",sevent_prize = "GHC 0.00",sevent_date,sevent_time;
    TextView success,event_date, event_time;
    ImageView theeventposter;
    Spinner eventrate;
    String[] rate_list = {"Free","Paid"};

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_event);
        getSupportActionBar().setTitle("NitchApp");

        imageframe_layout = (FrameLayout) findViewById(R.id.imageframe_layout);
        event_title = (EditText) findViewById(R.id.event_title);
        event_organizers = (EditText) findViewById(R.id.event_organizers);
        event_location = (EditText) findViewById(R.id.event_location);
        event_description = (EditText) findViewById(R.id.event_description);
        addevent_button = (Button) findViewById(R.id.addevent_button);
        success = (TextView) findViewById(R.id.successful);
        theeventposter = (ImageView) findViewById(R.id.event_poster);
        eventrate = (Spinner) findViewById(R.id.event_rate);
        event_prize = (EditText) findViewById(R.id.event_prize);
        event_date = (TextView) findViewById(R.id.event_date);
        event_time = (TextView) findViewById(R.id.event_time);

        eventrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                srate_of_event = rate_list[position];
                if(srate_of_event.equals("Paid")){
                    event_prize.setVisibility(View.VISIBLE);
                }else{
                    event_prize.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                srate_of_event = "Free";
            }
        });
        ArrayAdapter<String> rate_of_event = new ArrayAdapter<String>(UploadEvent.this,android.R.layout.simple_list_item_1,rate_list);
        eventrate.setAdapter(rate_of_event);



        imageframe_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission("android.permission.READ_EXTERNAL_STORAGE",
                        "External Storage", 20);
                galleryAction(2000);
            }
        });

        //setting on click for the date and time edittexts

        event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView[] the_event_date = {event_date};
                showDatePicker(null,the_event_date);
            }
        });

        event_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView[] the_event_time = {event_time};
                showTimePicker(null,the_event_time);
            }
        });

        addevent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                se_title = event_title.getText().toString().trim();
                se_organizers = event_organizers.getText().toString().trim();
                se_location = event_location.getText().toString().trim();
                se_description = event_description.getText().toString().trim();
                sevent_prize = event_prize.getText().toString().trim();
                sevent_date = event_date.getText().toString().trim();
                sevent_time = event_time.getText().toString().trim();

                if(!se_title.equals("")){
                    if(!se_organizers.equals("")){
                        if(!se_location.equals("")){
                            if(!se_description.equals("")){
                                if(!sevent_date.equals("Select Date")){
                                    if(!sevent_time.equals("Select Time")) {
                                        if (addtoevents()) {
                                            hideProgressDialog();
                                            success.setVisibility(View.VISIBLE);
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }else{
                                            success.setText("Upload Failed");
                                            success.setTextColor(getResources().getColor(R.color.red));
                                            success.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        event_time.setError("Required");
                                    }
                                }else{
                                    event_date.setError("Required");
                                }
                            }else{
                                event_description.setError("Required");
                            }
                        }else{
                            event_location.setError("Required");
                        }
                    }else{
                        event_organizers.setError("Required");
                    }
                }else{
                    event_title.setError("Required");
                }
            }
        });
    }

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    private boolean addtoevents() {
        showProgressDialog();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            if(user!=null){
                String therandromString = randomString(20);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("post").child(therandromString);
                uploadFile(getBitmap(theeventposter),therandromString);
                reference.child("description").setValue(se_description);
                reference.child("image").setValue(therandromString+".jpg");
                reference.child("location").setValue(se_location);
                reference.child("title").setValue(se_title);
                reference.child("user").setValue(se_organizers);
                reference.child("rate").setValue(srate_of_event);
                reference.child("prize").setValue(sevent_prize);
                reference.child("date").setValue(sevent_date);
                reference.child("time").setValue(sevent_time);
                reference.child("likes").setValue("0");
            }
        }catch (NullPointerException e){

        }
        return true;
    }

    public boolean checkPermission(String permission, String msg, int MY_PERMISSIONS_REQUEST){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(UploadEvent.this, permission) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)UploadEvent.this, permission)){
//                    showDialog(msg, User_Profile.this, permission, MY_PERMISSIONS_REQUEST);
                    Toast.makeText(UploadEvent.this,permission + "not Granted",Toast.LENGTH_LONG).show();
                }else{
                    ActivityCompat.requestPermissions((Activity)UploadEvent.this, new String[]{permission},
                            MY_PERMISSIONS_REQUEST);
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }

    public void galleryAction(final int RESULT_LOAD_IMAGE){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(UploadEvent.this instanceof Activity){
            ((Activity) UploadEvent.this).startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }else{
            Toast.makeText(UploadEvent.this, "Error: Context should be an instance of activity", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFile(Bitmap bitmap, String imagenameString) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://ecom-e5158.appspot.com/");
        StorageReference profileImagesRef = storageRef.child("images/" + imagenameString + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profileImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
//                sendMsg("" + downloadUrl, 2);
                Log.d("downloadUrl-->", "" + downloadUrl);
            }
        });

    }

    public Bitmap getBitmap(ImageView theimageview){
        try{
            Bitmap encodingImage = Bitmap.createBitmap(theimageview.getDrawable().getIntrinsicWidth(),theimageview.getDrawable().getIntrinsicHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(encodingImage);
            theimageview.getDrawable().setBounds(0,0,canvas.getWidth(), canvas.getHeight());
            theimageview.getDrawable().draw(canvas);
            return encodingImage;
        }

        catch (OutOfMemoryError e){
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri uri = data.getData();
            theeventposter.setImageURI(uri);
        }
    }

    //Method to call DatePicker
    EditText[] editText;
    TextView[] textView;
    public void showDatePicker(EditText[] editText, TextView[] textView){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        this.editText = editText;
        this.textView = textView;

        DatePickerDialog datePickerDialog = new DatePickerDialog(UploadEvent.this, this, year, month, day);
        datePickerDialog.show();
    }

    public void showTimePicker(EditText[] editText, TextView[] textView){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int second = calendar.get(Calendar.SECOND);
        int minute = calendar.get(Calendar.MINUTE);
        this.editText = editText;
        this.textView = textView;

        TimePickerDialog timePickerDialog = new TimePickerDialog(UploadEvent.this, this, hour, minute, false);
        timePickerDialog.show();
    }

    int yr, mt, dy, hr, min;
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yr = year;
        mt = month + 1;
        dy = dayOfMonth;
        if(editText != null){
            for (int i = 0; i < editText.length; i++){
                editText[i].setText(yr + "-" + mt + "-" + dy);
            }
        }
        if(textView != null){
            for (int i = 0; i < textView.length; i++){
                textView[i].setText(yr + "-" + mt + "-" + dy);
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hr = hourOfDay;
        min = minute;
        if(editText != null){
            for (int i = 0; i < editText.length; i++){
                editText[i].setText(hr+":"+min);
            }
        }
        if(textView != null){
            for (int i = 0; i < textView.length; i++){
                textView[i].setText(hr+":"+min);
            }
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
