package com.example.rentedapp.ui.home;

import android.content.Intent;
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
import com.example.rentedapp.data.model.Bookmark;
import com.example.rentedapp.data.model.Room;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.ui.room.RoomDetailActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private View layoutEmptyState;
    private RoomAdapter adapter;
    private List<Room> favoriteRooms = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomAdapter(favoriteRooms);
        adapter.setOnItemClickListener(room -> {
            Intent intent = new Intent(getContext(), RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", room.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getBookmarks().enqueue(new Callback<ApiResponse<List<Bookmark>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Bookmark>>> call, Response<ApiResponse<List<Bookmark>>> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Bookmark> bookmarks = response.body().getData();
                        favoriteRooms.clear();
                        if (bookmarks != null && !bookmarks.isEmpty()) {
                            for (Bookmark b : bookmarks) {
                                favoriteRooms.add(b.toRoom());
                            }
                            layoutEmptyState.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Không thể tải danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Bookmark>>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
