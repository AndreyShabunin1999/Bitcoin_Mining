package eradev.bitcoin.mining.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.ConfigAppEntity;
import eradev.bitcoin.mining.data.local.entity.UserEntity;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.models.Users;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.databinding.ActivityMainBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements IUnityAdsInitializationListener{

    private Button startBtn, takeBtcBtn, boostBtn;
    private TextView tv_conclusion_balance, tv_duplicate_progress, pingServer1, pingServer2, pingServer3, pingServer4,
            textPingServer1, msServer1, textPingServer2, msServer2, textPingServer3, msServer3,
            textPingServer4, msServer4, smallTextBalance;
    private ImageView imgStickServer1,imgStickServer2,imgStickServer3,imgStickServer4, imgMetricServer1, imgMetricServer2, imgMetricServer3, imgMetricServer4;
    private ActivityMainBinding binding;
    private RelativeLayout relativeLayout_server1, relativeLayout_server2, relativeLayout_server3, relativeLayout_server4;
    //Для различия за что вознаграждение true - boost, false - смена сервера
    private boolean rewardAfterAds;
    private int clickServer;
    private Dialog dialog;
    SharedPreferences sharedPreferences;
    BitcoinMiningDB db;
    ApiService apiService;

    private final String unityGameID = "5350565";
    private final Boolean testMode = true;
    private final String adUnitId = "Rewarded_Android";
    private int ping;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            UnityAds.show(MainActivity.this, adUnitId, new UnityAdsShowOptions(), showListener);
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
        }
    };

    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowStart: " + placementId);
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowClick: " + placementId);
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.v("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);
            if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                if(rewardAfterAds){
                    //проверка на время буста (не прошло ли 8 часов)
                    checkTimeBoost();
                    //расчет нового значения буста
                    getAcceleration();
                    sendServerBoost();
                } else {
                    //пересчет пинга
                    ping = calculatePing();
                    //отключаем текущий сервер
                    settingViewServer(false);
                    sharedPreferences.edit().putInt("currentServer", clickServer).apply();
                    //включение нового сервера
                    settingViewServer(true);
                }
            }
        }
    };

    private void checkTimeBoost(){
        Call<Users> boostCall;
        if(Objects.equals(binding.getUser().getPassword(), "")){
            boostCall = apiService.getUserGoogle(binding.getUser().getEmail());
        } else {
            boostCall = apiService.getUser(binding.getUser().getEmail(), binding.getUser().getPassword());
        }
        boostCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        Date date2 = Calendar.getInstance().getTime();
                        Date date1 = null;
                        try {
                            date1 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).parse(response.body().getUsers().get(0).getBoost());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        assert date1 != null;
                        Log.e("DATE2", String.valueOf(date2));
                        Log.e("DATE1", String.valueOf(date1));
                        //-3 из-за разницы во времени с сервером
                        long diffHours = ((date2.getTime() - date1.getTime()) / (60 * 60 * 1000)) - 3;
                        Log.e("TIME", String.valueOf(diffHours));
                        //Если Boost больше 8 часов возращаем сбрасываем значения буста до дефолтных
                        if(diffHours > 8) {
                            SharedPreferences preferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                            preferences.edit().remove("minBoost").apply();
                            preferences.edit().remove("maxBoost").apply();
                            preferences.edit().remove("miningPerMinute").apply();
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {}
        });
    }

    private int calculateRandom(int min, int max){
        Random random = new Random();
        return random.nextInt(max + 1 - min) + min;
    }

    private void getAcceleration() {
        int boost = sharedPreferences.getInt("boost", 1);
        //Если буст не активирован запрашиваем значения из БД
        if(boost == 1){
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executorService.execute(() -> handler.post(() -> {
                ConfigAppEntity configApp = db.configAppDao().getInfoApp(0);
                int maxBoost = Integer.parseInt(configApp.getMaxBoost());
                int minBoost = Integer.parseInt(configApp.getMinBoost());
                int acceleration = Integer.parseInt(configApp.getMiningPerMinute());
                saveValueSP(minBoost, maxBoost, acceleration);
            }));
        } else {
            int minBoost = sharedPreferences.getInt("minBoost", 3);
            int maxBoost = sharedPreferences.getInt("maxBoost", 6);
            int acceleration = sharedPreferences.getInt("boost", 1);
            saveValueSP(minBoost, maxBoost, acceleration);
        }
    }

    private void saveValueSP(int minBoost, int maxBoost, int acceleration){
        //Сохранение новых значений
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        minBoost = (int) (maxBoost * 0.9);
        maxBoost = (int) (maxBoost * 0.9);
        //Значение минимального буста не может быть меньше 3
        if(minBoost > 3){
            myEdit.putInt("minBoost", minBoost);
        }
        //Значение максимального буста не может быть меньше 6
        if(maxBoost > 6){
            myEdit.putInt("minBoost", maxBoost);
        }
        myEdit.putInt("boost", calculateRandom(minBoost, maxBoost) + acceleration);
        myEdit.apply();
    }

    //Функция расчета пинга
    private int calculatePing(){
        ArrayList<Integer> pingList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            pingList.add(calculateRandom(1, 50));
        }
        Collections.sort(pingList);
        return pingList.get(1);
    }

    private void changeViewCard(TextView textPingServer, TextView valuePingServer, TextView msServer, RelativeLayout relativeLayoutServer,
                                ImageView imgStickServer, ImageView imgMetricServer, boolean active){
        if(active){
            textPingServer.setTextColor(ContextCompat.getColor(this, R.color.color_text_ping));
            valuePingServer.setText(String.valueOf(ping));
            valuePingServer.setTextColor(ContextCompat.getColor(this, R.color.white));
            msServer.setTextColor(ContextCompat.getColor(this, R.color.turquoise_blue));
            imgMetricServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.metric_active));
            imgStickServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.stick_active));
            imgStickServer.setRotation((float) (180 - (ping * 3.6)));
            relativeLayoutServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.gradient_active_card));
        } else {
            textPingServer.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            valuePingServer.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            msServer.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            imgMetricServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.metric_disable));
            imgStickServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.stick_disable));
            relativeLayoutServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.gradient_card_server));
        }
    }

    private void settingViewServer(boolean active){
        int currentServer = sharedPreferences.getInt("currentServer", 1);
        switch (currentServer) {
            case 1:
                changeViewCard(textPingServer1, pingServer1, msServer1, relativeLayout_server1, imgStickServer1, imgMetricServer1, active);
                break;
            case 2:
                changeViewCard(textPingServer2, pingServer2, msServer2, relativeLayout_server2, imgStickServer2, imgMetricServer2, active);
                break;
            case 3:
                changeViewCard(textPingServer3, pingServer3, msServer3, relativeLayout_server3, imgStickServer3, imgMetricServer3, active);
                break;
            case 4:
                changeViewCard(textPingServer4, pingServer4, msServer4, relativeLayout_server4, imgStickServer4, imgMetricServer4, active);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //инициализация глобальный переменных
        db = App.getInstance().getDatabase();
        apiService = Servicey.getPromoMinerApi();
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        dialog = new Dialog(MainActivity.this);

        //Загрузка данных в dataBinding
        loadDataBinding();

        //Показ спец предложения
        showDialogSpecOffer();

        //Инициализация элементов интерфейса
        initView();

        // инициализация SDK Unity ADS
        UnityAds.initialize(MainActivity.this, unityGameID, testMode, this);

        //Расчет пинга
        ping = calculatePing();
        //Загрузка серверов
        settingViewServer(true);

        startBtn.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            v.startAnimation(scale);

            startBtn.setText(R.string.text_after_start_btn);

            tv_conclusion_balance.setText(R.string.text_conclusion_balance_up);
            tv_duplicate_progress.setText(R.string.text_progress_string_up);

            Call<StatusMessage> startMiningCall = apiService
                    .startMining("andrushka456@gmail.com");
            startMiningCall.enqueue(new Callback<StatusMessage>() {
                @Override
                public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                    if(response.isSuccessful()){
                        assert response.body() != null;
                        if(response.body().getSuccess() == 1){
                            Toast.makeText(MainActivity.this, R.string.text_start_mining, Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Mining" + binding.getUser().getMining_is_started(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.text_error_start_mining, Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Mining" + binding.getUser().getMining_is_started(), Toast.LENGTH_SHORT).show();
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

        // Переключение серверов
        relativeLayout_server1.setOnClickListener(v -> changeServer(1));
        relativeLayout_server2.setOnClickListener(v -> changeServer(2));
        relativeLayout_server3.setOnClickListener(v -> changeServer(3));
        relativeLayout_server4.setOnClickListener(v -> changeServer(4));

        //Обработка нажатия кнопки
        boostBtn.setOnClickListener(v -> {
            rewardAfterAds = true;
            showCustomDialog(R.layout.custom_dialog_boost);
        });
        //Обработка нажатия кнопки "Take BTC"
        takeBtcBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuestsActivity.class)));
    }

    private void processMining(int isStartMining){
        //Если майнинг запущен запускаем цикл счета майнинга
        if(isStartMining == 1){
            Handler handler = new Handler();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        while(true) {
                            sleep(1000);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    calculateMiningPerMinute();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }
    private void calculateMiningPerMinute(){
        //Извлекаю процент буста (ускорения)
        int boost = sharedPreferences.getInt("boost", 1);
        //Извлечение пинга и его рассчет
        int ping = (50 - sharedPreferences.getInt("ping", 1))/2;
        int zk = 1;
        int balance = sharedPreferences.getInt ("balance", 0);
        int newBalance = 0;
        if(balance == 0){
            balance = binding.getUser().getValue();
            newBalance = binding.getUser().getValue() + (zk * (1 + (boost/100)) * ping);
        } else {
            newBalance = balance + (zk * (1 + (boost/100)) * ping);
        }
        float balanceBtc = ((float) binding.getUser().getValue() / 100000000F);

        //Сохраняем значениее баланса
        sharedPreferences.edit().putInt("balance", newBalance).apply();
        binding.getUser().setValue(newBalance);
        final ValueAnimator animatorSatoshi = ValueAnimator.ofInt(balance, balance += (newBalance - balance));
        animatorSatoshi.addUpdateListener(animation -> binding.bigTextBalance.setText(animatorSatoshi.getAnimatedValue().toString()));
        animatorSatoshi.start();
        //final ValueAnimator animatorBtc = ValueAnimator.ofFloat(balanceBtc, balanceBtc += ((newBalance - balance) / 100000000F));
       // animatorBtc.addUpdateListener(animation -> binding.smallTextBalance.setText(String.format("%.8f",animatorBtc.getAnimatedValue().toString())));
       // animatorBtc.start();
        //updateBalanceUser(newBalance);
    }

    //Смена сервера
    private void changeServer(int numberClickServer){
        rewardAfterAds = false;
        clickServer = numberClickServer;
        showCustomDialog(R.layout.dialog_change_server);
    }

    private void sendServerBoost(){
        Call<StatusMessage> boostCall = apiService
                  .sendBoost(binding.getUser().getEmail());
        boostCall.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        Toast.makeText(MainActivity.this, "Буст успешно отправлен", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Майнинг: " + String.valueOf(binding.getUser().getMining_is_started()), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Буст не отправлен", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Буст не отправлен", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //открытие диалога для просмотра рекламы
    private void showCustomDialog(Integer idLayout){
        dialog.setContentView(idLayout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        ImageView imgCross = dialog.findViewById(R.id.img_cross);
        //Обработка нажатия на кнопку "ОК"
        btn_ok.setOnClickListener(v -> {
            UnityAds.load(adUnitId, loadListener);
            UnityAds.show(MainActivity.this, adUnitId, showListener);
            dialog.cancel();
        });
        //Обработка нажатия на крестик (закрытие диалогового окна)
        imgCross.setOnClickListener(v -> dialog.cancel());
        dialog.show();
    }

    //открытие диалогового окна с специальным предложением
    private void showDialogSpecOffer(){
        dialog.setContentView(R.layout.dialog_referal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnCancellation = dialog.findViewById(R.id.btn_cancellation);
        Button btnLetsGo = dialog.findViewById(R.id.btn_lets_go);
        ImageView blackCross = dialog.findViewById(R.id.img_black_cross);
        //Закртиые диалога по кнопке "остаться"
        btnCancellation.setOnClickListener(v -> dialog.cancel());
        //Закрытие диалога
        blackCross.setOnClickListener(v -> dialog.cancel());
        //Переход на экран рефералов
        btnLetsGo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReferalsActivity.class)));
        dialog.show();
    }

    //Загрузка данных в DataBinding
    private void loadDataBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        db.userDAO().getSingleUser(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<UserEntity>() {
                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        //user = userEntity;
                        binding.setUser(userEntity);
                        checkTimeBoost();
                        //Запуск майнинга
                        processMining(binding.getUser().getMining_is_started());
                    }
                    @Override
                    public void onError(Throwable e) {}
                });
    }

    //инициализация элементов графического интерфейса
    void initView(){
        startBtn = findViewById(R.id.startBtn);
        takeBtcBtn = findViewById(R.id.btn_take_btc);
        tv_conclusion_balance = findViewById(R.id.tv_conclusion_balance_up);
        tv_duplicate_progress = findViewById(R.id.tv_duplicate_progress);
        boostBtn = findViewById(R.id.btn_boost);
        relativeLayout_server1 = findViewById(R.id.relativeLayout_server1);
        relativeLayout_server2 = findViewById(R.id.relativeLayout_server2);
        relativeLayout_server3 = findViewById(R.id.relativeLayout_server3);
        relativeLayout_server4 = findViewById(R.id.relativeLayout_server4);
        pingServer1 = findViewById(R.id.tv_value_ping_server1);
        imgStickServer1 = findViewById(R.id.img_stick_server1);
        pingServer2 = findViewById(R.id.tv_value_ping_server2);
        imgStickServer2 = findViewById(R.id.img_stick_server2);
        pingServer3 = findViewById(R.id.tv_value_ping_server3);
        imgStickServer3 = findViewById(R.id.img_stick_server3);
        pingServer4 = findViewById(R.id.tv_value_ping_server4);
        imgStickServer4 = findViewById(R.id.img_stick_server4);
        textPingServer1 = findViewById(R.id.text_ping_server1);
        textPingServer2 = findViewById(R.id.text_ping_server2);
        textPingServer3 = findViewById(R.id.text_ping_server3);
        textPingServer4 = findViewById(R.id.text_ping_server4);
        pingServer2 = findViewById(R.id.tv_value_ping_server2);
        pingServer3 = findViewById(R.id.tv_value_ping_server3);
        pingServer4 = findViewById(R.id.tv_value_ping_server4);
        msServer1 = findViewById(R.id.tv_ms_server1);
        msServer2 = findViewById(R.id.tv_ms_server2);
        msServer3 = findViewById(R.id.tv_ms_server3);
        msServer4 = findViewById(R.id.tv_ms_server4);
        imgMetricServer1 = findViewById(R.id.img_ping_server1);
        imgMetricServer2 = findViewById(R.id.img_ping_server2);
        imgMetricServer3 = findViewById(R.id.img_ping_server3);
        imgMetricServer4 = findViewById(R.id.img_ping_server4);
        smallTextBalance = findViewById(R.id.small_text_balance);
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
        sharedPreferences.edit().remove("balance").apply();
        super.onStop();
    }

    @Override
    public void onInitializationComplete() {
        Log.e("UnityAdsExample", "Success");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e("UnityAdsExample", "Unity Ads initialization failed with error: [" + error + "] " + message);
    }

}