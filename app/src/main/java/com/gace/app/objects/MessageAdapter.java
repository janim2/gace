package com.gace.app.objects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gace.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    ArrayList<ChatMessage> itemList;
    Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public MessageAdapter(ArrayList<ChatMessage> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat_message, parent, false);
            return new ViewHolder(view);
        }
        else if (viewType == 2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.their_chat_message, parent, false);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }

//        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat_message, parent, false);
//        ViewHolder vh = new ViewHolder(layoutView);
//        return vh;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 1:
                initLayoutOne((ViewHolder)holder, position);
                break;
            case 2:
                initLayoutTwo((ViewHolder) holder, position);
                break;
            default:
                break;
        }
//        // Get references to the views of message.xml
//        TextView messageText = holder.view.findViewById(R.id.message_text);
//        TextView messageUser = holder.view.findViewById(R.id.message_user);
//        TextView messageTime = holder.view.findViewById(R.id.message_time);
//
//        // Set their text
//        messageText.setText(itemList.get(position).getMessageText());
////        messageUser.setText(itemList.get(position).getMessageUser());
//
//        // Format the date before showing it
//        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//                itemList.get(position).getMessageTime()));

    }

    private void initLayoutOne(ViewHolder holder, int pos) {

        // Get references to the views of message.xml
        TextView messageText = holder.view.findViewById(R.id.message_text);
        TextView messageUser = holder.view.findViewById(R.id.message_user);
        TextView messageTime = holder.view.findViewById(R.id.message_time);

        // Set their text
        messageText.setText(itemList.get(pos).getMessageText());
//        messageUser.setText(itemList.get(position).getMessageUser());

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                itemList.get(pos).getMessageTime()));
    }

    private void initLayoutTwo(ViewHolder holder, int pos) {

        // Get references to the views of message.xml
        TextView messageText = holder.view.findViewById(R.id.message_text);
        TextView messageUser = holder.view.findViewById(R.id.message_user);
        TextView messageTime = holder.view.findViewById(R.id.message_time);

        // Set their text
        messageText.setText(itemList.get(pos).getMessageText());
        messageUser.setText("Auto Dokta");

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                itemList.get(pos).getMessageTime()));
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = itemList.get(position);
        if (item.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return 1;
        }
//        else if (item.getMessageUser().equals("customerCare")) {
//            return 2;
//        }
        else {
            return 2;
        }
    }

//    // Static inner class to initialize the views of rows
//    static class ViewHolderOne extends RecyclerView.ViewHolder {
//        public TextView item;
//        public ViewHolderOne(View itemView) {
//            super(itemView);
////            item = (TextView) itemView.findViewById(R.id.row_item);
//        }
//    }
//
//    static class ViewHolderTwo extends RecyclerView.ViewHolder {
//        public TextView tvLeft, tvRight;
//        public ViewHolderTwo(View itemView) {
//            super(itemView);
////            tvLeft = (TextView) itemView.findViewById(R.id.row_item_left);
////            tvRight = (TextView) itemView.findViewById(R.id.row_item_right);
//        }
//    }
}