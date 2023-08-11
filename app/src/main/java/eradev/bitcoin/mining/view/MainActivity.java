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
import android.graphics.Point;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private Integer minimalSummToWithdraw;
    private Dialog dialog;
    private boolean miningJob = false;
    SharedPreferences sharedPreferences;
    BitcoinMiningDB db;
    ApiService apiService;
    Thread threadMining;

    private final String unityGameID = "5350565";
    private final Boolean testMode = true;
    private final String adUnitId = "Rewarded_Android";

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
                    //расчет нового значения буста
                    getAcceleration();
                    sendServerBoost();
                } else {
                    sharedPreferences.edit().putInt("currentServer", clickServer).apply();
                    //пересчет пинга
                    calculatePing();
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
                ConfigAppEntity configApp = db.configAppDao().getInfoApp();
                saveValueSP(Integer.parseInt(configApp.getMinBoost()), Integer.parseInt(configApp.getMaxBoost()), boost, Integer.parseInt(configApp.getMiningPerMinute()));
            }));
        } else {
            saveValueSP(sharedPreferences.getInt("minBoost", 3), sharedPreferences.getInt("maxBoost", 6), boost);
        }
    }

    private void saveValueSP(int minBoost, int maxBoost, int currentBoost, int... miningPerMinute){
        //Сохранение новых значений
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        int tempMinBoost = (int) (minBoost * 0.9);
        int tempMaxBoost = (int) (maxBoost * 0.9);

        //Значение минимального буста не может быть меньше 3
        if(minBoost > 3){
            minBoost = tempMinBoost;
            myEdit.putInt("minBoost", minBoost);
        }
        //Значение максимального буста не может быть меньше 6
        if(maxBoost > 6){
            maxBoost = tempMaxBoost;
            myEdit.putInt("maxBoost", maxBoost);
        }

        int newBoost = calculateRandom(minBoost, maxBoost) + currentBoost;
        myEdit.putInt("boost", newBoost);

        boostBtn.setText(String.format(getResources().getString(R.string.text_btn_boost_value), newBoost));

        if(miningPerMinute != null){
            myEdit.putInt("miningPerMinute", miningPerMinute[0]).apply();
        }
        myEdit.apply();
    }

    //Функция расчета пинга
    private void calculatePing(){
        ArrayList<Integer> pingList = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            pingList.add(calculateRandom(1, 50));
        }
        Collections.sort(pingList);
        //Сохраняет значение пинга для текущего сервера
        sharedPreferences.edit().putInt("ping", pingList.get(1)).apply();
        for(int i = 0; i < pingList.size(); i++){
            if(sharedPreferences.getInt("currentServer", 0) == i){
                if(i==1){
                    continue;
                } else {
                    i++;
                    settingViewServer(false, i, pingList.get(i-1));
                    continue;
                }
            }
            //значение с индексом 1 для текущего сервера
            if(i == 1){
                i++;
                settingViewServer(false, i-1, pingList.get(i));
            }
            settingViewServer(false, i, pingList.get(i));
        }
        settingViewServer(true, sharedPreferences.getInt("currentServer", 0), pingList.get(1));
    }

    private void changeViewCard(TextView textPingServer, TextView valuePingServer, TextView msServer, RelativeLayout relativeLayoutServer,
                                ImageView imgStickServer, ImageView imgMetricServer, boolean active, int ping){
        if(active){
            textPingServer.setTextColor(ContextCompat.getColor(this, R.color.color_text_ping));
            valuePingServer.setTextColor(ContextCompat.getColor(this, R.color.white));
            msServer.setTextColor(ContextCompat.getColor(this, R.color.turquoise_blue));
            imgMetricServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.metric_active));
            imgStickServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.stick_active));
            relativeLayoutServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.gradient_active_card));
        } else {
            textPingServer.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            valuePingServer.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            msServer.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            imgMetricServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.metric_disable));
            imgStickServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.stick_disable));
            relativeLayoutServer.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.gradient_card_server));
        }
        valuePingServer.setText(String.valueOf(ping));
        imgStickServer.setRotation((float) (180 - (ping * 3.6)));
    }

    private void settingViewServer(boolean active, int currentServer, int ping){
        switch (currentServer) {
            case 0:
                changeViewCard(textPingServer1, pingServer1, msServer1, relativeLayout_server1, imgStickServer1, imgMetricServer1, active, ping);
                break;
            case 1:
                changeViewCard(textPingServer2, pingServer2, msServer2, relativeLayout_server2, imgStickServer2, imgMetricServer2, active, ping);
                break;
            case 2:
                changeViewCard(textPingServer3, pingServer3, msServer3, relativeLayout_server3, imgStickServer3, imgMetricServer3, active, ping);
                break;
            case 3:
                changeViewCard(textPingServer4, pingServer4, msServer4, relativeLayout_server4, imgStickServer4, imgMetricServer4, active, ping);
                break;
        }
    }

    private void getMinSumToWithdraw(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> handler.post(() -> {
            minimalSummToWithdraw = Integer.parseInt(db.configAppDao().getMinSumToWithdraw());
        }));
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

        //Проверка на прохождение обучения
        if(!sharedPreferences.getBoolean("learning", false)){
            sharedPreferences.edit().putBoolean("learning", true).apply();
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
        }

        dialog = new Dialog(MainActivity.this);
        //Получение минимальной суммы для вывода
        getMinSumToWithdraw();

        //Загрузка данных в dataBinding
        loadDataBinding();

        //Показ спец предложения
        showDialogSpecOffer();

        //Инициализация элементов интерфейса
        initView();

        //Если имеется рабочий буст, то отобразить надпись
        if(sharedPreferences.getInt("boost",1) != 1){
            binding.btnBoost.setText(getResources().getString(R.string.text_btn_boost_value, sharedPreferences.getInt("boost", 1)));
        }

        // инициализация SDK Unity ADS
        UnityAds.initialize(MainActivity.this, unityGameID, testMode, this);

        //Расчет пинга
        calculatePing();

        startBtn.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            v.startAnimation(scale);

            if(binding.getUser().getMining_is_started() == 0){
                startBtn.setText(R.string.text_after_start_btn);
                tv_conclusion_balance.setText(R.string.text_conclusion_balance_up);

                Call<StatusMessage> startMiningCall = apiService
                        .startMining(binding.getUser().getEmail());
                startMiningCall.enqueue(new Callback<StatusMessage>() {
                    @Override
                    public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                        if(response.isSuccessful()){
                            assert response.body() != null;
                            if(response.body().getSuccess() == 1){
                                Log.e("YA TUT", "PRIVET");
                                binding.getUser().setMining_is_started(response.body().getSuccess());
                                miningJob = true;
                                processMining();
                            } else {
                                startBtn.setText(R.string.text_start_btn);
                                tv_conclusion_balance.setText(R.string.text_empty);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {
                        startBtn.setText(R.string.text_start_btn);
                        tv_conclusion_balance.setText(R.string.text_empty);
                    }
                });
            } else {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                intent.putExtra("balance", binding.getUser().getValue());
                intent.putExtra("minimalSummToWithdraw", minimalSummToWithdraw);
                intent.putExtra("email", binding.getUser().getEmail());
                startActivity(intent);
            }
        });

        binding.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
            }
        });

        // Переключение серверов
        relativeLayout_server1.setOnClickListener(v -> changeServer(0));
        relativeLayout_server2.setOnClickListener(v -> changeServer(1));
        relativeLayout_server3.setOnClickListener(v -> changeServer(2));
        relativeLayout_server4.setOnClickListener(v -> changeServer(3));

        //Обработка нажатия кнопки
        boostBtn.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            v.startAnimation(scale);
            rewardAfterAds = true;
            showCustomDialog(R.layout.custom_dialog_boost);
        });
        //Обработка нажатия кнопки "Take BTC"

        takeBtcBtn.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            v.startAnimation(scale);
            startActivity(new Intent(MainActivity.this, QuestsActivity.class));
        });


    }

    private void processMining(){
        //Если майнинг запущен запускаем цикл счета майнинга
        if(binding.getUser().getMining_is_started() == 1){
            //скорость в минуту
            int miningPerMinute = calculateMiningPerMinute();
            float speed = (float) 60/miningPerMinute * 1000;
            Handler handler = new Handler();
            threadMining = new Thread() {
                @Override
                public void run() {
                    try {
                        while(miningJob) {
                            sleep((int) speed);
                            handler.post(() -> {
                                int balance = binding.getUser().getValue();
                                int newBalance = balance + 1;
                                //Сохраняем значениее баланса
                                binding.getUser().setValue(newBalance);
                                //Анимация счета
                                final ValueAnimator animatorBtc = ValueAnimator.ofFloat(balance / 100000000F, newBalance / 100000000F);
                                animatorBtc.addUpdateListener(animation -> binding.smallTextBalance.setText(String.format(getResources().getString(R.string.value_float_btc), (float) animatorBtc.getAnimatedValue())));
                                animatorBtc.start();

                                final ValueAnimator animatorSatoshi = ValueAnimator.ofInt(balance, newBalance);
                                animatorSatoshi.addUpdateListener(animation -> binding.bigTextBalance.setText(animatorSatoshi.getAnimatedValue().toString()));
                                animatorSatoshi.start();

                                Point maxSizePoint = new Point();
                                getWindowManager().getDefaultDisplay().getSize(maxSizePoint);
                                final int maxX = maxSizePoint.x;

                                int progressStatus = ((minimalSummToWithdraw - newBalance) / (minimalSummToWithdraw + newBalance) / 2) * 100;
                                if((newBalance > 1) && (progressStatus < 1)){
                                    progressStatus = 1;
                                }

                                binding.progressBar.setProgress(1);
                                int val = (progressStatus * (binding.progressBar.getWidth() - 2 )) / binding.progressBar.getMax();
                                int textViewX = val - (binding.tvProgress.getWidth() / 2);
                                int finalX = binding.tvProgress.getWidth() + textViewX > maxX ? (maxX - binding.tvProgress.getWidth() - 30) : textViewX + 30;
                                binding.tvProgress.setX(finalX < 0 ? 30 : finalX);
                                binding.tvProgress.setText(getResources().getString(R.string.process_progress,  progressStatus));
                                binding.tvDuplicateProgress.setText(getResources().getString(R.string.process_progress,  progressStatus));
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadMining.start();
        }
    }

    //Функция возвращения значения сатоши в минуту
    private int calculateMiningPerMinute(){
        //Извлекаю процент буста (ускорения)
        int boost = sharedPreferences.getInt("boost", 1);
        //Извлечение пинга и его рассчет
        int ping = (50 - sharedPreferences.getInt("ping", 1))/2;
        int zk = sharedPreferences.getInt("miningPerMinute", 120);
        if(boost != 1){
            return (zk * (1 + (boost/100)) * (1 + ping/100));
        } else {
            return (zk * boost * (1 + ping/100));
        }
    }

    private void updateBalanceUser(int newBalance){
        Call<StatusMessage> balanceCall = apiService
                .sendBalance(binding.getUser().getEmail(), newBalance);

        balanceCall.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        Log.e("BALANCE UPDATE", "БАЛАНС ОБНОВЛЕН");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {
                Log.e("BALANCE UPDATE", "БАЛАНС НЕ ОБНОЛЕН");
            }
        });
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
        Button btnDialog = dialog.findViewById(R.id.btn_dialog);
        ImageView imgCross = dialog.findViewById(R.id.img_cross);
        //Обработка нажатия на кнопку "ОК"
        btnDialog.setOnClickListener(v -> {
            //Вызов просмотра рекламы
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
                        //checkTimeBoost();
                        // Если майнинг прежде запускался
                        Log.e("STARTMINING", String.valueOf(binding.getUser().getMining_is_started()));
                        if(binding.getUser().getMining_is_started() == 1){
                            checkOfflineTimeMining();
                            Log.e("ВСЕ ХОРОШО","");
                            miningJob = true;
                            processMining();
                        }
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
        if(threadMining != null){
            checkOfflineTimeMining();
            miningJob = true;
            processMining();
        }
        super.onStart();
    }

    //Функция
    private void checkOfflineTimeMining() {
        long stopMining = sharedPreferences.getLong("dateStopMining", 0L);
        if(stopMining != 0L){
            Log.e("CHECK", "МАЙНИНГ ЕСТЬ");
            //Дата и времявыхода из приложения
            Date dateStopMining = new Date(stopMining);

            Log.e("dateStopMining", new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).format(dateStopMining));

            //текущая дата и время (когда пользователь вернулся в приложение)
            Date dateRestart = new Date();

            Log.e("dateRestart", new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).format(dateRestart));

            //Расчет разницы в секундах
            long diffSeconds = (dateRestart.getTime() - dateStopMining.getTime())/1000;
            Log.e("diffSeconds", String.valueOf(diffSeconds));
            //старое значение майнинга в минуту
            int miningPerMinute = sharedPreferences.getInt("miningPerMinute", 120);
            Log.e("miningPerMinute", String.valueOf(miningPerMinute));
            //Получение баланса на момент выхода из приложения
            int oldBalance = db.userDAO().getBalanceUser(0);
            Log.e("oldBalance", String.valueOf(oldBalance));

            //Проверка на наличие действующего буста
            if(sharedPreferences.getInt("boost", 1) != 1){

                Log.e("CHECK", "БУСТ ЕСТЬ");

                //Время старта буста
                Date dateStartBoost = null;
                try {
                    dateStartBoost = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).parse(binding.getUser().getBoost());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                assert dateStartBoost != null;

                Log.e("dateStartBoost", new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).format(dateStartBoost));

                //Проверяем на момент окончания буста (если буст активен при возврате
                if(((dateStartBoost.getTime()/1000) + 28800) < (dateRestart.getTime()/1000)){

                    Log.e("CHECK", "БУСТ меньше времени возврата");

                    //Время работы буста в секундах
                    long workSecondsBoost = ((dateStartBoost.getTime() / 1000) + 28800) - (dateStopMining.getTime()/1000);

                    //получаем баланс при работе с бустом
                    int offlineBalanceBoost = (int) (workSecondsBoost * (miningPerMinute/60));

                    //Прибавляем к старому балансу
                    oldBalance = oldBalance + offlineBalanceBoost;

                    long remainsTime = dateRestart.getTime()/1000 - ((dateStartBoost.getTime() / 1000) + 28800);

                    miningPerMinute = (int) (miningPerMinute / (1 + (sharedPreferences.getLong("boost", 0)/100)));

                    //получаем баланс при работе без буста
                    int offlineBalanceNotBoost = (int) (remainsTime * (miningPerMinute/60));

                    binding.getUser().setValue(oldBalance + offlineBalanceNotBoost);

                    sharedPreferences.edit().remove("boost").apply();
                } else {
                    Log.e("CHECK", "БУСТ больше времени возврата");

                    long offlineBalance = (diffSeconds * (miningPerMinute/60));
                    Log.e("offlineBalance", String.valueOf(offlineBalance));
                    binding.getUser().setValue((int) (oldBalance + offlineBalance));
                }
            } else {
                Log.e("CHECK", "БУСТ больше времени возврата");

                long offlineBalance = (diffSeconds * (miningPerMinute/60));
                Log.e("offlineBalance", String.valueOf(offlineBalance));
                binding.getUser().setValue((int) (oldBalance + offlineBalance));
            }
        }
        //Очищаем sharedPreferences
        sharedPreferences.edit().remove("dateStopMining").apply();
        sharedPreferences.edit().remove("miningPerMinute").apply();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        //Обновляем баланс пользователя на сервере
        updateBalanceUser(binding.getUser().getValue());
        //Обновление данных в БД
        db.userDAO().updateBalanceFromUser(0, binding.getUser().getValue());
        //запоминаем дату последнего майнинга при открытом приложении, а также значение скорости в минуту
        Date dateStopMining = new Date();
        sharedPreferences.edit().putLong("dateStopMining", dateStopMining.getTime()).apply();
        sharedPreferences.edit().putInt("miningPerMinute", calculateMiningPerMinute()).apply();
        //останавливаем визуализацию счета Satoshi
        miningJob = false;
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