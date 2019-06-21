package com.gace.app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gace.app.objects.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView description;
    TextView location;
    ImageView image;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        this.title = itemView.findViewById(R.id.title);
        this.description = itemView.findViewById(R.id.discription);
        this.location = itemView.findViewById(R.id.location);
//        this.image = itemView.findViewById(R.id.myImage);
    }

    public void bindToPost(Post post) {
        title.setText(post.title);
        description.setText(post.description);
        location.setText(post.location);
    }
}
