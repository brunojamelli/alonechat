package com.softwares.jamelli.alone_chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.softwares.jamelli.alone_chat.model.FriendlyMessage;

import java.util.List;

public class MessageAdapterN extends RecyclerView.Adapter{
    Context context;
    List<FriendlyMessage> mensages;
    public MessageAdapterN(Context c, List<FriendlyMessage> m){
        this.context = c;
        this.mensages = m;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_message, parent, false);
        MessageViewHolder holder = new MessageViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder mholder = (MessageViewHolder) holder;
        FriendlyMessage mchoise = mensages.get(position);
        boolean isPhoto = mchoise.getPhotoUrl() != null;
        if (isPhoto) {
            mholder.messageTextView.setVisibility(View.GONE);
            mholder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(mholder.photoImageView.getContext())
                    .load(mchoise.getPhotoUrl())
                    .into(mholder.photoImageView);
        }else {
            mholder.messageTextView.setVisibility(View.VISIBLE);
            mholder.photoImageView.setVisibility(View.GONE);
            mholder.messageTextView.setText(mchoise.getText());
            mholder.dataTv.setText(String.valueOf(mchoise.getData_envio()));
        }
        mholder.authorTextView.setText(mchoise.getName());


    }

    @Override
    public int getItemCount() {
        return mensages == null ? 0 : mensages.size();
    }
}
