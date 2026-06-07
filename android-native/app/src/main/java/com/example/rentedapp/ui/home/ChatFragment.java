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
import com.example.rentedapp.data.model.Conversation;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import com.example.rentedapp.ui.chat.ChatActivity;
import com.example.rentedapp.ui.chat.ConversationAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private View layoutEmptyState;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList = new ArrayList<>();
    
    private ApiService apiService;
    private AuthManager authManager;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewConversations);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        authManager = new AuthManager(requireContext());
        currentUserId = authManager.getUserId();
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        adapter = new ConversationAdapter(conversationList, currentUserId, conversation -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("CONVERSATION_ID", conversation.getId());
            intent.putExtra("PARTNER_NAME", conversation.getPartnerName(currentUserId));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        apiService.getConversations().enqueue(new Callback<ApiResponse<List<Conversation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Conversation>>> call, Response<ApiResponse<List<Conversation>>> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Conversation> data = response.body().getData();
                        conversationList.clear();
                        if (data != null && !data.isEmpty()) {
                            conversationList.addAll(data);
                            layoutEmptyState.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Không thể tải danh sách cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Conversation>>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
