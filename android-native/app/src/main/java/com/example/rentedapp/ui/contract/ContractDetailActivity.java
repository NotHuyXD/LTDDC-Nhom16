package com.example.rentedapp.ui.contract;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.Contract;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;

public class ContractDetailActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private ProgressBar progressBar;

    private TextView tvStatus;
    private ImageView ivRoomImage;
    private TextView tvRoomTitle;
    private TextView tvRoomAddress;
    private TextView tvRoomArea;

    private TextView tvLandlordName;
    private TextView tvLandlordPhone;
    private TextView tvLandlordEmail;

    private TextView tvTenantName;
    private TextView tvTenantPhone;
    private TextView tvTenantEmail;

    private TextView tvMonthlyRent;
    private TextView tvDepositAmount;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private LinearLayout layoutSignedDate;
    private TextView tvSignedDate;
    private TextView tvTerms;

    private Button btnSign;
    private Button btnTerminate;
    private Button btnCreateInvoice;

    private ApiService apiService;
    private AuthManager authManager;
    private String contractId;
    private Contract currentContract;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);

        contractId = getIntent().getStringExtra("contract_id");
        if (contractId == null || contractId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin hợp đồng!", Toast.LENGTH_SHORT).show();
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
        ivRoomImage = findViewById(R.id.ivRoomImage);
        tvRoomTitle = findViewById(R.id.tvRoomTitle);
        tvRoomAddress = findViewById(R.id.tvRoomAddress);
        tvRoomArea = findViewById(R.id.tvRoomArea);

        tvLandlordName = findViewById(R.id.tvLandlordName);
        tvLandlordPhone = findViewById(R.id.tvLandlordPhone);
        tvLandlordEmail = findViewById(R.id.tvLandlordEmail);

        tvTenantName = findViewById(R.id.tvTenantName);
        tvTenantPhone = findViewById(R.id.tvTenantPhone);
        tvTenantEmail = findViewById(R.id.tvTenantEmail);

        tvMonthlyRent = findViewById(R.id.tvMonthlyRent);
        tvDepositAmount = findViewById(R.id.tvDepositAmount);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        layoutSignedDate = findViewById(R.id.layoutSignedDate);
        tvSignedDate = findViewById(R.id.tvSignedDate);
        tvTerms = findViewById(R.id.tvTerms);

        btnSign = findViewById(R.id.btnSign);
        btnTerminate = findViewById(R.id.btnTerminate);
        btnCreateInvoice = findViewById(R.id.btnCreateInvoice);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        authManager = new AuthManager(this);

        btnSign.setOnClickListener(v -> performSignContract());
        btnTerminate.setOnClickListener(v -> confirmTerminateContract());
        btnCreateInvoice.setOnClickListener(v -> {
            if (currentContract != null) {
                android.content.Intent intent = new android.content.Intent(this, com.example.rentedapp.ui.invoice.CreateInvoiceActivity.class);
                intent.putExtra("contract_id", currentContract.getId());
                intent.putExtra("room_title", currentContract.getRoomTitle());
                intent.putExtra("base_rent", currentContract.getMonthlyRent());
                startActivity(intent);
            }
        });

        loadContractDetail();
    }

    private void loadContractDetail() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        apiService.getContractById(contractId).enqueue(new Callback<ApiResponse<Contract>>() {
            @Override
            public void onResponse(Call<ApiResponse<Contract>> call, Response<ApiResponse<Contract>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Contract contract = response.body().getData();
                    if (contract != null) {
                        displayContractDetail(contract);
                    } else {
                        Toast.makeText(ContractDetailActivity.this, "Hợp đồng trống", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(ContractDetailActivity.this, "Không thể tải chi tiết hợp đồng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Contract>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ContractDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayContractDetail(Contract contract) {
        scrollView.setVisibility(View.VISIBLE);

        // Room Info
        tvRoomTitle.setText(contract.getRoomTitle() != null ? contract.getRoomTitle() : "Phòng trọ");
        tvRoomAddress.setText("Địa chỉ: " + (contract.getRoomAddress() != null ? contract.getRoomAddress() : "Chưa cập nhật"));
        tvRoomArea.setText("Diện tích: " + contract.getRoomArea() + " m²");

        Glide.with(this)
                .load(contract.getAbsoluteRoomImageUrl())
                .placeholder(R.drawable.ic_room_placeholder)
                .error(R.drawable.ic_room_placeholder)
                .into(ivRoomImage);

        // Landlord Info
        tvLandlordName.setText("Họ tên: " + (contract.getLandlordName() != null ? contract.getLandlordName() : "..."));
        tvLandlordPhone.setText("Số điện thoại: " + (contract.getLandlordPhone() != null ? contract.getLandlordPhone() : "Chưa cập nhật"));
        tvLandlordEmail.setText("Email: " + (contract.getLandlordEmail() != null ? contract.getLandlordEmail() : "Chưa cập nhật"));

        // Tenant Info
        tvTenantName.setText("Họ tên: " + (contract.getTenantName() != null ? contract.getTenantName() : "..."));
        tvTenantPhone.setText("Số điện thoại: " + (contract.getTenantPhone() != null ? contract.getTenantPhone() : "Chưa cập nhật"));
        tvTenantEmail.setText("Email: " + (contract.getTenantEmail() != null ? contract.getTenantEmail() : "Chưa cập nhật"));

        // Terms
        tvMonthlyRent.setText(decimalFormat.format(contract.getMonthlyRent()) + " VNĐ / tháng");
        tvDepositAmount.setText(decimalFormat.format(contract.getDepositAmount()) + " VNĐ");
        tvStartDate.setText(formatDate(contract.getStartDate()));
        tvEndDate.setText(formatDate(contract.getEndDate()));

        if (contract.getSignedAt() != null) {
            layoutSignedDate.setVisibility(View.VISIBLE);
            tvSignedDate.setText(formatDate(contract.getSignedAt()));
        } else {
            layoutSignedDate.setVisibility(View.GONE);
        }

        tvTerms.setText(contract.getTerms() != null && !contract.getTerms().isEmpty() ? contract.getTerms() : "Không có điều khoản bổ sung.");

        // Status Badge
        String status = contract.getStatus();
        tvStatus.setBackgroundResource(R.drawable.bg_status_badge);
        tvStatus.setTextColor(Color.WHITE);
        if ("pending_sign".equalsIgnoreCase(status)) {
            tvStatus.setText("CHỜ KÝ");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F59E0B")));
        } else if ("active".equalsIgnoreCase(status)) {
            tvStatus.setText("ĐANG HOẠT ĐỘNG");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#10B981")));
        } else if ("expired".equalsIgnoreCase(status)) {
            tvStatus.setText("HẾT HẠN");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6B7280")));
        } else if ("terminated".equalsIgnoreCase(status)) {
            tvStatus.setText("ĐÃ CHẤM DỨT");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF4444")));
        } else {
            tvStatus.setText(status != null ? status.toUpperCase() : "CHƯA XÁC ĐỊNH");
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#374151")));
        }

        // Store reference to current contract
        this.currentContract = contract;

        // Action Buttons logic
        String currentUserId = authManager.getUserId();
        btnSign.setVisibility(View.GONE);
        btnTerminate.setVisibility(View.GONE);
        btnCreateInvoice.setVisibility(View.GONE);

        if ("pending_sign".equalsIgnoreCase(status)) {
            // Only tenant can sign the contract
            if (currentUserId.equals(contract.getTenantId())) {
                btnSign.setVisibility(View.VISIBLE);
            }
        } else if ("active".equalsIgnoreCase(status)) {
            // Only landlord can terminate or invoice
            if (currentUserId.equals(contract.getLandlordId())) {
                btnTerminate.setVisibility(View.VISIBLE);
                btnCreateInvoice.setVisibility(View.VISIBLE);
            }
        }
    }

    private void performSignContract() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận ký hợp đồng")
                .setMessage("Tôi đã đọc kỹ và đồng ý với tất cả điều khoản hợp đồng thuê phòng.")
                .setPositiveButton("Ký hợp đồng", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    apiService.signContract(contractId).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(ContractDetailActivity.this, "Ký hợp đồng thành công!", Toast.LENGTH_SHORT).show();
                                loadContractDetail();
                            } else {
                                Toast.makeText(ContractDetailActivity.this, "Lỗi khi ký hợp đồng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ContractDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmTerminateContract() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận chấm dứt hợp đồng")
                .setMessage("Bạn có chắc chắn muốn chấm dứt hợp đồng này? Phòng trọ sẽ tự động được đưa về trạng thái trống (sẵn sàng cho thuê). Hành động này không thể hoàn tác.")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    apiService.terminateContract(contractId).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(ContractDetailActivity.this, "Chấm dứt hợp đồng thành công!", Toast.LENGTH_SHORT).show();
                                loadContractDetail();
                            } else {
                                Toast.makeText(ContractDetailActivity.this, "Lỗi khi chấm dứt hợp đồng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ContractDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String formatDate(String dateStr) {
        if (dateStr == null) return "...";
        if (dateStr.contains("T")) {
            return dateStr.split("T")[0];
        }
        return dateStr;
    }
}
