package com.example.rentedapp.ui.room;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.AddBookmarkRequest;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.BookmarkCheckResponse;
import com.example.rentedapp.data.model.Room;
import com.example.rentedapp.data.model.CreateConversationRequest;
import com.example.rentedapp.data.model.ConversationIdResponse;
import com.example.rentedapp.data.model.CreateAppointmentRequest;
import com.example.rentedapp.data.model.CreateRentalRequestRequest;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvPrice, tvAddress, tvArea, tvDescription;
    private ImageView ivCover;
    private String roomId;
    private boolean isBookmarked = false;
    private android.view.MenuItem bookmarkItem;
    private ApiService apiService;
    private String landlordId;
    private String landlordName;

    private Button btnChat, btnBook, btnRent;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        authManager = new AuthManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.detail_menu);
        bookmarkItem = toolbar.getMenu().findItem(R.id.action_bookmark);
        
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_bookmark) {
                toggleBookmark();
                return true;
            }
            return false;
        });

        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvAddress = findViewById(R.id.tvAddress);
        tvArea = findViewById(R.id.tvArea);
        tvDescription = findViewById(R.id.tvDescription);
        ivCover = findViewById(R.id.ivCover);

        btnChat = findViewById(R.id.btnChat);
        btnBook = findViewById(R.id.btnBook);
        btnRent = findViewById(R.id.btnRent);

        btnChat.setOnClickListener(v -> {
            if (landlordId == null) {
                Toast.makeText(this, "Đang tải thông tin chủ nhà, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                return;
            }
            apiService.getOrCreateConversation(new CreateConversationRequest(landlordId, roomId)).enqueue(new Callback<ApiResponse<ConversationIdResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<ConversationIdResponse>> call, Response<ApiResponse<ConversationIdResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        String conversationId = response.body().getData().getId();
                        android.content.Intent intent = new android.content.Intent(RoomDetailActivity.this, com.example.rentedapp.ui.chat.ChatActivity.class);
                        intent.putExtra("CONVERSATION_ID", conversationId);
                        intent.putExtra("PARTNER_NAME", landlordName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RoomDetailActivity.this, "Không thể tạo cuộc hội thoại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ConversationIdResponse>> call, Throwable t) {
                    Toast.makeText(RoomDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBook.setOnClickListener(v -> showBookAppointmentDialog());
        btnRent.setOnClickListener(v -> showRentalRequestDialog());

        roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId != null) {
            loadRoomDetail(roomId);
            checkBookmarkStatus();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showBookAppointmentDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_book_appointment, null);
        EditText etAppointmentDate = dialogView.findViewById(R.id.etAppointmentDate);
        Spinner spinnerAppointmentTime = dialogView.findViewById(R.id.spinnerAppointmentTime);
        EditText etMessage = dialogView.findViewById(R.id.etMessage);

        // Date picker
        etAppointmentDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1); // Only tomorrow onwards
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RoomDetailActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        etAppointmentDate.setText(dateString);
                    }, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
            datePickerDialog.show();
        });

        // Time Spinner
        String[] times = new String[]{"07:00","07:30","08:00","08:30","09:00","09:30","10:00","10:30",
                                      "11:00","11:30","13:00","13:30","14:00","14:30","15:00","15:30",
                                      "16:00","16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAppointmentTime.setAdapter(timeAdapter);
        spinnerAppointmentTime.setSelection(4); // Default 09:00

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String date = etAppointmentDate.getText().toString().trim();
            String time = spinnerAppointmentTime.getSelectedItem().toString();
            String message = etMessage.getText().toString().trim();

            if (date.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày hẹn", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            apiService.createAppointment(new CreateAppointmentRequest(roomId, date, time, message)).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RoomDetailActivity.this, "Đặt lịch hẹn xem phòng thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RoomDetailActivity.this, "Đặt lịch thất bại hoặc lịch hẹn đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(RoomDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showRentalRequestDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rental_request, null);
        EditText etMoveInDate = dialogView.findViewById(R.id.etMoveInDate);
        Spinner spinnerNumPeople = dialogView.findViewById(R.id.spinnerNumPeople);
        EditText etMessage = dialogView.findViewById(R.id.etMessage);

        // Date picker
        etMoveInDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RoomDetailActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        etMoveInDate.setText(dateString);
                    }, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
            datePickerDialog.show();
        });

        // Occupants Spinner
        String[] peopleOptions = new String[]{"1 người", "2 người", "3 người", "4 người", "5 người"};
        ArrayAdapter<String> peopleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, peopleOptions);
        peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumPeople.setAdapter(peopleAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String moveInDate = etMoveInDate.getText().toString().trim();
            int numPeople = spinnerNumPeople.getSelectedItemPosition() + 1;
            String message = etMessage.getText().toString().trim();

            if (moveInDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày dọn vào", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            apiService.createRentalRequest(new CreateRentalRequestRequest(roomId, moveInDate, numPeople, message)).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RoomDetailActivity.this, "Gửi yêu cầu thuê phòng thành công! Chủ trọ sẽ phản hồi sớm.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RoomDetailActivity.this, "Gửi yêu cầu thất bại hoặc yêu cầu đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(RoomDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void checkBookmarkStatus() {
        apiService.checkBookmark(roomId).enqueue(new Callback<ApiResponse<BookmarkCheckResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookmarkCheckResponse>> call, Response<ApiResponse<BookmarkCheckResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BookmarkCheckResponse data = response.body().getData();
                    if (data != null) {
                        isBookmarked = data.isBookmarked();
                        updateBookmarkIcon();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookmarkCheckResponse>> call, Throwable t) {}
        });
    }

    private void toggleBookmark() {
        if (isBookmarked) {
            apiService.removeBookmark(roomId).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        isBookmarked = false;
                        updateBookmarkIcon();
                        Toast.makeText(RoomDetailActivity.this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
            });
        } else {
            apiService.addBookmark(new AddBookmarkRequest(roomId)).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        isBookmarked = true;
                        updateBookmarkIcon();
                        Toast.makeText(RoomDetailActivity.this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
            });
        }
    }

    private void updateBookmarkIcon() {
        if (bookmarkItem != null) {
            bookmarkItem.setIcon(isBookmarked ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        }
    }

    private void loadRoomDetail(String id) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getRoomById(id).enqueue(new Callback<ApiResponse<Room>>() {
            @Override
            public void onResponse(Call<ApiResponse<Room>> call, Response<ApiResponse<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Room room = response.body().getData();
                    if (room != null) {
                        landlordId = room.getLandlordId();
                        landlordName = room.getLandlordName();
                        tvTitle.setText(room.getTitle());
                        tvPrice.setText(room.getPrice() != null ? room.getPrice() + " VNĐ" : "Đang cập nhật");
                        tvAddress.setText(room.getAddress());
                        tvArea.setText(room.getArea() != null ? "Diện tích: " + room.getArea() + " m2" : "Diện tích: Đang cập nhật");
                        tvDescription.setText(room.getDescription() != null ? room.getDescription() : "Chưa có mô tả.");

                        // Show/Hide buttons depending on availability and role
                        String currentUserId = authManager.getUserId();
                        boolean isOwnRoom = currentUserId != null && currentUserId.equalsIgnoreCase(landlordId);
                        boolean isAvailable = "available".equalsIgnoreCase(room.getStatus());

                        if (isOwnRoom || !isAvailable) {
                            btnBook.setVisibility(View.GONE);
                            btnRent.setVisibility(View.GONE);
                        } else {
                            btnBook.setVisibility(View.VISIBLE);
                            btnRent.setVisibility(View.VISIBLE);
                        }

                        String coverUrl = room.getAbsoluteCoverImageUrl();
                        android.util.Log.d("ROOM_DETAIL", "coverUrl from room: " + coverUrl);
                        List<String> imageUrls = room.getAbsoluteImageUrls();
                        android.util.Log.d("ROOM_DETAIL", "absoluteImageUrls size: " + imageUrls.size());
                        if (coverUrl == null && !imageUrls.isEmpty()) {
                            coverUrl = imageUrls.get(0);
                            android.util.Log.d("ROOM_DETAIL", "using fallback coverUrl: " + coverUrl);
                        }
                        final String finalCoverUrl = coverUrl;
                        Glide.with(RoomDetailActivity.this)
                                .load(coverUrl)
                                .placeholder(R.drawable.ic_room_placeholder)
                                .error(R.drawable.ic_room_placeholder)
                                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                        android.util.Log.e("GLIDE_ERROR", "Detail load failed: " + (e != null ? e.getMessage() : "unknown") + " for URL: " + finalCoverUrl);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(ivCover);
                    }
                } else {
                    Toast.makeText(RoomDetailActivity.this, "Không thể tải dữ liệu phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Room>> call, Throwable t) {
                Toast.makeText(RoomDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
