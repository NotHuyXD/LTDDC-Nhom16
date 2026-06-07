package com.example.rentedapp.ui.invoice;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.CreateInvoiceRequest;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;
import java.util.Calendar;

public class CreateInvoiceActivity extends AppCompatActivity {

    private TextView tvContractInfo;
    private EditText etPeriodMonth;
    private EditText etBaseRent;
    private EditText etElectricUsage;
    private EditText etElectricFee;
    private EditText etWaterUsage;
    private EditText etWaterFee;
    private EditText etOtherFees;
    private EditText etDueDate;
    private TextView tvCalculatedTotal;
    private Button btnSubmit;

    private String contractId;
    private String roomTitle;
    private double baseRent;
    private String selectedPeriodMonth = ""; // formatted as YYYY-MM-01
    
    private ApiService apiService;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);

        contractId = getIntent().getStringExtra("contract_id");
        roomTitle = getIntent().getStringExtra("room_title");
        baseRent = getIntent().getDoubleExtra("base_rent", 0);

        if (contractId == null || contractId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin hợp đồng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvContractInfo = findViewById(R.id.tvContractInfo);
        etPeriodMonth = findViewById(R.id.etPeriodMonth);
        etBaseRent = findViewById(R.id.etBaseRent);
        etElectricUsage = findViewById(R.id.etElectricUsage);
        etElectricFee = findViewById(R.id.etElectricFee);
        etWaterUsage = findViewById(R.id.etWaterUsage);
        etWaterFee = findViewById(R.id.etWaterFee);
        etOtherFees = findViewById(R.id.etOtherFees);
        etDueDate = findViewById(R.id.etDueDate);
        tvCalculatedTotal = findViewById(R.id.tvCalculatedTotal);
        btnSubmit = findViewById(R.id.btnSubmit);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Prefill details
        tvContractInfo.setText("Hợp đồng phòng: " + (roomTitle != null ? roomTitle : "..."));
        etBaseRent.setText(String.valueOf((int) baseRent));

        // Date Pickers
        etPeriodMonth.setOnClickListener(v -> showMonthPicker());
        etDueDate.setOnClickListener(v -> showDatePicker(etDueDate));

        // Textwatchers for auto calculations
        TextWatcher autoCalculateWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndDisplayTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etElectricFee.addTextChangedListener(autoCalculateWatcher);
        etWaterFee.addTextChangedListener(autoCalculateWatcher);
        etOtherFees.addTextChangedListener(autoCalculateWatcher);

        calculateAndDisplayTotal();

        btnSubmit.setOnClickListener(v -> performSubmitInvoice());
    }

    private void showMonthPicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Store period month as first day of month (YYYY-MM-01)
                    selectedPeriodMonth = String.format("%04d-%02d-01", selectedYear, selectedMonth + 1);
                    etPeriodMonth.setText(String.format("Tháng %02d/%04d", selectedMonth + 1, selectedYear));
                }, year, month, 1);
        datePickerDialog.show();
    }

    private void showDatePicker(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(dateString);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateAndDisplayTotal() {
        double electricFee = parseDouble(etElectricFee.getText().toString());
        double waterFee = parseDouble(etWaterFee.getText().toString());
        double otherFees = parseDouble(etOtherFees.getText().toString());

        double total = baseRent + electricFee + waterFee + otherFees;
        tvCalculatedTotal.setText(decimalFormat.format(total) + " VNĐ");
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void performSubmitInvoice() {
        String periodMonth = selectedPeriodMonth;
        String dueDate = etDueDate.getText().toString().trim();

        if (periodMonth.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn tháng phát hành hóa đơn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn hạn thanh toán hóa đơn!", Toast.LENGTH_SHORT).show();
            return;
        }

        double electricUsage = parseDouble(etElectricUsage.getText().toString());
        double electricFee = parseDouble(etElectricFee.getText().toString());
        double waterUsage = parseDouble(etWaterUsage.getText().toString());
        double waterFee = parseDouble(etWaterFee.getText().toString());
        double otherFees = parseDouble(etOtherFees.getText().toString());

        CreateInvoiceRequest request = new CreateInvoiceRequest(
                contractId,
                periodMonth,
                baseRent,
                electricUsage,
                waterUsage,
                electricFee,
                waterFee,
                otherFees,
                dueDate
        );

        btnSubmit.setEnabled(false);
        apiService.createInvoice(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                btnSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(CreateInvoiceActivity.this, "Đã lập hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateInvoiceActivity.this, "Không thể lập hóa đơn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnSubmit.setEnabled(true);
                Toast.makeText(CreateInvoiceActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
