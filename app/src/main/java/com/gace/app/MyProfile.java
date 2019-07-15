package com.gace.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;

public class MyProfile extends AppCompatActivity {

    TextView usernameTView, emailTView,text_status;
    EditText old_passwordeText, newPassword;
    FirebaseUser user;
    String semail, susername, old_password, new_Password,profileimageString;

    ImageView editimage,profileimage;
    CardView imageCardView;

    Button submitButton;
    ProgressBar loading;

    ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setTitle("Profile");

        usernameTView = (TextView)findViewById(R.id.username);
        emailTView = (TextView)findViewById(R.id.email);
        old_passwordeText = (EditText)findViewById(R.id.old_password_e_text);
        newPassword = (EditText)findViewById(R.id.new_password_e_text);
        editimage = (ImageView)findViewById(R.id.edit);
        submitButton = (Button) findViewById(R.id.submit_button);
        profileimage = (ImageView) findViewById(R.id.profile_image);
        imageCardView = (CardView) findViewById(R.id.image_cardView);
        loading = (ProgressBar) findViewById(R.id.loading);
        text_status = (TextView) findViewById(R.id.edit_status);

        //loading image if it exists

        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReferenceFromUrl("gs://ecom-e5158.appspot.com/");

            DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                    cacheOnDisk(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).
                    defaultDisplayImageOptions(theImageOptions).build();
            ImageLoader.getInstance().init(config);
//
            String profileimagelink = profileimageString;
                    //reference.child("/profile_images/"+user.getUid()+".jpg").getDownloadUrl().toString();
            imageLoader.displayImage(profileimagelink,profileimage);
        }catch (NullPointerException e){

        }


        imageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission("android.permission.READ_EXTERNAL_STORAGE",
                        "External Storage", 20);
                galleryAction(2000);            }
        });


        getuserinfo();

        editimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameTView.setVisibility(View.GONE);
                emailTView.setVisibility(View.GONE);

                old_passwordeText.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null) {
                    DatabaseReference edit = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                    old_password = old_passwordeText.getText().toString().trim();
                    new_Password = newPassword.getText().toString().trim();

                    loading.setVisibility(View.VISIBLE);
                    if(!old_password.equals("")){
                        if(!new_Password.equals("")){
                            changePassword(old_password,new_Password);

                            usernameTView.setVisibility(View.VISIBLE);
                            emailTView.setVisibility(View.VISIBLE);

                            old_passwordeText.setVisibility(View.GONE);
                            newPassword.setVisibility(View.GONE);
                            submitButton.setVisibility(View.GONE);
                        }
                    }
                    uploadFile(getBitmap(profileimage));
                    edit.child("profileimage").setValue(user.getUid()+".jpg");
                }
            }
        });
    }

    private void changePassword(String oldpass, final String newPass) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),oldpass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                loading.setVisibility(View.GONE);
                                text_status.setVisibility(View.VISIBLE);
                                text_status.setTextColor(getResources().getColor(R.color.red));
                                text_status.setText("Something went wrong. Please try again later");
                            }else {
                                loading.setVisibility(View.GONE);
                                text_status.setVisibility(View.VISIBLE);
                                text_status.setText("Password Successfully Modified");
                            }
                        }
                    });
                }else{
                    loading.setVisibility(View.GONE);
                    text_status.setVisibility(View.VISIBLE);
                    text_status.setTextColor(getResources().getColor(R.color.red));
                    text_status.setText("Authentication Failed. Old password incorrect");
                }

            }
        });
    }

    private void getuserinfo() {

        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("email")){
                            semail = child.getValue().toString();
                        }
                        if(child.getKey().equals("username")){
                            susername = child.getValue().toString();
                        }
                        try {
                            if(child.getKey().equals("profileimage")){
                                profileimageString = child.getValue().toString();
                            }
                        }catch (NullPointerException e){

                        }
                    }
                    emailTView.setText(semail);
                    usernameTView.setText(susername);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }catch (NullPointerException e){

        }

    }

    public boolean checkPermission(String permission, String msg, int MY_PERMISSIONS_REQUEST){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(MyProfile.this, permission) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)MyProfile.this, permission)){
//                    showDialog(msg, User_Profile.this, permission, MY_PERMISSIONS_REQUEST);
                    Toast.makeText(MyProfile.this,permission + "not Granted",Toast.LENGTH_LONG).show();
                }else{
                    ActivityCompat.requestPermissions((Activity)MyProfile.this, new String[]{permission},
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
            profileimage.setImageURI(uri);
        }
    }

    public void galleryAction(final int RESULT_LOAD_IMAGE){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(MyProfile.this instanceof Activity){
            ((Activity) MyProfile.this).startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }else{
            Toast.makeText(MyProfile.this, "Error: Context should be an instance of activity", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://ecom-e5158.appspot.com/");
        StorageReference profileImagesRef = storageRef.child("profile_images/" + user.getUid() + ".jpg");
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

}

