package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.StartScreenEntity;
import eradev.bitcoin.mining.databinding.ActivityAboutTheProgramBinding;

public class AboutTheProgramActivity extends AppCompatActivity {

    private ActivityAboutTheProgramBinding binding;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_program);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Получаю данные из интента
        Intent intent = getIntent();

        boolean userCreate = intent.getBooleanExtra("USER_CREATE", false);

        //Загрузка данных в dataBinding
        loadDataBinding();

        //Обработка кнопки Продолжить
        binding.btnNext.setOnClickListener(v -> {
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

    //Загрузка данных в DataBinding
    private void loadDataBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_the_program);

        BitcoinMiningDB db = App.getInstance().getDatabase();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> handler.post(() -> {
            StartScreenEntity startScreenEntity = db.startScreenDAO().getInfoStartScreen(0);
            binding.setStartScreen(startScreenEntity);
        }));
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