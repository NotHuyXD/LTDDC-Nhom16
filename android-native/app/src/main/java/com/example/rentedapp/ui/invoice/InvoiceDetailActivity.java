package com.example.rentedapp.ui.invoice;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.CreatePaymentRequest;
import com.example.rentedapp.data.model.Invoice;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;

public class InvoiceDetailActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private ProgressBar progressBar;

    private TextView tvStatus;
    private TextView tvRoomTitle;
    private TextView tvRoomAddress;
    private TextView tvPeriodMonth;

    private TextView tvBaseRent;
    private TextView tvElectricUsage;
    private TextView tvElectricFee;
    private TextView tvWaterUsage;
    private TextView tvWaterFee;
    private TextView tvOtherFees;
    private TextView tvTotal;

    private TextView tvDueDate;
    private LinearLayout layoutPaidDate;
    private TextView tvPaidDate;

    private Button btnPay;

    private ApiService apiService;
    private AuthManager authManager;
    private String invoiceId;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        invoiceId = getIntent().getStringExtra("invoice_id");
        if (invoiceId == null || invoiceId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã hóa đơn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);

        tvStatus = findViewById(R.id.tvStatus);
        tvRoomTitle = findViewById(R.id.tvRoomTitle);
        tvRoomAddress = findViewById(R.id.tvRoomAddress);
        tvPeriodMonth = findViewById(R.id.tvPeriodMonth);

        tvBaseRent = findViewById(R.id.tvBaseRent);
        tvElectricUsage = findViewById(R.id.tvElectricUsage);
        tvElectricFee = findViewById(R.id.tvElectricFee);
        tvWaterUsage = findViewById(R.id.tvWaterUsage);
        tvWaterFee = findViewById(R.id.tvWaterFee);
        tvOtherFees = findViewById(R.id.tvOtherFees);
        tvTotal = findViewById(R.id.tvTotal);

        tvDueDate = findViewById(R.id.tvDueDate);
        layoutPaidDate = findViewById(R.id.layoutPaidDate);
        tvPaidDate = findViewById(R.id.tvPaidDate);

        btnPay = findViewById(R.id.btnPay);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        authManager = new AuthManager(this);

        btnPay.setOnClickListener(v -> showPaymentDialog());

        loadInvoiceDetail();
    }

    private void loadInvoiceDetail() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        apiService.getInvoiceById(invoiceId).enqueue(new Callback<ApiResponse<Invoice>>() {
            @Override
            public void onResponse(Call<ApiResponse<Invoice>> call, Response<ApiResponse<Invoice>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Invoice invoice = response.body().getData();
                    if (invoice != null) {
                        displayInvoiceDetail(invoice);
                    } else {
                        Toast.makeText(InvoiceDetailActivity.this, "Hóa đơn trống", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(InvoiceDetailActivity.this, "Không thể tải chi tiết hóa đơn", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Invoice>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InvoiceDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayInvoiceDetail(Invoice invoice) {
        scrollView.setVisibility(View.VISIBLE);

        tvRoomTitle.setText(invoice.getRoomTitle() != null ? invoice.getRoomTitle() : "Phòng trọ");
        tvRoomAddress.setText("Địa chỉ: " + (invoice.getRoomAddress() != null ? invoice.getRoomAddress() : "Chưa cập nhật"));
        tvPeriodMonth.setText("Hóa đơn kỳ: Tháng " + formatPeriod(invoice.getPeriodMonth()));

        // Pricing breakdown
        tvBaseRent.setText(decimalFormat.format(invoice.getBaseRent()) + " VNĐ");
        tvElectricUsage.setText("Tiêu thụ: " + invoice.getElectricUsage() + " kWh");
        tvElectricFee.setText(decimalFormat.format(invoice.getElectricFee()) + " VNĐ");
        tvWaterUsage.setText("Tiêu thụ: " + invoice.getWaterUsage() + " m³");
        tvWaterFee.setText(decimalFormat.format(invoice.getWaterFee()) + " VNĐ");
        tvOtherFees.setText(decimalFormat.format(invoice.getOtherFees()) + " VNĐ");
        tvTotal.setText(decimalFormat.format(invoice.getTotal()) + " VNĐ");

        // Dates
        tvDueDate.setText(formatDate(invoice.getDueDate()));
        if (invoice.getPaidAt() != null) {
            layoutPaidDate.setVisibility(View.VISIBLE);
            tvPaidDate.setText(formatDate(invoice.getPaidAt()));
        } else {
            layoutPaidDate.setVisibility(View.GONE);
        }

        // Status Badge styling
        String status = invoice.getStatus();
        tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
        tvStatus.setTextColor(Color.WHITE);
        if ("paid".equalsIgnoreCase(status)) {
            tvStatus.setText("ĐÃ THANH TOÁN");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#10B981"))); // Green
        } else if ("unpaid".equalsIgnoreCase(status)) {
            tvStatus.setText("CHƯA THANH TOÁN");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF4444"))); // Red
        } else if ("overdue".equalsIgnoreCase(status)) {
            tvStatus.setText("QUÁ HẠN");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F59E0B"))); // Orange
        } else {
            tvStatus.setText(status != null ? status.toUpperCase() : "CHƯA XÁC ĐỊNH");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6B7280")));
        }

        // Show pay button only for tenant and if unpaid/overdue
        String currentUserId = authManager.getUserId();
        btnPay.setVisibility(View.GONE);

        if (!"paid".equalsIgnoreCase(status)) {
            if (currentUserId.equals(invoice.getTenantId())) {
                btnPay.setVisibility(View.VISIBLE);
                btnPay.setTag(invoice.getTotal()); // store total for payment request
            }
        }
    }

    private void showPaymentDialog() {
        String[] methodsDisplay = {"Chuyển khoản ngân hàng", "Ví MoMo", "Tiền mặt", "Ví VNPAY", "Ví ZaloPay"};
        String[] methodsValues = {"bank_transfer", "momo", "cash", "vnpay", "zalopay"};

        final int[] selectedIndex = {0};

        new AlertDialog.Builder(this)
                .setTitle("Chọn phương thức thanh toán")
                .setSingleChoiceItems(methodsDisplay, 0, (dialog, which) -> selectedIndex[0] = which)
                .setPositiveButton("Xác nhận thanh toán", (dialog, which) -> {
                    String method = methodsValues[selectedIndex[0]];
                    double amount = (double) btnPay.getTag();
                    performPayment(method, amount);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performPayment(String method, double amount) {
        progressBar.setVisibility(View.VISIBLE);
        CreatePaymentRequest request = new CreatePaymentRequest(invoiceId, amount, method);

        apiService.createPayment(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(InvoiceDetailActivity.this, "Thanh toán hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    loadInvoiceDetail();
                } else {
                    Toast.makeText(InvoiceDetailActivity.this, "Lỗi thanh toán thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InvoiceDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}
