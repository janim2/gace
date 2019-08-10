package com.gace.app.objects;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gace.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    ArrayList<Reviews> itemList;
    Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ReviewAdapter(ArrayList<Reviews> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, final int position) {
        final TextView review_titled = holder.view.findViewById(R.id.rate_title);
        final TextView review_message = holder.view.findViewById(R.id.rate_message);
        final TextView bywho = holder.view.findViewById(R.id.rate_byWho);

        review_titled.setText(itemList.get(position).getTitle());
        review_message.setText(itemList.get(position).getMessage());
        bywho.setText("by " + itemList.get(position).getName());

//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ItemDetailsActivity.class);
//                intent.putExtra("partid",itemList.get(position).getPartId());
//                intent.putExtra("theimage",itemList.get(position).getImage());
//                intent.putExtra("thename",itemList.get(position).getname());
//                intent.putExtra("theprice",itemList.get(position).getPrice());
//                intent.putExtra("thedescription",itemList.get(position).getDescription());
//                intent.putExtra("thesellersNumber",itemList.get(position).getsellersNumber());
//                intent.putExtra("therating",itemList.get(position).getProduct_rating());
//                v.getContext().startActivity(intent);
//            }
//        });


    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }
}