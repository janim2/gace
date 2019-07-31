package com.gace.app.objects;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gace.app.Eventinfo;
import com.gace.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


public class RelatedPostAdapter extends RecyclerView.Adapter<RelatedPostAdapter.ViewHolder>{
    ArrayList<Post> itemList;
    Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public RelatedPostAdapter(ArrayList<Post> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public RelatedPostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_item_event,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(RelatedPostAdapter.ViewHolder holder, final int position) {
        final TextView title = holder.view.findViewById(R.id.title);
        ImageView image = holder.view.findViewById(R.id.image);
//        TextView description = holder.view.findViewById(R.id.discription);
//        TextView location = holder.view.findViewById(R.id.location);

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
        String imagelink = itemList.get(position).getImage();
        imageLoader.displayImage(imagelink,image);

        title.setText(itemList.get(position).getTitle());
//        description.setText(itemList.get(position).getUser());
//        location.setText(itemList.get(position).getLocation());


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Eventinfo.class);
                intent.putExtra("eventid",itemList.get(position).getEventid());
                intent.putExtra("theimage",itemList.get(position).getImage());
                intent.putExtra("thetitle",itemList.get(position).getTitle());
                intent.putExtra("thedescription",itemList.get(position).getDescription());
                intent.putExtra("thelocation",itemList.get(position).getLocation());
                intent.putExtra("theuser",itemList.get(position).getUser());
                intent.putExtra("therate",itemList.get(position).getRate());
                intent.putExtra("theprize",itemList.get(position).getPrize());
                intent.putExtra("thedate",itemList.get(position).getDate());
                intent.putExtra("thetime",itemList.get(position).getTime());
                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }
}