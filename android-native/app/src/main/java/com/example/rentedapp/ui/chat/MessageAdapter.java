package com.example.rentedapp.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messages;
    private String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (currentUserId != null && currentUserId.equals(message.getSenderId())) {
            return VIEW_TYPE_SENT;
        }
        return VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private String formatTime(String isoTime) {
        if (isoTime == null || isoTime.length() < 16) return "";
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = parser.parse(isoTime);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(date);
        } catch (Exception e) {
            try {
                int tIdx = isoTime.indexOf('T');
                if (tIdx != -1 && isoTime.length() >= tIdx + 6) {
                    return isoTime.substring(tIdx + 1, tIdx + 6);
                }
            } catch (Exception ex) {}
            return "";
        }
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageContent, tvMessageTime;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
        }

        void bind(Message message) {
            tvMessageContent.setText(message.getContent());
            tvMessageTime.setText(formatTime(message.getCreatedAt()));
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSenderAvatar;
        TextView tvMessageContent, tvMessageTime;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            ivSenderAvatar = itemView.findViewById(R.id.ivSenderAvatar);
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
        }

        void bind(Message message) {
            tvMessageContent.setText(message.getContent());
            tvMessageTime.setText(formatTime(message.getCreatedAt()));

            String avatarUrl = message.getAbsoluteSenderAvatarUrl();
            if (avatarUrl != null) {
                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .error(android.R.drawable.sym_def_app_icon)
                        .into(ivSenderAvatar);
            } else {
                ivSenderAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }
}
