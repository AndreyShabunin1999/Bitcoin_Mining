package eradev.bitcoin.mining.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.AdsEntity;
import eradev.bitcoin.mining.data.local.entity.ConfigAppEntity;
import eradev.bitcoin.mining.data.local.entity.QuestEntity;
import eradev.bitcoin.mining.data.local.entity.StartScreenEntity;
import eradev.bitcoin.mining.data.local.entity.UserEntity;
import eradev.bitcoin.mining.data.remote.models.ConfigAppModel;
import eradev.bitcoin.mining.data.remote.models.QuestsModel;
import eradev.bitcoin.mining.data.remote.models.StartScreenModel;
import eradev.bitcoin.mining.data.remote.models.UnityAdsModel;
import eradev.bitcoin.mining.data.remote.models.User;
import eradev.bitcoin.mining.data.remote.models.Users;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.data.remote.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    //массив с данными о загруженных конфигах
    boolean[] ready = new boolean[] {false, false, false, false};

    //Переменная для показа/скрытия экрана о программе
    boolean visibleStartScreen = true;

    BitcoinMiningDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        db = App.getInstance().getDatabase();
        dismissSplashScreen();
    }

    //Вызов функций для получения конфигов
    private void getConfigs() {
        ApiService apiService = Servicey.getPromoMinerApi();
        String language = Locale.getDefault().getLanguage();
        getConfigApp(apiService, language);
        getConfigAds(apiService, language);
        getConfigQuests(apiService, language);
        getConfigInfoStartScreen(apiService, language);
    }

    private void getConfigInfoStartScreen(ApiService apiService, String language){
        Call<StartScreenModel> startScreenResponseCall = apiService
                .startScreen(language);

        startScreenResponseCall.enqueue(new Callback<StartScreenModel>() {
            @Override
            public void onResponse(@NonNull Call<StartScreenModel> call, @NonNull Response<StartScreenModel> response) {
                switch (response.code()){
                    case 200:
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executorService.execute(() -> handler.post(() -> {
                            assert response.body() != null;
                            db.startScreenDAO().insert(new StartScreenEntity(response.body()));
                        }));
                        ready[0] = true;
                        assert response.body() != null;
                        visibleStartScreen = response.body().getNeedToShow();
                        checkDownload();
                        break;
                    case 404:
                        getConfigInfoStartScreen(apiService, "default");
                        break;
                }
            }
            @Override
            public void onFailure(@NonNull Call<StartScreenModel> call, @NonNull Throwable t) {

            }
        });
    }

    public void getConfigQuests(ApiService apiService, String language){
        Call<QuestsModel> questsResponseCall = apiService
                .quests(language);

        questsResponseCall.enqueue(new Callback<QuestsModel>() {
            @Override
            public void onResponse(@NonNull Call<QuestsModel> call, @NonNull Response<QuestsModel> response) {
                switch (response.code()){
                    case 200:
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executorService.execute(() -> handler.post(() -> {
                            for(int i = 0; i < Objects.requireNonNull(response.body()).getQuests().size(); i++){
                                db.questDAO().insert(new QuestEntity(response.body().getQuests().get(i), i));
                            }
                        }));
                        ready[1] = true;
                        checkDownload();
                        break;
                    case 404:
                        getConfigQuests(apiService, "default");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuestsModel> call, @NonNull Throwable t) {
            }
        });
    }

    //Получение конфига приложения
    public void getConfigApp(ApiService apiService, String language){
        Call<ConfigAppModel> configAppModelCall = apiService
                .configApp(language);

        configAppModelCall.enqueue(new Callback<ConfigAppModel>() {
            @Override
            public void onResponse(@NonNull Call<ConfigAppModel> call, @NonNull Response<ConfigAppModel> response) {
                switch (response.code()){
                    case 200:
                        assert response.body() != null;
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executorService.execute(() -> handler.post(() -> {
                            ConfigAppEntity configAppEntity = new ConfigAppEntity(response.body());
                            db.configAppDao().insert(configAppEntity);
                        }));
                        ready[2] = true;
                        checkDownload();
                        break;
                    case 404:
                        getConfigApp(apiService, "default");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConfigAppModel> call, @NonNull Throwable t) {
            }
        });
    }

    //Получение конфига рекламы Unity
    private void getConfigAds(ApiService apiService, String language){
        Call<UnityAdsModel> responseCall = apiService
                .ads(language);

        responseCall.enqueue(new Callback<UnityAdsModel>() {
            @Override
            public void onResponse(@NonNull Call<UnityAdsModel> call, @NonNull Response<UnityAdsModel> response) {
                switch (response.code()){
                    case 200:
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executorService.execute(() -> handler.post(() -> {
                            assert response.body() != null;
                            AdsEntity adsEntity = new AdsEntity(response.body());
                            db.unityAdsDAO().insert(adsEntity);
                        }));
                        ready[3] = true;
                        checkDownload();
                        break;
                    case 404:
                        getConfigAds(apiService, "default");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<UnityAdsModel> call, @NonNull Throwable t) {
            }
        });
    }

    private void getInfoUserFromDB() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> handler.post(() -> {
            UserEntity user = db.userDAO().getUser(0);
            //Если в БД существует пользователь, то выполняется запрос на наличие данных на сервере
            if(user != null) {
                Log.e("USER", user.getEmail());
                updateUserDB(user);
            } else {
                createIntent(false);
            }
        }));
    }

    private void updateUserDB(User user) {
        BitcoinMiningDB db = App.getInstance().getDatabase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> handler.post(() -> db.userDAO().updateUser(0, user.getEmail(),
                user.getEntered_code(), user.getRef_code(), user.getRef_value(), user.getServer_time(),user.getValue())));
    }

    public void updateUserDB(UserEntity user){
        ApiService apiService = Servicey.getPromoMinerApi();
        Call<Users> usersCall;
        //Способ регистрации пользователя (без password через gmail
        if(user.getPassword().isEmpty()){
            usersCall = apiService
                    .getUserGoogle(user.getEmail());
        } else {
            usersCall = apiService
                    .getUser(user.getEmail(), user.getPassword());
        }

        usersCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    Log.e("FINAL", "УСПЕХ");
                    //обновляем данные о пользователе в БД
                    updateUserDB(response.body().getUsers().get(0));
                    // Если пользователь есть на сервере
                    createIntent(response.body().getSuccess() == 1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {

            }
        });
    }

    private void dismissSplashScreen() {
        Handler handler = new Handler();
        //Получение конфигов
        handler.postDelayed(this::getConfigs, 3000);
    }

    private void createIntent(boolean isCreateUser){
        Intent intent;
        if(visibleStartScreen){
            intent = new Intent(SplashScreen.this, AboutTheProgramActivity.class);
            intent.putExtra("USER_CREATE", isCreateUser);
        } else {
            if(isCreateUser){
                intent = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                intent = new Intent(SplashScreen.this, AuthActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

    private void checkDownload(){
        //если данные загружены
        if(ready[0] && ready[1] && ready[2] && ready[3]){
            getInfoUserFromDB();
        }
    }
}