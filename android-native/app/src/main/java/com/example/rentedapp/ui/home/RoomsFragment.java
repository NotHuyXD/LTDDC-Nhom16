package com.example.rentedapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.Room;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomsFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    private RoomAdapter adapter;
    private List<Room> roomsList = new ArrayList<>();
    private java.util.Map<String, String> currentFilters = new java.util.HashMap<>();

    private android.widget.TextView tvFilterStatus;
    private android.widget.ImageView btnResetFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        tvFilterStatus = view.findViewById(R.id.tvFilterStatus);
        btnResetFilter = view.findViewById(R.id.btnResetFilter);
        View cardFilter = view.findViewById(R.id.cardFilter);

        cardFilter.setOnClickListener(v -> {
            FilterBottomSheetFragment bottomSheet = FilterBottomSheetFragment.newInstance(currentFilters, this);
            bottomSheet.show(getParentFragmentManager(), "filter_bottom_sheet");
        });

        btnResetFilter.setOnClickListener(v -> {
            currentFilters.clear();
            updateFilterUI();
            loadRooms();
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RoomAdapter(roomsList);
        adapter.setOnItemClickListener(room -> {
            android.content.Intent intent = new android.content.Intent(getContext(), com.example.rentedapp.ui.room.RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", room.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        updateFilterUI();
        loadRooms();

        return view;
    }

    private void updateFilterUI() {
        if (currentFilters.isEmpty()) {
            tvFilterStatus.setText("Tìm kiếm và lọc phòng trọ...");
            btnResetFilter.setVisibility(View.GONE);
        } else {
            tvFilterStatus.setText("Đang áp dụng bộ lọc (" + currentFilters.size() + " tiêu chí)");
            btnResetFilter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFilterApplied(java.util.Map<String, String> filters) {
        currentFilters.clear();
        currentFilters.putAll(filters);
        updateFilterUI();
        loadRooms();
    }

    private void loadRooms() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getRooms(currentFilters).enqueue(new Callback<ApiResponse<List<Room>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Room>>> call, Response<ApiResponse<List<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Room> apiRooms = response.body().getData();
                    if (apiRooms != null) {
                        roomsList.clear();
                        roomsList.addAll(apiRooms);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách phòng trọ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Room>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
