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
import com.example.rentedapp.data.model.Conversation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private List<Conversation> conversations;
    private String currentUserId;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Conversation conversation);
    }

    public ConversationAdapter(List<Conversation> conversations, String currentUserId, OnItemClickListener listener) {
        this.conversations = conversations;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation, currentUserId, listener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvTime, tvRoomTitle, tvLastMessage, tvUnreadBadge;

        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRoomTitle = itemView.findViewById(R.id.tvRoomTitle);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvUnreadBadge = itemView.findViewById(R.id.tvUnreadBadge);
        }

        void bind(Conversation conversation, String currentUserId, OnItemClickListener listener) {
            String partnerName = conversation.getPartnerName(currentUserId);
            tvName.setText(partnerName != null ? partnerName : "Người dùng");
            
            if (conversation.getRoomTitle() != null && !conversation.getRoomTitle().isEmpty()) {
                tvRoomTitle.setText("Phòng: " + conversation.getRoomTitle());
                tvRoomTitle.setVisibility(View.VISIBLE);
            } else {
                tvRoomTitle.setVisibility(View.GONE);
            }

            tvLastMessage.setText(conversation.getLastMessage() != null ? conversation.getLastMessage() : "Chưa có tin nhắn");
            tvTime.setText(formatTime(conversation.getLastMessageAt()));

            if (conversation.getUnreadCount() > 0) {
                tvUnreadBadge.setText(String.valueOf(conversation.getUnreadCount()));
                tvUnreadBadge.setVisibility(View.VISIBLE);
            } else {
                tvUnreadBadge.setVisibility(View.GONE);
            }

            String partnerAvatar = conversation.getPartnerAvatarUrl(currentUserId);
            if (partnerAvatar != null) {
                Glide.with(itemView.getContext())
                        .load(partnerAvatar)
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .error(android.R.drawable.sym_def_app_icon)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(conversation);
                }
            });
        }

        private String formatTime(String isoTime) {
            if (isoTime == null || isoTime.length() < 10) return "";
            try {
                // E.g. 2026-06-06T10:28:57
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = parser.parse(isoTime);
                
                // If it's today, show HH:mm. Otherwise, show dd/MM
                Date now = new Date();
                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                dayFormat.setTimeZone(TimeZone.getDefault());
                
                SimpleDateFormat formatter;
                if (dayFormat.format(date).equals(dayFormat.format(now))) {
                    formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                } else {
                    formatter = new SimpleDateFormat("dd/MM", Locale.getDefault());
                }
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
    }
}
