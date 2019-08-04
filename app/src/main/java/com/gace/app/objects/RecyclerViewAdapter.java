package com.gace.app.objects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gace.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    List<Post> list;

    public RecyclerViewAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_row,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Post post = list.get(i);
        viewHolder.title.setText(post.getTitle());
        viewHolder.date.setText(post.getDate());
//        Picasso.with(context).load(post.getImage()).into(viewHolder.image);
        Picasso.get().load(post.getImage()).into(viewHolder.image);

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title,date;
        public ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.myTitle);
            image = (ImageView)itemView.findViewById(R.id.myImage);
            date = (TextView)itemView.findViewById(R.id.myDate);
        }
    }
}
