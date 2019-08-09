package com.gace.app.objects;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gace.app.Accessories;
import com.gace.app.Eventinfo;
import com.gace.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    ArrayList<Post> itemList;
    Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Accessories accessories;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public PostAdapter(ArrayList<Post> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, final int position) {
        final TextView title = holder.view.findViewById(R.id.title);
        ImageView image = holder.view.findViewById(R.id.image);
        TextView description = holder.view.findViewById(R.id.discription);
        TextView location = holder.view.findViewById(R.id.location);
        TextView event_tag = holder.view.findViewById(R.id.event_tag);

//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        storageReference.child("images/").child(itemList.get(position).getImage());
//
//        String imageurl  = storageReference.getDownloadUrl().toString();

        //prep work before image is loaded is to load it into the cache
        DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).
                defaultDisplayImageOptions(theImageOptions).build();
        ImageLoader.getInstance().init(config);
//
        if(itemList.get(position).getRate().equals("Free")){
            event_tag.setVisibility(View.GONE);
        }

        String imagelink = itemList.get(position).getImage();
        imageLoader.displayImage(imagelink,image);

        title.setText(itemList.get(position).getTitle());
        description.setText(itemList.get(position).getUser());
        location.setText(itemList.get(position).getLocation());


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessories = new Accessories(v.getContext());

                Intent intent = new Intent(v.getContext(), Eventinfo.class);
                accessories.put("eventid",itemList.get(position).getEventid());
                accessories.put("theimage",itemList.get(position).getImage());
                accessories.put("thetitle",itemList.get(position).getTitle());
                accessories.put("thelikes",itemList.get(position).getLikes());
                accessories.put("thedescription",itemList.get(position).getDescription());
                accessories.put("thelocation",itemList.get(position).getLocation());
                accessories.put("theuser",itemList.get(position).getUser());
                accessories.put("therate",itemList.get(position).getRate());
                accessories.put("theprize",itemList.get(position).getPrize());
                accessories.put("thedate",itemList.get(position).getDate());
                accessories.put("thetime",itemList.get(position).getTime());
                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }
}