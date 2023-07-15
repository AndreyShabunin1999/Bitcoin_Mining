package eradev.bitcoin.mining.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button startBtn, takeBtcBtn;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();

        if(getUserStartMining() == 0){
            startBtn.setText(R.string.text_start_btn);
        } else {
            startBtn.setText(R.string.text_after_start_btn);
        }

        startBtn.setOnClickListener(v -> {
            startBtn.setText(R.string.text_after_start_btn);

            ApiService apiService = Servicey.getPromoMinerApi();

            Call<StatusMessage> startMiningCall = apiService
                    .startMining(getEmailUser());

            startMiningCall.enqueue(new Callback<StatusMessage>() {
                @Override
                public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                    if(response.isSuccessful()){
                        assert response.body() != null;
                        if(response.body().getSuccess() != 0){
                            Toast.makeText(MainActivity.this, R.string.text_start_mining, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.text_start_mining, Toast.LENGTH_SHORT).show();
                            startBtn.setText(R.string.text_start_btn);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {
                    Toast.makeText(MainActivity.this, R.string.text_start_mining, Toast.LENGTH_SHORT).show();
                    startBtn.setText(R.string.text_start_btn);
                }
            });
        });

        takeBtcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QuestsActivity.class));
            }
        });
    }

    //инициализация элементов графического интерфейса
    void initView(){
        startBtn = findViewById(R.id.startBtn);
        takeBtcBtn = findViewById(R.id.btn_take_btc);
    }

    //Получение Email пользователя
    private String getEmailUser(){
        final String[] email = {""};
        BitcoinMiningDB db = App.getInstance().getDatabase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> handler.post(() -> email[0] = db.userDAO().getEmailUser(0)));
        return email[0];
    }

    //Получение информации о том, нажимал ли пользователь на кнопку "Start"
    private Integer getUserStartMining(){
        AtomicInteger isStartMining = new AtomicInteger();
        BitcoinMiningDB db = App.getInstance().getDatabase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> handler.post(() ->
                isStartMining.set(db.userDAO().getUserMiningIsStarted(0))));
        return isStartMining.get();
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