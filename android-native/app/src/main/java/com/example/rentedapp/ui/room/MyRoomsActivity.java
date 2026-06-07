package com.example.rentedapp.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.Room;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.ui.home.RoomAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private RoomAdapter adapter;
    private List<Room> myRoomsList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rooms);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewMyRooms);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter(myRoomsList);
        adapter.setOnItemClickListener(room -> {
            Intent intent = new Intent(MyRoomsActivity.this, RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", room.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        loadMyRooms();
    }

    private void loadMyRooms() {
        apiService.getMyRooms().enqueue(new Callback<ApiResponse<List<Room>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Room>>> call, Response<ApiResponse<List<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Room> rooms = response.body().getData();
                    myRoomsList.clear();
                    if (rooms != null && !rooms.isEmpty()) {
                        myRoomsList.addAll(rooms);
                        tvEmptyState.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyRoomsActivity.this, "Không thể tải danh sách phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Room>>> call, Throwable t) {
                Toast.makeText(MyRoomsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
