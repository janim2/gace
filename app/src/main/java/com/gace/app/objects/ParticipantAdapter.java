package com.gace.app.objects;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.gace.app.Accessories;
import com.gace.app.Chat_details;
import com.gace.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Random;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder>{
    ArrayList<Chat_participants> itemList;
    Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ParticipantAdapter(ArrayList<Chat_participants> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ParticipantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_participant_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ParticipantAdapter.ViewHolder holder, final int position) {
        final CardView participant_card = holder.view.findViewById(R.id.participant_card);
        final TextView isadmin_indication = holder.view.findViewById(R.id.admin_indicator);
        final ImageView participant_image = holder.view.findViewById(R.id.participant_image);
        final TextView participant_name = holder.view.findViewById(R.id.participant_name);

        if(!itemList.get(position).getParticipant_image().equals("")){
            DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                    cacheOnDisk(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext()).
                    defaultDisplayImageOptions(theImageOptions).build();
            ImageLoader.getInstance().init(config);

            imageLoader.displayImage(itemList.get(position).getParticipant_image(), participant_image);
        }else{
            participant_image.setImageDrawable(context.getResources().getDrawable(R.drawable.newlogo));
        }

        final Accessories adapter_accessor = new Accessories(context);

        if(itemList.get(position).getParticipant_number().equals(adapter_accessor.getString("saved_phone"))){
            isadmin_indication.setVisibility(View.VISIBLE);
            participant_name.setText("You");
        }else {
            participant_name.setText(itemList.get(position).getParticipant_name());
        }
        participant_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemList.get(position).getParticipant_number().equals(adapter_accessor.getString("saved_phone"))){
                    final PopupMenu popupMenu = new PopupMenu(v.getContext(),participant_card);
                    popupMenu.inflate(R.menu.participant_menu);

                    Menu item_ = popupMenu.getMenu();
                    item_.getItem(0).setTitle("Message " + itemList.get(position).getParticipant_name());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.message_:
                                    if(isNetworkAvailable()) {
//                                      parameters needed here include userphone number and potential person to chat phone number
                                        CreateChat(adapter_accessor,itemList.get(position).getParticipant_number(), itemList.get(position).getParticipant_name(), adapter_accessor.getString("saved_phone"));
                                    }else{
                                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            }
        });


    }

    private void CreateChat(final Accessories adapteraccessor, String potential_participant_number, final String potential_chatter_name, String user_number) {
//        chat key
        Random i = new Random();
        int d = i.nextInt(343443434);
        final String group_key = d+"";

        DatabaseReference add_chat = FirebaseDatabase.getInstance().getReference("groups")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(group_key);
        add_chat.child("image").setValue("");
        add_chat.child("name").setValue(potential_chatter_name);
        add_chat.child("description").setValue("");
        add_chat.child("isagroup").setValue("No");


        DatabaseReference add_participant = FirebaseDatabase.getInstance().getReference("group_participants")
                .child(group_key).child(potential_participant_number);

        final DatabaseReference add_participant2 = FirebaseDatabase.getInstance().getReference("group_participants")
                .child(group_key).child(user_number);

        add_participant.child("participant").setValue("Yes").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                add_participant2.child("participant").setValue("Yes");
                Intent openchat = new Intent(context, Chat_details.class);
                adapteraccessor.put("group_id", group_key);
                adapteraccessor.put("group_name", potential_chatter_name);
                context.startActivity(openchat);
            }
        });

    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}