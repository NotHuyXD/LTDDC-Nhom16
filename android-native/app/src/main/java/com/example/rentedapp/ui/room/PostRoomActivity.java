package com.example.rentedapp.ui.room;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.CreateRoomRequest;
import com.example.rentedapp.data.model.District;
import com.example.rentedapp.data.model.Province;
import com.example.rentedapp.data.model.Room;
import com.example.rentedapp.data.model.RoomType;
import com.example.rentedapp.data.model.Ward;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRoomActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etAddress, etArea, etPrice, etDeposit, etMaxOccupants;
    private Spinner spinnerRoomType, spinnerProvince, spinnerDistrict, spinnerWard;
    private Button btnSubmit;
    private ApiService apiService;

    private List<RoomType> roomTypeList = new ArrayList<>();
    private List<Province> provinceList = new ArrayList<>();
    private List<District> districtList = new ArrayList<>();
    private List<Ward> wardList = new ArrayList<>();

    private ArrayAdapter<RoomType> roomTypeAdapter;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_room);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        apiService = ApiClient.getClient(this).create(ApiService.class);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etAddress = findViewById(R.id.etAddress);
        etArea = findViewById(R.id.etArea);
        etPrice = findViewById(R.id.etPrice);
        etDeposit = findViewById(R.id.etDeposit);
        etMaxOccupants = findViewById(R.id.etMaxOccupants);

        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        btnSubmit = findViewById(R.id.btnSubmit);

        setupSpinners();
        loadInitialData();

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void setupSpinners() {
        roomTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypeList);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(roomTypeAdapter);

        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceList);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wardList);
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(wardAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province selected = provinceList.get(position);
                loadDistricts(selected.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                District selected = districtList.get(position);
                loadWards(selected.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadInitialData() {
        // Load Room Types
        apiService.getRoomTypes().enqueue(new Callback<ApiResponse<List<RoomType>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RoomType>>> call, Response<ApiResponse<List<RoomType>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RoomType> data = response.body().getData();
                    if (data != null) {
                        roomTypeList.clear();
                        roomTypeList.addAll(data);
                        roomTypeAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RoomType>>> call, Throwable t) {
                Toast.makeText(PostRoomActivity.this, "Không thể tải loại phòng", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Provinces
        apiService.getProvinces().enqueue(new Callback<ApiResponse<List<Province>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Province>>> call, Response<ApiResponse<List<Province>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Province> data = response.body().getData();
                    if (data != null) {
                        provinceList.clear();
                        provinceList.addAll(data);
                        provinceAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Province>>> call, Throwable t) {
                Toast.makeText(PostRoomActivity.this, "Không thể tải danh sách Tỉnh/TP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDistricts(String provinceId) {
        apiService.getDistricts(provinceId).enqueue(new Callback<ApiResponse<List<District>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<District>>> call, Response<ApiResponse<List<District>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<District> data = response.body().getData();
                    if (data != null) {
                        districtList.clear();
                        districtList.addAll(data);
                        districtAdapter.notifyDataSetChanged();
                        if (!districtList.isEmpty()) {
                            spinnerDistrict.setSelection(0);
                            loadWards(districtList.get(0).getId());
                        } else {
                            wardList.clear();
                            wardAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<District>>> call, Throwable t) {
                Toast.makeText(PostRoomActivity.this, "Không thể tải Quận/Huyện", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWards(String districtId) {
        apiService.getWards(districtId).enqueue(new Callback<ApiResponse<List<Ward>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ward>>> call, Response<ApiResponse<List<Ward>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ward> data = response.body().getData();
                    if (data != null) {
                        wardList.clear();
                        wardList.addAll(data);
                        wardAdapter.notifyDataSetChanged();
                        if (!wardList.isEmpty()) {
                            spinnerWard.setSelection(0);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ward>>> call, Throwable t) {
                Toast.makeText(PostRoomActivity.this, "Không thể tải Phường/Xã", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSubmit() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String depositStr = etDeposit.getText().toString().trim();
        String maxOccupantsStr = etMaxOccupants.getText().toString().trim();

        if (title.isEmpty() || address.isEmpty() || areaStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền các thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerRoomType.getSelectedItem() == null || spinnerWard.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn loại phòng và địa chỉ đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        String roomTypeId = ((RoomType) spinnerRoomType.getSelectedItem()).getId();
        String wardId = ((Ward) spinnerWard.getSelectedItem()).getId();

        double area = Double.parseDouble(areaStr);
        double price = Double.parseDouble(priceStr);
        double deposit = depositStr.isEmpty() ? 0.0 : Double.parseDouble(depositStr);
        int maxOccupants = maxOccupantsStr.isEmpty() ? 1 : Integer.parseInt(maxOccupantsStr);

        // Include default mock image (we can put a nice unsplash room image link)
        List<String> mockImages = Collections.singletonList("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80");

        CreateRoomRequest request = new CreateRoomRequest(
                title, description, roomTypeId, wardId, address, area, price, deposit, maxOccupants, mockImages
        );

        btnSubmit.setEnabled(false);
        apiService.createRoom(request).enqueue(new Callback<ApiResponse<Room>>() {
            @Override
            public void onResponse(Call<ApiResponse<Room>> call, Response<ApiResponse<Room>> response) {
                btnSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(PostRoomActivity.this, "Đăng phòng thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(PostRoomActivity.this, "Đăng phòng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Room>> call, Throwable t) {
                btnSubmit.setEnabled(true);
                Toast.makeText(PostRoomActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
