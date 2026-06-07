package com.example.rentedapp.ui.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rentedapp.R;
import com.example.rentedapp.data.model.ApiResponse;
import com.example.rentedapp.data.model.Invoice;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.google.android.material.tabs.TabLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class InvoicesActivity extends AppCompatActivity implements InvoiceAdapter.OnInvoiceClickListener {

    private RecyclerView rvInvoices;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private TabLayout tabLayout;

    private InvoiceAdapter adapter;
    private final List<Invoice> invoiceList = new ArrayList<>();
    private ApiService apiService;
    private String currentStatusFilter = null; // null = All, "unpaid", "paid"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvInvoices = findViewById(R.id.rvInvoices);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tabLayout = findViewById(R.id.tabLayout);

        rvInvoices.setLayoutManager(new LinearLayoutManager(this));
        apiService = ApiClient.getClient(this).create(ApiService.class);

        adapter = new InvoiceAdapter(invoiceList, this);
        rvInvoices.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentStatusFilter = null; // Tất cả
                        break;
                    case 1:
                        currentStatusFilter = "unpaid"; // Chưa thanh toán
                        break;
                    case 2:
                        currentStatusFilter = "paid"; // Đã thanh toán
                        break;
                }
                loadInvoices();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInvoices();
    }

    private void loadInvoices() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        rvInvoices.setVisibility(View.GONE);

        apiService.getInvoices(currentStatusFilter, null).enqueue(new Callback<ApiResponse<List<Invoice>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Invoice>>> call, Response<ApiResponse<List<Invoice>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    invoiceList.clear();
                    if (response.body().getData() != null) {
                        invoiceList.addAll(response.body().getData());
                    }

                    if (invoiceList.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvInvoices.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvInvoices.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(InvoicesActivity.this, "Không thể tải danh sách hóa đơn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Invoice>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InvoicesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onInvoiceClick(Invoice invoice) {
        Intent intent = new Intent(this, InvoiceDetailActivity.class);
        intent.putExtra("invoice_id", invoice.getId());
        startActivity(intent);
    }
}
