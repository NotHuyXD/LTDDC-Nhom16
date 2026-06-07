package com.example.rentedapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.District;
import com.example.rentedapp.data.model.Province;
import com.example.rentedapp.data.model.RoomType;
import com.example.rentedapp.data.model.Ward;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    public interface FilterListener {
        void onFilterApplied(Map<String, String> filters);
    }

    private FilterListener listener;
    private Map<String, String> initialFilters = new HashMap<>();

    private TextInputEditText etPriceMin, etPriceMax, etMaxOccupants;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard, spinnerRoomType;
    private MaterialButton btnReset, btnApply;

    private ApiService apiService;

    private List<Province> provinceList = new ArrayList<>();
    private List<District> districtList = new ArrayList<>();
    private List<Ward> wardList = new ArrayList<>();
    private List<RoomType> roomTypeList = new ArrayList<>();

    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;
    private ArrayAdapter<RoomType> roomTypeAdapter;

    public static FilterBottomSheetFragment newInstance(Map<String, String> currentFilters, FilterListener listener) {
        FilterBottomSheetFragment fragment = new FilterBottomSheetFragment();
        fragment.initialFilters = new HashMap<>(currentFilters);
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        etPriceMin = view.findViewById(R.id.etPriceMin);
        etPriceMax = view.findViewById(R.id.etPriceMax);
        etMaxOccupants = view.findViewById(R.id.etMaxOccupants);

        spinnerProvince = view.findViewById(R.id.spinnerProvince);
        spinnerDistrict = view.findViewById(R.id.spinnerDistrict);
        spinnerWard = view.findViewById(R.id.spinnerWard);
        spinnerRoomType = view.findViewById(R.id.spinnerRoomType);

        btnReset = view.findViewById(R.id.btnReset);
        btnApply = view.findViewById(R.id.btnApply);

        setupSpinners();
        restoreInputs();
        loadInitialData();

        btnReset.setOnClickListener(v -> resetFilters());
        btnApply.setOnClickListener(v -> applyFilters());
    }

    private void restoreInputs() {
        if (initialFilters.containsKey("priceMin")) {
            etPriceMin.setText(initialFilters.get("priceMin"));
        }
        if (initialFilters.containsKey("priceMax")) {
            etPriceMax.setText(initialFilters.get("priceMax"));
        }
        if (initialFilters.containsKey("maxOccupants")) {
            etMaxOccupants.setText(initialFilters.get("maxOccupants"));
        }
    }

    private void setupSpinners() {
        // Province
        provinceAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, provinceList);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        // District
        districtAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        // Ward
        wardAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, wardList);
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(wardAdapter);

        // Room Type
        roomTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roomTypeList);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(roomTypeAdapter);

        // Selection listeners
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province selected = provinceList.get(position);
                if (selected.getId().isEmpty()) {
                    setEmptyDistricts();
                } else {
                    loadDistricts(selected.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                District selected = districtList.get(position);
                if (selected.getId().isEmpty()) {
                    setEmptyWards();
                } else {
                    loadWards(selected.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setEmptyDistricts() {
        districtList.clear();
        districtList.add(new District("", "Tất cả Quận/Huyện"));
        districtAdapter.notifyDataSetChanged();
        spinnerDistrict.setSelection(0);
        setEmptyWards();
    }

    private void setEmptyWards() {
        wardList.clear();
        wardList.add(new Ward("", "Tất cả Phường/Xã"));
        wardAdapter.notifyDataSetChanged();
        spinnerWard.setSelection(0);
    }

    private void loadInitialData() {
        // Load Room Types
        apiService.getRoomTypes().enqueue(new Callback<ApiResponse<List<RoomType>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RoomType>>> call, Response<ApiResponse<List<RoomType>>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<RoomType> data = response.body().getData();
                    roomTypeList.clear();
                    roomTypeList.add(new RoomType("", "Tất cả loại phòng"));
                    if (data != null) {
                        roomTypeList.addAll(data);
                    }
                    roomTypeAdapter.notifyDataSetChanged();
                    
                    // Preselect
                    String savedRoomTypeId = initialFilters.get("roomTypeId");
                    if (savedRoomTypeId != null) {
                        for (int i = 0; i < roomTypeList.size(); i++) {
                            if (roomTypeList.get(i).getId().equals(savedRoomTypeId)) {
                                spinnerRoomType.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RoomType>>> call, Throwable t) {}
        });

        // Load Provinces
        apiService.getProvinces().enqueue(new Callback<ApiResponse<List<Province>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Province>>> call, Response<ApiResponse<List<Province>>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<Province> data = response.body().getData();
                    provinceList.clear();
                    provinceList.add(new Province("", "Tất cả Tỉnh/Thành phố"));
                    if (data != null) {
                        provinceList.addAll(data);
                    }
                    provinceAdapter.notifyDataSetChanged();

                    // Preselect
                    String savedProvinceId = initialFilters.get("provinceId");
                    if (savedProvinceId != null) {
                        for (int i = 0; i < provinceList.size(); i++) {
                            if (provinceList.get(i).getId().equals(savedProvinceId)) {
                                spinnerProvince.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Province>>> call, Throwable t) {}
        });
    }

    private void loadDistricts(String provinceId) {
        apiService.getDistricts(provinceId).enqueue(new Callback<ApiResponse<List<District>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<District>>> call, Response<ApiResponse<List<District>>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<District> data = response.body().getData();
                    districtList.clear();
                    districtList.add(new District("", "Tất cả Quận/Huyện"));
                    if (data != null) {
                        districtList.addAll(data);
                    }
                    districtAdapter.notifyDataSetChanged();
                    spinnerDistrict.setSelection(0);

                    // Preselect
                    String savedDistrictId = initialFilters.get("districtId");
                    if (savedDistrictId != null) {
                        for (int i = 0; i < districtList.size(); i++) {
                            if (districtList.get(i).getId().equals(savedDistrictId)) {
                                spinnerDistrict.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<District>>> call, Throwable t) {}
        });
    }

    private void loadWards(String districtId) {
        apiService.getWards(districtId).enqueue(new Callback<ApiResponse<List<Ward>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ward>>> call, Response<ApiResponse<List<Ward>>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<Ward> data = response.body().getData();
                    wardList.clear();
                    wardList.add(new Ward("", "Tất cả Phường/Xã"));
                    if (data != null) {
                        wardList.addAll(data);
                    }
                    wardAdapter.notifyDataSetChanged();
                    spinnerWard.setSelection(0);

                    // Preselect
                    String savedWardId = initialFilters.get("wardId");
                    if (savedWardId != null) {
                        for (int i = 0; i < wardList.size(); i++) {
                            if (wardList.get(i).getId().equals(savedWardId)) {
                                spinnerWard.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ward>>> call, Throwable t) {}
        });
    }

    private void resetFilters() {
        etPriceMin.setText("");
        etPriceMax.setText("");
        etMaxOccupants.setText("");
        if (!provinceList.isEmpty()) spinnerProvince.setSelection(0);
        if (!roomTypeList.isEmpty()) spinnerRoomType.setSelection(0);
        setEmptyDistricts();
    }

    private void applyFilters() {
        Map<String, String> filters = new HashMap<>();

        String priceMin = etPriceMin.getText().toString().trim();
        String priceMax = etPriceMax.getText().toString().trim();
        String maxOccupants = etMaxOccupants.getText().toString().trim();

        if (!priceMin.isEmpty()) filters.put("priceMin", priceMin);
        if (!priceMax.isEmpty()) filters.put("priceMax", priceMax);
        if (!maxOccupants.isEmpty()) filters.put("maxOccupants", maxOccupants);

        if (spinnerProvince.getSelectedItem() != null) {
            String pId = ((Province) spinnerProvince.getSelectedItem()).getId();
            if (!pId.isEmpty()) filters.put("provinceId", pId);
        }

        if (spinnerDistrict.getSelectedItem() != null) {
            String dId = ((District) spinnerDistrict.getSelectedItem()).getId();
            if (!dId.isEmpty()) filters.put("districtId", dId);
        }

        if (spinnerWard.getSelectedItem() != null) {
            String wId = ((Ward) spinnerWard.getSelectedItem()).getId();
            if (!wId.isEmpty()) filters.put("wardId", wId);
        }

        if (spinnerRoomType.getSelectedItem() != null) {
            String rtId = ((RoomType) spinnerRoomType.getSelectedItem()).getId();
            if (!rtId.isEmpty()) filters.put("roomTypeId", rtId);
        }

        if (listener != null) {
            listener.onFilterApplied(filters);
        }
        dismiss();
    }
}
