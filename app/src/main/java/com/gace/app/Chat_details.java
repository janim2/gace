package com.gace.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.gace.app.objects.ChatMessage;
import com.gace.app.objects.MessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Chat_details extends AppCompatActivity {

    private String group_id,group_name;
    private Intent details_intent;

    FirebaseListAdapter<ChatMessage> adapter;
    private ArrayList messagesArray = new ArrayList<ChatMessage>();
    private RecyclerView messages_RecyclerView;
    private RecyclerView.Adapter messages_mAdapter;

    private String messagess, message_text, message_time, document_name, message_user;
    private EditText message_;
    private ImageView attach,send_message;
    private CardView attachment_cardView;
    private int show_attach = 0;
    private FrameLayout chats_framelayout;
    private TextView add_images, add_documents, add_videos;
    private ProgressBar loading;
    private Accessories chat_details_accessor;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_details_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cdetails:
                Intent chat_details = new Intent(Chat_details.this, Chat_information.class);
                chat_details.putExtra("group_id",group_id);
                chat_details.putExtra("group_name",group_name);
                startActivity(chat_details);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        chat_details_accessor = new Accessories(Chat_details.this);

        details_intent = getIntent();

        group_id = chat_details_accessor.getString("group_id");
        group_name = chat_details_accessor.getString("group_name");

        getSupportActionBar().setTitle(group_name);

        messages_RecyclerView = findViewById(R.id.messages_Recyclerview);
        message_ = findViewById(R.id.message_);
        attach = findViewById(R.id.attach);
        attachment_cardView = findViewById(R.id.attachment_layout);
        send_message = findViewById(R.id.send_message);
        chats_framelayout = findViewById(R.id.chat_frame);
        add_images = findViewById(R.id.add_pictures);
        add_documents = findViewById(R.id.add_documents);
        add_videos = findViewById(R.id.add_videos);
        loading = findViewById(R.id.loading);

        //getiing the messages variable initialization starts here
        final Handler thehandler;

        thehandler = new Handler(Looper.getMainLooper());
        final int delay = 10000; //checking for a message every 10 seconds

        thehandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNetworkAvailable()){
                    getTheMessagesKeys();
                }else{
//                        Toast.makeText(Admin_MainActivity.this,"checking", Toast.LENGTH_LONG).show();
                }
                thehandler.postDelayed(this,delay);
            }
        },delay);

        try {
            messages_RecyclerView.setHasFixedSize(true);

            messages_mAdapter = new MessageAdapter(getMessagesFromDatabase(),Chat_details.this);
            messages_RecyclerView.setAdapter(messages_mAdapter);

        }catch (IndexOutOfBoundsException e){

        }

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagess = message_.getText().toString().trim();
                if(!messagess.equals("")){
                    if(isNetworkAvailable()){
                        DatabaseReference get_messsages = FirebaseDatabase.getInstance().getReference("groups");//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        FirebaseDatabase.getInstance()
                                .getReference("group_chats")
                                .child(group_id)
                                .push()
                                .setValue(new ChatMessage(messagess,new Date().getTime(),
                                        "text",FirebaseAuth.getInstance().getCurrentUser().getUid()));

                        // Clear the input
                        message_.setText("");
                        getTheMessagesKeys();
                    }else{
                        Toast.makeText(Chat_details.this, "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Chat_details.this, "Message required", Toast.LENGTH_LONG).show();

                }
            }
        });

        chats_framelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(attachment_cardView.getVisibility() == View.VISIBLE){
                    show_attach = 0;
                    attachment_cardView.setVisibility(View.GONE);
                }
            }
        });

        add_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission("android.permission.READ_EXTERNAL_STORAGE",
                        "External Storage", 20);
                galleryAction(2000);
            }
        });

        add_documents.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 1011);
            }
        });

        add_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent , 3000);
            }
        });


        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show_attach == 0){
                    show_attach = 1;
                    attachment_cardView.setVisibility(View.VISIBLE);
                }else{
                    show_attach = 0;
                    attachment_cardView.setVisibility(View.GONE);
                }
            }
        });


    }

    private void getTheMessagesKeys() {
        messagesArray.clear();
        DatabaseReference get_messsages = FirebaseDatabase.getInstance().getReference("group_chats")//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        .child(group_id);
        get_messsages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey() != null){
                            getTheMessages(child.getKey());
                        }
                    }
                }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chat_details.this,"Cancelled",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getTheMessages(String key) {
        DatabaseReference get_messsages = FirebaseDatabase.getInstance().getReference("group_chats")//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                .child(group_id)
                .child(key);
        get_messsages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("messageText")) {
                            message_text = child.getValue().toString();
                        }

                        if (child.getKey().equals("messageTime")) {
                            message_time = child.getValue().toString();
                        }

                        if (child.getKey().equals("documentName")) {
                            document_name = child.getValue().toString();
                        }

                        if (child.getKey().equals("messageUser")) {
                            message_user = child.getValue().toString();
                        }

                        else {
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }

                    ChatMessage obj = new ChatMessage(message_text,Long.valueOf(message_time),document_name,message_user);
                    messagesArray.add(obj);
                    messages_RecyclerView.setAdapter(messages_mAdapter);
                    messages_mAdapter.notifyDataSetChanged();
