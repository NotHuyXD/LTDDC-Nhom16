package com.example.rentedapp.ui.contract;

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
import com.example.rentedapp.data.model.Contract;
import com.example.rentedapp.data.network.ApiClient;
import com.example.rentedapp.data.network.ApiService;
import com.example.rentedapp.data.network.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ContractsActivity extends AppCompatActivity implements ContractAdapter.OnContractClickListener {

    private RecyclerView rvContracts;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private ContractAdapter adapter;
    private List<Contract> contractList = new ArrayList<>();
    private ApiService apiService;
    private AuthManager authManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contracts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvContracts = findViewById(R.id.rvContracts);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvContracts.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);
        authManager = new AuthManager(this);

        adapter = new ContractAdapter(contractList, authManager.getUserId(), this);
        rvContracts.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContracts();
    }

    private void loadContracts() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        rvContracts.setVisibility(View.GONE);

        apiService.getContracts(null).enqueue(new Callback<ApiResponse<List<Contract>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Contract>>> call, Response<ApiResponse<List<Contract>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    contractList.clear();
                    if (response.body().getData() != null) {
                        contractList.addAll(response.body().getData());
                    }

                    if (contractList.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvContracts.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvContracts.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(ContractsActivity.this, "Không thể tải danh sách hợp đồng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Contract>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ContractsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onContractClick(Contract contract) {
        Intent intent = new Intent(this, ContractDetailActivity.class);
        intent.putExtra("contract_id", contract.getId());
        startActivity(intent);
    }
}
