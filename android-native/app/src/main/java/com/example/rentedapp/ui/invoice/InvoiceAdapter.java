package com.example.rentedapp.ui.invoice;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.Invoice;

import java.text.DecimalFormat;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private final List<Invoice> invoices;
    private final OnInvoiceClickListener listener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public interface OnInvoiceClickListener {
        void onInvoiceClick(Invoice invoice);
    }

    public InvoiceAdapter(List<Invoice> invoices, OnInvoiceClickListener listener) {
        this.invoices = invoices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoices.get(position);

        holder.tvRoomTitle.setText(invoice.getRoomTitle() != null ? invoice.getRoomTitle() : "Phòng trọ");
        holder.tvPeriodMonth.setText("Kỳ thanh toán: Tháng " + formatPeriod(invoice.getPeriodMonth()));
        holder.tvTotalAmount.setText(decimalFormat.format(invoice.getTotal()) + " VNĐ");
        holder.tvDueDate.setText("📅 Hạn đóng: " + formatDate(invoice.getDueDate()));

        String status = invoice.getStatus();
        if ("paid".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("ĐÃ THANH TOÁN");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#10B981"))); // Green
        } else if ("unpaid".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("CHƯA THANH TOÁN");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EF4444"))); // Red
        } else if ("overdue".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("QUÁ HẠN");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F59E0B"))); // Orange/Yellow
        } else {
            holder.tvStatus.setText(status != null ? status.toUpperCase() : "CHƯA XÁC ĐỊNH");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#6B7280"))); // Gray
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInvoiceClick(invoice);
            }
        });
    }

    private String formatPeriod(String dateStr) {
        if (dateStr == null) return "...";
        if (dateStr.contains("-")) {
            String[] parts = dateStr.split("-");
            if (parts.length >= 2) {
                return parts[1] + "/" + parts[0];
            }
        }
        return dateStr;
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
        return invoices != null ? invoices.size() : 0;
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomTitle;
        TextView tvPeriodMonth;
        TextView tvStatus;
        TextView tvDueDate;
        TextView tvTotalAmount;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomTitle = itemView.findViewById(R.id.tvRoomTitle);
            tvPeriodMonth = itemView.findViewById(R.id.tvPeriodMonth);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}
