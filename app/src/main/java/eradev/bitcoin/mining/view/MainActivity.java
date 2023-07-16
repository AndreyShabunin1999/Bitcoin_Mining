package eradev.bitcoin.mining.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.UserEntity;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button startBtn, takeBtcBtn, boostBtn;

    TextView tv_conclusion_balance, tv_duplicate_progress;
    ActivityMainBinding binding;

    Dialog dialog;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        BitcoinMiningDB db = App.getInstance().getDatabase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> handler.post(() -> {
            binding.setUser(db.userDAO().getUser(0));
        }));

        initView();

        startBtn.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            v.startAnimation(scale);

            startBtn.setText(R.string.text_after_start_btn);

            tv_conclusion_balance.setText(R.string.text_progress_string_up);
            tv_duplicate_progress.setText(R.string.text_conclusion_balance_up);

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

        dialog = new Dialog(MainActivity.this);

        //Обработка нажатия кнопки
        boostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        //Обработка нажатия кнопки "Take BTC"
        takeBtcBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuestsActivity.class)));
    }

    private void showCustomDialog(){
        dialog.setContentView(R.layout.custom_dialog_boost);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_ok = dialog.findViewById(R.id.btn_ok);

        ImageView imgCross = dialog.findViewById(R.id.img_cross);

        //Обработка нажатия на кнопку "ОК"
        btn_ok.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Ты нажал на кнопку ОК", Toast.LENGTH_SHORT).show());

        //Обработка нажатия на крестик (закрытие диалогового окна)
        imgCross.setOnClickListener(v -> dialog.cancel());

        dialog.show();
    }

    //инициализация элементов графического интерфейса
    void initView(){
        startBtn = findViewById(R.id.startBtn);
        takeBtcBtn = findViewById(R.id.btn_take_btc);
        tv_conclusion_balance = findViewById(R.id.tv_conclusion_balance_up);
        tv_duplicate_progress = findViewById(R.id.tv_duplicate_progress);
        boostBtn = findViewById(R.id.btn_boost);
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