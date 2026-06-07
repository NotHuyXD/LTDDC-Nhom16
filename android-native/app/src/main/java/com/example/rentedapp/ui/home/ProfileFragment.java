package com.example.rentedapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.User;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvName, tvRole, tvEmail, tvPhone;
    private View layoutLandlordOptions;
    private Button btnPostRoom, btnMyRooms, btnLogout;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        authManager = new AuthManager(requireContext());

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvRole = view.findViewById(R.id.tvRole);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        layoutLandlordOptions = view.findViewById(R.id.layoutLandlordOptions);
        btnPostRoom = view.findViewById(R.id.btnPostRoom);
        btnMyRooms = view.findViewById(R.id.btnMyRooms);
        btnLogout = view.findViewById(R.id.btnLogout);
        Button btnAppointments = view.findViewById(R.id.btnAppointments);
        Button btnRentalRequests = view.findViewById(R.id.btnRentalRequests);
        Button btnContracts = view.findViewById(R.id.btnContracts);

        btnLogout.setOnClickListener(v -> handleLogout());
        
        btnPostRoom.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.rentedapp.ui.room.PostRoomActivity.class);
            startActivity(intent);
        });

        btnMyRooms.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.rentedapp.ui.room.MyRoomsActivity.class);
            startActivity(intent);
        });

        btnAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.rentedapp.ui.appointment.AppointmentsActivity.class);
            startActivity(intent);
        });

        btnRentalRequests.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.rentedapp.ui.rental.RentalRequestsActivity.class);
            startActivity(intent);
        });

        btnContracts.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.rentedapp.ui.contract.ContractsActivity.class);
            startActivity(intent);
        });

        Button btnInvoices = view.findViewById(R.id.btnInvoices);
        btnInvoices.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.rentedapp.ui.invoice.InvoicesActivity.class);
            startActivity(intent);
        });

        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.rentedapp.ui.profile.EditProfileActivity.class);
            startActivity(intent);
        });

        loadProfile();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    if (user != null) {
                        displayUser(user);
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Không thể lấy thông tin cá nhân", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayUser(User user) {
        tvName.setText(user.getFullName() != null ? user.getFullName() : "Không tên");
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "Chưa cập nhật");
        tvPhone.setText(user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Chưa cập nhật");
        
        String roleText = "Người Thuê";
        if ("landlord".equalsIgnoreCase(user.getRole())) {
            roleText = "Chủ Trọ";
            layoutLandlordOptions.setVisibility(View.VISIBLE);
        } else if ("admin".equalsIgnoreCase(user.getRole())) {
            roleText = "Quản Trị Viên";
            layoutLandlordOptions.setVisibility(View.VISIBLE);
        } else {
            layoutLandlordOptions.setVisibility(View.GONE);
        }
        tvRole.setText(roleText);

        String avatarUrl = user.getAbsoluteAvatarUrl();
        if (avatarUrl != null) {
            Glide.with(this)
                    .load(avatarUrl)
                    .into(ivAvatar);
        }
    }

    private void handleLogout() {
        authManager.clear();
        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), com.example.rentedapp.ui.auth.LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
