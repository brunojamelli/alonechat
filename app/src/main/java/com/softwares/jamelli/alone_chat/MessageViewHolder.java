package com.softwares.jamelli.alone_chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MessageViewHolder extends RecyclerView.ViewHolder{
    final ImageView photoImageView;
    final TextView messageTextView;
    final TextView authorTextView;
    final TextView dataTv;
    public MessageViewHolder(View v) {
        super(v);
        photoImageView = v.findViewById(R.id.photoImageView);
        messageTextView = v.findViewById(R.id.messageTextView);
        authorTextView = v.findViewById(R.id.nameTextView);
        dataTv = v.findViewById(R.id.dataTextView);

    }
}
