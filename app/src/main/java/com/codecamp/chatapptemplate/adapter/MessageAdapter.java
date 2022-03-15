package com.codecamp.chatapptemplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codecamp.chatapptemplate.R;
import com.codecamp.chatapptemplate.model.Messages;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import static com.codecamp.chatapptemplate.ChatPageActivity.p_rImage;
import static com.codecamp.chatapptemplate.ChatPageActivity.p_sImage;


public class MessageAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECIVE = 2;

    public MessageAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item, parent, false);
            return new MessageAdapter.SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item, parent, false);
            return new MessageAdapter.ReciverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesArrayList.get(position);

        if (holder.getClass() == MessageAdapter.SenderViewHolder.class) {
            MessageAdapter.SenderViewHolder viewHolder = (MessageAdapter.SenderViewHolder) holder;
            viewHolder.txtmessage.setText(messages.getMessage());
            Picasso.get().load(p_sImage).into(viewHolder.circleImageView);

        } else {
            MessageAdapter.ReciverViewHolder viewHolder = (MessageAdapter.ReciverViewHolder) holder;
            viewHolder.txtmessage.setText(messages.getMessage());
            Picasso.get().load(p_rImage).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }
    class SenderViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtmessage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profile_image_msg);
            txtmessage = itemView.findViewById(R.id.txtMessages);

        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtmessage;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profile_image_msg);
            txtmessage = itemView.findViewById(R.id.txtMessages);

        }
    }
}
