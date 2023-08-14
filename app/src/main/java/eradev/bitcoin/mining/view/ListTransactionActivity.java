package eradev.bitcoin.mining.view;

import static com.unity3d.services.core.properties.ClientProperties.getApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import eradev.bitcoin.mining.AdapterReferal;
import eradev.bitcoin.mining.AdapterTransaction;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.DepositCodes;
import eradev.bitcoin.mining.data.remote.models.Promocode;
import eradev.bitcoin.mining.data.remote.models.Referal;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTransactionActivity extends AppCompatActivity {

    AdapterTransaction adapterTransaction;
    RecyclerView recyclerView;

    List<Promocode> promocodesList = new ArrayList<>();

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaction);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button btnBack = findViewById(R.id.btn_back_list_trans);
        ImageView imgShareMV = findViewById(R.id.img_share_mixer_wallet);
        recyclerView = findViewById(R.id.recyclerView_transaction);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Получение списка промокодов
        getPromocodesList();

        //Обработка нажатия на кнопку <-
        btnBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        //Обработка нажатия на иконку для скачивания Mixer Wallet
        imgShareMV.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.share_mixer_wallet)))));

    }

    private void getPromocodesList(){
        ApiService apiService = Servicey.getPromoMinerApi();

        Call<DepositCodes> depositCodesCall = apiService
                .getDepCodes(getIntent().getStringExtra("email"));

        depositCodesCall.enqueue(new Callback<DepositCodes>() {
            @Override
            public void onResponse(@NonNull Call<DepositCodes> call, @NonNull Response<DepositCodes> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        promocodesList = response.body().getPromocodes();
                        adapterTransaction = new AdapterTransaction(ListTransactionActivity.this, promocodesList);
                        recyclerView.setAdapter(adapterTransaction);
                        recyclerView.addItemDecoration(new DividerItemDecoration(ListTransactionActivity.this, DividerItemDecoration.HORIZONTAL));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DepositCodes> call, @NonNull Throwable t) {}
        });
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}