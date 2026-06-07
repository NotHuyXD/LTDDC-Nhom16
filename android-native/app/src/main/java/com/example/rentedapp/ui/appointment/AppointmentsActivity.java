package com.example.rentedapp.ui.appointment;

import android.os.Bundle;
import android.view.View;
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
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.Appointment;
import com.example.rentedapp.data.model.User;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView rvAppointments;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private ApiService apiService;
    private AuthManager authManager;

    private User currentUser;
    private boolean isLandlord = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvAppointments = findViewById(R.id.rvAppointments);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvAppointments.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);
        authManager = new AuthManager(this);

        loadProfileAndAppointments();
    }

    private void loadProfileAndAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        rvAppointments.setVisibility(View.GONE);

        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body().getData();
                    if (currentUser != null) {
                        isLandlord = "landlord".equalsIgnoreCase(currentUser.getRole()) || "admin".equalsIgnoreCase(currentUser.getRole());
                        loadAppointments();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AppointmentsActivity.this, "Không thể lấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AppointmentsActivity.this, "Không thể lấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AppointmentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointments() {
        apiService.getAppointments().enqueue(new Callback<ApiResponse<List<Appointment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Appointment>>> call, Response<ApiResponse<List<Appointment>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    appointmentList.clear();
                    if (response.body().getData() != null) {
                        appointmentList.addAll(response.body().getData());
                    }

                    if (appointmentList.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvAppointments.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvAppointments.setVisibility(View.VISIBLE);
                        adapter = new AppointmentAdapter(
                                appointmentList,
                                authManager.getUserId(),
                                isLandlord,
                                AppointmentsActivity.this
                        );
                        rvAppointments.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(AppointmentsActivity.this, "Không thể tải danh sách lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Appointment>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AppointmentsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConfirm(Appointment appointment) {
        apiService.confirmAppointment(appointment.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentsActivity.this, "Xác nhận lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    loadAppointments();
                } else {
                    Toast.makeText(AppointmentsActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(AppointmentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancel(Appointment appointment) {
        String confirmMsg = isLandlord ? "Từ chối lịch hẹn xem phòng này?" : "Hủy lịch hẹn xem phòng này?";
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage(confirmMsg)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    apiService.cancelAppointment(appointment.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AppointmentsActivity.this, "Đã hủy lịch hẹn", Toast.LENGTH_SHORT).show();
                                loadAppointments();
                            } else {
                                Toast.makeText(AppointmentsActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(AppointmentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }
}
