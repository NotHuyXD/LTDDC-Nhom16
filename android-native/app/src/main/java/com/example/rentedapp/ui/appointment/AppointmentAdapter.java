package com.example.rentedapp.ui.appointment;

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
import com.example.rentedapp.data.model.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnAppointmentActionListener {
        void onConfirm(Appointment appointment);
        void onCancel(Appointment appointment);
    }

    private List<Appointment> appointments;
    private String currentUserId;
    private boolean isLandlord;
    private OnAppointmentActionListener listener;

    public AppointmentAdapter(List<Appointment> appointments, String currentUserId, boolean isLandlord, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.currentUserId = currentUserId;
        this.isLandlord = isLandlord;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        holder.tvRoomTitle.setText(appointment.getRoomTitle() != null ? appointment.getRoomTitle() : "Phòng trọ");
        holder.tvRoomAddress.setText(appointment.getRoomAddress() != null ? appointment.getRoomAddress() : "");

        // Format partner info
        if (isLandlord) {
            String tenantInfo = "Khách thuê: " + (appointment.getTenantName() != null ? appointment.getTenantName() : "Chưa cập nhật");
            if (appointment.getTenantPhone() != null && !appointment.getTenantPhone().isEmpty()) {
                tenantInfo += " (" + appointment.getTenantPhone() + ")";
            }
            holder.tvPartnerInfo.setText(tenantInfo);
        } else {
            String landlordInfo = "Chủ nhà: " + (appointment.getLandlordName() != null ? appointment.getLandlordName() : "Chưa cập nhật");
            if (appointment.getLandlordPhone() != null && !appointment.getLandlordPhone().isEmpty()) {
                landlordInfo += " (" + appointment.getLandlordPhone() + ")";
            }
            holder.tvPartnerInfo.setText(landlordInfo);
        }

        // Appointment Time
        holder.tvAppointmentTime.setText("📅 Lịch hẹn: " + appointment.getAppointmentDate() + " lúc " + appointment.getAppointmentTime());

        // Message
        if (appointment.getMessage() != null && !appointment.getMessage().trim().isEmpty()) {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText("Lời nhắn: \"" + appointment.getMessage() + "\"");
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        // Status Badge
        String status = appointment.getStatus();
        if ("pending".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Đang chờ");
            holder.tvStatus.getBackground().setTint(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        } else if ("confirmed".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Đã xác nhận");
            holder.tvStatus.getBackground().setTint(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.getBackground().setTint(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }

        // Actions visibility
        if ("pending".equalsIgnoreCase(status)) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            if (isLandlord) {
                holder.btnConfirm.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setText("Từ chối");
            } else {
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setText("Hủy hẹn");
            }
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        // Load cover image
        String imageUrl = getAbsoluteImageUrl(appointment.getRoomImage());
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_room_placeholder)
                .error(R.drawable.ic_room_placeholder)
                .into(holder.ivRoomImage);

        // Click listeners
        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) listener.onConfirm(appointment);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(appointment);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
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
        TextView tvRoomTitle, tvRoomAddress, tvPartnerInfo, tvStatus, tvAppointmentTime, tvMessage;
        LinearLayout layoutActions;
        Button btnConfirm, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomTitle = itemView.findViewById(R.id.tvRoomTitle);
            tvRoomAddress = itemView.findViewById(R.id.tvRoomAddress);
            tvPartnerInfo = itemView.findViewById(R.id.tvPartnerInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAppointmentTime = itemView.findViewById(R.id.tvAppointmentTime);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