//                    no_chats.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chat_details.this, "Cancelled", Toast.LENGTH_LONG).show();

            }
        });
    }

    public boolean checkPermission(String permission, String msg, int MY_PERMISSIONS_REQUEST){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(Chat_details.this, permission) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)Chat_details.this, permission)){
//                    showDialog(msg, User_Profile.this, permission, MY_PERMISSIONS_REQUEST);
                    Toast.makeText(Chat_details.this,permission + "not Granted",Toast.LENGTH_LONG).show();
                }else{
                    ActivityCompat.requestPermissions((Activity)Chat_details.this, new String[]{permission},
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
        if(Chat_details.this instanceof Activity){
            ((Activity) Chat_details.this).startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }else{
            Toast.makeText(Chat_details.this, "Error: Context should be an instance of activity", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 2000 && resultCode == RESULT_OK){
                Uri uri = data.getData();
                UploadImageToChat(uri);
                attachment_cardView.setVisibility(View.GONE);
            }
            else if(requestCode == 1011 && resultCode == RESULT_OK){
                Uri uri = data.getData();
//                    File myFile = new File(uriString);
                uploadFileToChat(uri);
                attachment_cardView.setVisibility(View.GONE);
            }
            else if(requestCode == 3000 && resultCode == RESULT_OK){
                Uri uri = data.getData();
//                    File myFile = new File(uriString);
                uploadVideoToChat(uri);
                attachment_cardView.setVisibility(View.GONE);
            }
    }

    private void UploadImageToChat(Uri uri) {
        if(uri != null)
        {
//                    progressDialog.setTitle("Uploading...");
//                    progressDialog.show();
            loading.setVisibility(View.VISIBLE);
            Random r = new Random();
            int d = r.nextInt(4545423);
            String image_id = d+"";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storage_reference = storage.getReference();

            StorageReference ref_1 = storage_reference.child("images/chat_images/"+image_id+"/"+ UUID.randomUUID().toString());
            ref_1.putFile(uri)
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

                                            FirebaseDatabase.getInstance()
                                                    .getReference("group_chats")
                                                    .child(group_id)
                                                    .push()
                                                    .setValue(new ChatMessage(imageUrl,new Date().getTime(),
                                                            "image",FirebaseAuth.getInstance().getCurrentUser().getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    loading.setVisibility(View.GONE);
                                                    getTheMessagesKeys();
                                                }
                                            });

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
                            AlertDialog.Builder success = new AlertDialog.Builder(Chat_details.this);
                            success.setMessage("Upload failed");
                            success.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            success.show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            loading.setVisibility(View.GONE);
                            getTheMessagesKeys();
                        }
                    });
        }
    }

    private void uploadFileToChat(Uri myFile) {
        if(myFile != null)
        {

            loading.setVisibility(View.VISIBLE);
            Random r = new Random();
            int d = r.nextInt(4545423);
            String document_id = d+"";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storage_reference = storage.getReference();

            StorageReference ref_1 = storage_reference.child("files/documents/"+document_id+"/"+ UUID.randomUUID().toString());
            ref_1.putFile(myFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String documentUrl = uri.toString();
                                            File pdffile = new File(documentUrl);
                                            if(documentUrl.contains("content://")){
                                                Cursor cursor = null;
                                                try {
                                                    cursor = getContentResolver().query(uri, null, null, null, null);
                                                    if (cursor != null && cursor.moveToFirst()) {
                                                        String document_name  = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                                                        FirebaseDatabase.getInstance()
                                                                .getReference("group_chats")
                                                                .child(group_id)
                                                                .push()
                                                                .setValue(new ChatMessage(documentUrl,new Date().getTime(),
                                                                        document_name,FirebaseAuth.getInstance().getCurrentUser().getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                loading.setVisibility(View.GONE);
                                                                getTheMessagesKeys();
                                                            }
                                                        });
                                                    }
                                                } finally {
                                                    cursor.close();
                                                }
                                            }
                                            else if (documentUrl.startsWith("file://")) {
                                                String document_name = pdffile.getName();
                                                FirebaseDatabase.getInstance()
                                                        .getReference("group_chats")
                                                        .child(group_id)
                                                        .push()
                                                        .setValue(new ChatMessage(documentUrl,new Date().getTime(),
                                                                document_name,FirebaseAuth.getInstance().getCurrentUser().getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        loading.setVisibility(View.GONE);
                                                        getTheMessagesKeys();
                                                    }
                                                });
                                            }
                                            else{
                                                FirebaseDatabase.getInstance()
                                                        .getReference("group_chats")
                                                        .child(group_id)
                                                        .push()
                                                        .setValue(new ChatMessage(documentUrl,new Date().getTime(),
                                                                "document.pdf",FirebaseAuth.getInstance().getCurrentUser().getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        loading.setVisibility(View.GONE);
                                                        getTheMessagesKeys();
                                                    }
                                                });
                                            }
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
                            AlertDialog.Builder success = new AlertDialog.Builder(Chat_details.this);
                            success.setMessage("Upload failed");
                            success.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            success.show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            loading.setVisibility(View.GONE);
                            getTheMessagesKeys();
                        }
                    });
        }
    }

    private void uploadVideoToChat(Uri uri) {
        if(uri != null)
        {
//                    progressDialog.setTitle("Uploading...");
//                    progressDialog.show();
            loading.setVisibility(View.VISIBLE);
            Random r = new Random();
            int d = r.nextInt(4545423);
            String video_id = d+"";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storage_reference = storage.getReference();

            StorageReference ref_1 = storage_reference.child("videos/chat_videos/"+video_id+"/"+ UUID.randomUUID().toString());
            ref_1.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String videoUrl = uri.toString();

                                            FirebaseDatabase.getInstance()
                                                    .getReference("group_chats")
                                                    .child(group_id)
                                                    .push()
                                                    .setValue(new ChatMessage(videoUrl,new Date().getTime(),
                                                            "video",FirebaseAuth.getInstance().getCurrentUser().getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    loading.setVisibility(View.GONE);
                                                    getTheMessagesKeys();
                                                }
                                            });

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
                            AlertDialog.Builder success = new AlertDialog.Builder(Chat_details.this);
                            success.setMessage("Upload failed");
                            success.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            success.show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            loading.setVisibility(View.GONE);
                            getTheMessagesKeys();
                        }
                    });
        }
    }

    public ArrayList<ChatMessage> getMessagesFromDatabase(){
        return  messagesArray;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
