package com.example.rentedapp.ui.rental;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.RentalRequest;

import java.util.List;

public class RentalRequestAdapter extends RecyclerView.Adapter<RentalRequestAdapter.ViewHolder> {

    public interface OnRentalRequestActionListener {
        void onAccept(RentalRequest request);
        void onReject(RentalRequest request);
        void onCancel(RentalRequest request);
    }

    private List<RentalRequest> requests;
    private String currentUserId;
    private boolean isLandlord;
    private OnRentalRequestActionListener listener;

    public RentalRequestAdapter(List<RentalRequest> requests, String currentUserId, boolean isLandlord, OnRentalRequestActionListener listener) {
        this.requests = requests;
        this.currentUserId = currentUserId;
        this.isLandlord = isLandlord;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentalRequest request = requests.get(position);

        holder.tvRoomTitle.setText(request.getRoomTitle() != null ? request.getRoomTitle() : "Phòng trọ");
        holder.tvRoomPrice.setText(String.format("%,.0f VNĐ/tháng", request.getRoomPrice()));

        // Format partner info
        if (isLandlord) {
            String tenantInfo = "Khách thuê: " + (request.getTenantName() != null ? request.getTenantName() : "Chưa cập nhật");
            if (request.getTenantPhone() != null && !request.getTenantPhone().isEmpty()) {
                tenantInfo += " (" + request.getTenantPhone() + ")";
            }
            holder.tvPartnerInfo.setText(tenantInfo);
        } else {
            holder.tvPartnerInfo.setText("Chủ nhà: " + (request.getLandlordName() != null ? request.getLandlordName() : "Chưa cập nhật"));
        }

        // Move-in Date & Occupants
        holder.tvMoveInDate.setText("📅 Dọn vào: " + request.getMoveInDate());
        holder.tvNumPeople.setText("👥 Số người ở: " + request.getNumPeople() + " người");

        // Message
        if (request.getMessage() != null && !request.getMessage().trim().isEmpty()) {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText("Lời nhắn: \"" + request.getMessage() + "\"");
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        // Status Badge
        String status = request.getStatus();
        if ("pending".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Đang chờ");
            holder.tvStatus.getBackground().setTint(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        } else if ("accepted".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Đã duyệt");
            holder.tvStatus.getBackground().setTint(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if ("rejected".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Bị từ chối");
            holder.tvStatus.getBackground().setTint(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.getBackground().setTint(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }

        // Actions visibility
        if ("pending".equalsIgnoreCase(status)) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            if (isLandlord) {
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.GONE);
            } else {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.VISIBLE);
            }
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        // Load cover image
        String imageUrl = getAbsoluteImageUrl(request.getRoomImage());
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_room_placeholder)
                .error(R.drawable.ic_room_placeholder)
                .into(holder.ivRoomImage);

        // Click listeners
        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(request);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(request);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(request);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private String getAbsoluteImageUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        if (relativePath.startsWith("http")) {
            return relativePath;
        }
        String baseUrl = com.example.rentedapp.data.network.ApiClient.BASE_URL;
        String origin = "http://10.0.2.2:5000";
        if (baseUrl.contains("/api/v1/")) {
            origin = baseUrl.split("/api/v1/")[0];
        }
        return origin + (relativePath.startsWith("/") ? "" : "/") + relativePath;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage;
        TextView tvRoomTitle, tvRoomPrice, tvPartnerInfo, tvStatus, tvMoveInDate, tvNumPeople, tvMessage;
        LinearLayout layoutActions;
        Button btnAccept, btnReject, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomTitle = itemView.findViewById(R.id.tvRoomTitle);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvPartnerInfo = itemView.findViewById(R.id.tvPartnerInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMoveInDate = itemView.findViewById(R.id.tvMoveInDate);
            tvNumPeople = itemView.findViewById(R.id.tvNumPeople);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
