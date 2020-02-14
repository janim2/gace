package com.gace.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class Add_Group extends AppCompatActivity {

    private FrameLayout image_frame;
    private ImageView group_image;
    private EditText group_name, group_description;
    private Button create_group;
    private String g_name, g_description, user_phonenumber;

    private final int PICK_IMAGE_ONE_REQUEST = 71;
    private Uri filePath_one;
    private DatabaseReference storage_reference;
    private ProgressDialog progressDialog;
    private Accessories addgroupaccessor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__group);

        addgroupaccessor = new Accessories(Add_Group.this);

        user_phonenumber = addgroupaccessor.getString("saved_phone");

        getSupportActionBar().setTitle("Create Group");
        image_frame = findViewById(R.id.image_frame);
        group_image = findViewById(R.id.g_image);
        group_name = findViewById(R.id.g_name);
        group_description = findViewById(R.id.g_description);
        create_group = findViewById(R.id.create_group);
        progressDialog = new ProgressDialog(Add_Group.this);

        image_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_ONE_REQUEST);

            }
        });

        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                g_name = group_name.getText().toString().trim();
                g_description = group_description.getText().toString().trim();
                if(!g_name.equals("")){
                    if(isNetworkAvailable()){
                        Add_group_to_Database(g_name,g_description);
                    }else{
                        AlertDialog.Builder no_internet = new AlertDialog.Builder(Add_Group.this);
                        no_internet.setMessage("No internet connection");
                        no_internet.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        no_internet.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Add_group_to_Database(g_name,g_description);
                            }
                        });

                        no_internet.show();
                    }
                }
            }
        });
    }

    private void Add_group_to_Database(final String g_name, final String g_description) {
//        Add image if any
        if(filePath_one != null)
        {
//                    progressDialog.setTitle("Uploading...");
//                    progressDialog.show();
            Random r = new Random();
            int d = r.nextInt(4545423);
            String image_id = d+"";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storage_reference = storage.getReference();

            StorageReference ref_1 = storage_reference.child("images/group_images/"+image_id+"/"+ UUID.randomUUID().toString());
            ref_1.putFile(filePath_one)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();

//                                            Group key
                                            Random i = new Random();
                                            int d = i.nextInt(343443434);
                                            String group_key = d+"";

                                            DatabaseReference addImages_one = FirebaseDatabase.getInstance().getReference("groups").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(group_key);
                                            addImages_one.child("image").setValue(imageUrl);
                                            addImages_one.child("name").setValue(g_name);
                                            addImages_one.child("description").setValue(g_description);
                                            addImages_one.child("isagroup").setValue("Yes");

                                            DatabaseReference add_participant = FirebaseDatabase.getInstance().getReference("group_participants")
                                                    .child(group_key).child(user_phonenumber);
                                            add_participant.child("participant").setValue("Yes");
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                                    progressDialog.dismiss();
                            AlertDialog.Builder success = new AlertDialog.Builder(Add_Group.this);
                            success.setMessage("Upload failed");
                            success.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Add_group_to_Database(g_name, g_description);
                                }
                            });
                            success.show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
//                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                    Toast.makeText(Add_Group.this,"Uploaded "+(int)progress+"%", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            AlertDialog.Builder completeAlert = new AlertDialog.Builder(Add_Group.this);
                            completeAlert.setTitle("Complete")
                                    .setMessage("Group Added")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                        }
                    });
        }else{
            progressDialog.show();

//            Group key
            Random i = new Random();
            int d = i.nextInt(343443434);
            String group_key = d+"";

            DatabaseReference addImages_one = FirebaseDatabase.getInstance()
                    .getReference("groups").child(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid()).child(group_key);

            addImages_one.child("image").setValue("");
            addImages_one.child("name").setValue(g_name);
            addImages_one.child("description").setValue(g_description);
            addImages_one.child("isagroup").setValue("Yes");


            DatabaseReference add_participant = FirebaseDatabase.getInstance().getReference("group_participants")
                    .child(group_key).child(user_phonenumber);
            add_participant.child("participant").setValue("Yes")

            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    AlertDialog.Builder success = new AlertDialog.Builder(Add_Group.this);
                    success.setTitle("Complete");
                    success.setMessage("Group Added");
                    success.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    success.show();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_ONE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath_one = data.getData();
            try {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath_one);
                    group_image.setImageBitmap(bitmap);
                }catch (OutOfMemoryError e){

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
