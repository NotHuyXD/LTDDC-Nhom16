package com.example.rentedapp.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.Message;
import com.example.rentedapp.data.model.SendMessageRequest;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private MaterialButton btnSend;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    
    private String conversationId;
    private String partnerName;
    private String currentUserId;
    private ApiService apiService;
    private AuthManager authManager;

    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private static final int POLL_INTERVAL_MS = 4000; // Poll every 4 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        conversationId = getIntent().getStringExtra("CONVERSATION_ID");
        partnerName = getIntent().getStringExtra("PARTNER_NAME");

        if (conversationId == null) {
            Toast.makeText(this, "Không xác định được cuộc hội thoại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authManager = new AuthManager(this);
        currentUserId = authManager.getUserId();
        apiService = ApiClient.getClient(this).create(ApiService.class);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (partnerName != null) {
            toolbar.setTitle(partnerName);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());

        loadMessages(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPolling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPolling();
    }

    private void startPolling() {
        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
    }

    private void stopPolling() {
        pollHandler.removeCallbacks(pollRunnable);
    }

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            loadMessages(false);
            pollHandler.postDelayed(this, POLL_INTERVAL_MS);
        }
    };

    private void loadMessages(boolean shouldScrollToBottom) {
        apiService.getMessages(conversationId).enqueue(new Callback<ApiResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Message>>> call, Response<ApiResponse<List<Message>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> newMessages = response.body().getData();
                    if (newMessages != null) {
                        // Check if we have new messages
                        int oldSize = messageList.size();
                        if (newMessages.size() != oldSize) {
                            messageList.clear();
                            messageList.addAll(newMessages);
                            adapter.notifyDataSetChanged();
                            if (shouldScrollToBottom || oldSize == 0) {
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Message>>> call, Throwable t) {
                if (shouldScrollToBottom) {
                    Toast.makeText(ChatActivity.this, "Không thể tải tin nhắn: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        btnSend.setEnabled(false);
        apiService.sendMessage(conversationId, new SendMessageRequest(content)).enqueue(new Callback<ApiResponse<Message>>() {
            @Override
            public void onResponse(Call<ApiResponse<Message>> call, Response<ApiResponse<Message>> response) {
                btnSend.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Message sentMessage = response.body().getData();
                    if (sentMessage != null) {
                        etMessage.setText("");
                        messageList.add(sentMessage);
                        adapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Gửi tin nhắn thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Message>> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(ChatActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
