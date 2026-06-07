package com.example.rentedapp.ui.contract;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.Contract;

import java.text.DecimalFormat;
import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {

    private final List<Contract> contracts;
    private final String currentUserId;
    private final OnContractClickListener clickListener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public interface OnContractClickListener {
        void onContractClick(Contract contract);
    }

    public ContractAdapter(List<Contract> contracts, String currentUserId, OnContractClickListener clickListener) {
        this.contracts = contracts;
        this.currentUserId = currentUserId;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contract, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        Contract contract = contracts.get(position);

        holder.tvRoomTitle.setText(contract.getRoomTitle() != null ? contract.getRoomTitle() : "Phòng trọ");
        holder.tvContractPrice.setText("Giá thuê: " + decimalFormat.format(contract.getMonthlyRent()) + " VNĐ/tháng");
        holder.tvDeposit.setText("💵 Tiền đặt cọc: " + decimalFormat.format(contract.getDepositAmount()) + " VNĐ");

        // Format dates
        String startDate = formatDate(contract.getStartDate());
        String endDate = formatDate(contract.getEndDate());
        holder.tvDuration.setText("📅 Kỳ hạn: " + startDate + " - " + endDate);

        // Partner info
        boolean isCurrentTenant = currentUserId.equals(contract.getTenantId());
        String partnerName = isCurrentTenant ? contract.getLandlordName() : contract.getTenantName();
        String partnerRoleLabel = isCurrentTenant ? "Chủ nhà: " : "Người thuê: ";
        holder.tvPartnerInfo.setText(partnerRoleLabel + (partnerName != null ? partnerName : "..."));

        // Status badge styling
        String status = contract.getStatus();
        if ("pending_sign".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("CHỜ KÝ");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F59E0B"))); // Orange
        } else if ("active".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("ĐANG HOẠT ĐỘNG");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#10B981"))); // Green
        } else if ("expired".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("HẾT HẠN");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#6B7280"))); // Gray
        } else if ("terminated".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("ĐÃ CHẤM DỨT");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EF4444"))); // Red
        } else {
            holder.tvStatus.setText(status != null ? status.toUpperCase() : "KHÔNG XÁC ĐỊNH");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#374151")));
        }

        // Image loading
        String imageUrl = contract.getAbsoluteRoomImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_room_placeholder)
                .error(R.drawable.ic_room_placeholder)
                .into(holder.ivRoomImage);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onContractClick(contract);
            }
        });
    }

    private String formatDate(String dateStr) {
        if (dateStr == null) return "...";
        if (dateStr.contains("T")) {
            return dateStr.split("T")[0];
        }
        return dateStr;
    }

    @Override
    public int getItemCount() {
        return contracts != null ? contracts.size() : 0;
    }

    static class ContractViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage;
        TextView tvRoomTitle;
        TextView tvContractPrice;
        TextView tvPartnerInfo;
        TextView tvStatus;
        TextView tvDuration;
        TextView tvDeposit;

        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomTitle = itemView.findViewById(R.id.tvRoomTitle);
            tvContractPrice = itemView.findViewById(R.id.tvContractPrice);
            tvPartnerInfo = itemView.findViewById(R.id.tvPartnerInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDeposit = itemView.findViewById(R.id.tvDeposit);
        }
    }
}
