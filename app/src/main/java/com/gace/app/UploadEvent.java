package com.gace.app;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
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

public class UploadEvent extends BaseActivity {

    FrameLayout imageframe_layout;
    EditText event_title,event_organizers,event_location,event_description;
    Button addevent_button;
    String se_title, se_organizers, se_location, se_description;
    TextView success;
    ImageView theeventposter;

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

        imageframe_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission("android.permission.READ_EXTERNAL_STORAGE",
                        "External Storage", 20);
                galleryAction(2000);
            }
        });

        addevent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                se_title = event_title.getText().toString().trim();
                se_organizers = event_organizers.getText().toString().trim();
                se_location = event_location.getText().toString().trim();
                se_description = event_description.getText().toString().trim();

                if(!se_title.equals("")){
                    if(!se_organizers.equals("")){
                        if(!se_location.equals("")){
                            if(!se_description.equals("")){
                                if(addtoevents()){
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
}
