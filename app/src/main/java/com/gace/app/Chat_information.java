package com.gace.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gace.app.objects.ChatMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Chat_information extends AppCompatActivity {

    private String group_id, group_image_string ,chat_name, group_descripion_string;
    private Intent chat_info;
    private TextView group_name, group_description;
    private ImageView group_image;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CardView addParticipantCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_information);
        chat_info = getIntent();

        group_id = chat_info.getStringExtra("group_id");
        chat_name = chat_info.getStringExtra("group_name");

//        getSupportActionBar().setTitle(chat_name);

        group_image = findViewById(R.id.group_image);
        group_name = findViewById(R.id.group_name);
        group_description = findViewById(R.id.group_description);
        addParticipantCard = findViewById(R.id.add_participant_card);

        group_name.setText(chat_name);

        if(isNetworkAvailable()){
            FetchGroupInformation_ID();
        }else{
            Toast.makeText(Chat_information.this, "No internet connection", Toast.LENGTH_LONG).show();
        }


        addParticipantCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });

    }

    private void FetchGroupInformation_ID() {
        try{
            DatabaseReference fetch_groups_ID = FirebaseDatabase.getInstance().getReference("groups");//.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            fetch_groups_ID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            FetchGroupInformation(child.getKey());
                        }
                    }else{
//                        Toast.makeText(ChatGroups.this,"Doesnt",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Chat_information.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });

        }catch (NullPointerException e){

        }
    }

    private void FetchGroupInformation(String key) {
        try{
            DatabaseReference retrieve = FirebaseDatabase.getInstance().getReference("groups")
                    .child(key).child(group_id);
            retrieve.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getKey().equals("image")) {
                                group_image_string = child.getValue().toString();
                            }

                            if (child.getKey().equals("description")) {
                                group_descripion_string = child.getValue().toString();
                            }

                            else {
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                            }
                        }
                         //set image to imageview here
                        try {
                            DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                                    cacheOnDisk(true).build();
                            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).
                                    defaultDisplayImageOptions(theImageOptions).build();
                            ImageLoader.getInstance().init(config);
//
                            imageLoader.displayImage(group_image_string,group_image);
                        }catch (NullPointerException e){

                        }
                        group_description.setText(group_descripion_string);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Chat_information.this, "Cancelled", Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){
         e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
//                Toast.makeText(Chat_information.this, name + " : " + number, Toast.LENGTH_LONG).show();
                Addparticipant(number.replace("+233","0"));
        }
    }

    private void Addparticipant(String number) {
        try {
            if(isNetworkAvailable()){
                DatabaseReference addparticipant = FirebaseDatabase.getInstance()
                        .getReference("group_participants").child(group_id)
                        .child(number);
                addparticipant.child("participant").setValue("Yes")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Chat_information.this, "Participant added", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
            }
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