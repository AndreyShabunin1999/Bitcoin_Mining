package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.StartScreenEntity;

public class AboutTheProgramActivity extends AppCompatActivity {

    Button btnNext;
    TextView tvMain;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_program);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnNext = findViewById(R.id.btn_next);
        tvMain = findViewById(R.id.tv_main_text);

        //Получаю данные из интента
        Intent intent = getIntent();

        boolean userCreate = intent.getBooleanExtra("USER_CREATE", false);

        BitcoinMiningDB db = App.getInstance().getDatabase();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> handler.post(() -> {
            StartScreenEntity startScreenEntity = db.startScreenDAO().getInfoStartScreen(0);
            tvMain.setText(startScreenEntity.getMainText());
            btnNext.setText(startScreenEntity.getButtonText());
        }));

        //Обработка кнопки Продолжить
        btnNext.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            v.startAnimation(scale);
            //если пользователь создан переход на главныый экран, иначе на регистрацию
            if(userCreate){
                startActivity(new Intent(AboutTheProgramActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(AboutTheProgramActivity.this, AuthActivity.class));
                finish();
            }
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