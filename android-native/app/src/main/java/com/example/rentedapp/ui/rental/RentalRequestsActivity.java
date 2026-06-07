package com.example.rentedapp.ui.rental;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.AcceptRentalRequestRequest;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.RentalRequest;
import com.example.rentedapp.data.model.User;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RentalRequestsActivity extends AppCompatActivity implements RentalRequestAdapter.OnRentalRequestActionListener {

    private RecyclerView rvRentalRequests;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private RentalRequestAdapter adapter;
    private List<RentalRequest> requestList = new ArrayList<>();
    private ApiService apiService;
    private AuthManager authManager;

    private User currentUser;
    private boolean isLandlord = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvRentalRequests = findViewById(R.id.rvRentalRequests);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvRentalRequests.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);
        authManager = new AuthManager(this);

        loadProfileAndRequests();
    }

    private void loadProfileAndRequests() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        rvRentalRequests.setVisibility(View.GONE);

        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body().getData();
                    if (currentUser != null) {
                        isLandlord = "landlord".equalsIgnoreCase(currentUser.getRole()) || "admin".equalsIgnoreCase(currentUser.getRole());
                        loadRentalRequests();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RentalRequestsActivity.this, "Không thể lấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RentalRequestsActivity.this, "Không thể lấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RentalRequestsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRentalRequests() {
        apiService.getRentalRequests().enqueue(new Callback<ApiResponse<List<RentalRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RentalRequest>>> call, Response<ApiResponse<List<RentalRequest>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    if (response.body().getData() != null) {
                        requestList.addAll(response.body().getData());
                    }

                    if (requestList.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvRentalRequests.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvRentalRequests.setVisibility(View.VISIBLE);
                        adapter = new RentalRequestAdapter(
                                requestList,
                                authManager.getUserId(),
                                isLandlord,
                                RentalRequestsActivity.this
                        );
                        rvRentalRequests.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(RentalRequestsActivity.this, "Không thể tải danh sách yêu cầu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RentalRequest>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RentalRequestsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAccept(RentalRequest request) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_accept_rental_request, null);
        EditText etStartDate = dialogView.findViewById(R.id.etStartDate);
        EditText etEndDate = dialogView.findViewById(R.id.etEndDate);
        EditText etTerms = dialogView.findViewById(R.id.etTerms);

        // Date pickers logic
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));

        // Auto-fill dates: move-in date and move-in date + 1 year
        String moveInDateRaw = request.getMoveInDate();
        if (moveInDateRaw != null) {
            String cleanDate = moveInDateRaw.split("T")[0];
            etStartDate.setText(cleanDate);
            try {
                String[] parts = cleanDate.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                etEndDate.setText(String.format("%04d-%02d-%02d", year + 1, month, day));
            } catch (Exception ignored) {}
        } else {
            Calendar cal = Calendar.getInstance();
            String today = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            etStartDate.setText(today);
            etEndDate.setText(String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR) + 1, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();
            String terms = etTerms.getText().toString().trim();

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập ngày bắt đầu và kết thúc hợp đồng", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);

            apiService.acceptRentalRequest(request.getId(), new AcceptRentalRequestRequest(startDate, endDate, terms)).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(RentalRequestsActivity.this, "Đã chấp nhận yêu cầu thuê và tạo hợp đồng!", Toast.LENGTH_LONG).show();
                        loadRentalRequests();
                    } else {
                        Toast.makeText(RentalRequestsActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RentalRequestsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onReject(RentalRequest request) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Từ chối yêu cầu thuê phòng này?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    apiService.rejectRentalRequest(request.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(RentalRequestsActivity.this, "Đã từ chối yêu cầu", Toast.LENGTH_SHORT).show();
                                loadRentalRequests();
                            } else {
                                Toast.makeText(RentalRequestsActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RentalRequestsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }

    @Override
    public void onCancel(RentalRequest request) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Hủy yêu cầu thuê phòng này?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    apiService.cancelRentalRequest(request.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(RentalRequestsActivity.this, "Đã hủy yêu cầu thuê phòng", Toast.LENGTH_SHORT).show();
                                loadRentalRequests();
                            } else {
                                Toast.makeText(RentalRequestsActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RentalRequestsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }

    private void showDatePicker(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(dateString);
                }, year, month, day);
        datePickerDialog.show();
    }
}
