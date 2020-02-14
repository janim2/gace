package com.gace.app.objects;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gace.app.Accessories;
import com.gace.app.Chat_details;
import com.gace.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ViewHolder>{
    ArrayList<Chat_group> itemList;
    Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ChatGroupAdapter(ArrayList<Chat_group> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ChatGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ChatGroupAdapter.ViewHolder holder, final int position) {
        final CardView group_cardView = holder.view.findViewById(R.id.group_cardView);
        final ImageView group_image = holder.view.findViewById(R.id.group_image);
        final TextView group_name = holder.view.findViewById(R.id.group_name);
        final Accessories adapter_ = new Accessories(context);

        DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).
                defaultDisplayImageOptions(theImageOptions).build();

        ImageLoader.getInstance().init(config);
        imageLoader.displayImage(itemList.get(position).getImage(), group_image);
        group_name.setText(itemList.get(position).getGroup_name());

        group_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent group_intent = new Intent(v.getContext(), Chat_details.class);
                adapter_.put("group_id", itemList.get(position).getKey());
                adapter_.put("group_name", itemList.get(position).getGroup_name());
                adapter_.put("isagroup", itemList.get(position).getIsagroup());
                v.getContext().startActivity(group_intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }
}