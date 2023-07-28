package eradev.bitcoin.mining.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Button;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import eradev.bitcoin.mining.AdapterQuest;
import eradev.bitcoin.mining.QuestInterface;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.ConfigAppEntity;
import eradev.bitcoin.mining.data.local.entity.QuestEntity;
import eradev.bitcoin.mining.data.local.entity.UserEntity;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.QuestsModel;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.models.Users;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestsActivity extends AppCompatActivity implements QuestInterface {

    SharedPreferences sharedPreferences;
    BitcoinMiningDB db;
    AdapterQuest adapterQuest;
    ApiService apiService;
    UserEntity user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_quest);
        Button btnBack = findViewById(R.id.btn_back);
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        apiService = Servicey.getPromoMinerApi();

        db = App.getInstance().getDatabase();
        user = db.userDAO().getUser(0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapterQuest = new AdapterQuest(QuestsActivity.this, getQuestList());
        recyclerView.setAdapter(adapterQuest);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        //Обработка нажатия клавиши назад
        btnBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    private List<QuestEntity> getQuestList(){
        List<QuestEntity> questEntity = db.questDAO().getQuest();
        ConfigAppEntity configApp = db.configAppDao().getInfoApp();
        UserEntity user = db.userDAO().getUser(0);
        ListIterator<QuestEntity> iterator = questEntity.listIterator();
        if(!(user.getTask() == null)){
            while (iterator.hasNext())
            {
                QuestEntity quest = iterator.next();
                if(!(quest.getRepeat()) && (user.getTask().contains(quest.getCode()))){
                    iterator.remove();
                }
            }
        }
        //Проверка для ежедневного задания
        if(checkVisibleDailyQuest()){
            QuestEntity questEveryDay = new QuestEntity(questEntity.size(), getResources().getString(R.string.text_title_evr_quest),
                    Integer.parseInt(configApp.getDailyBonus()), Integer.parseInt(configApp.getDailyWeight()), getResources().getString(R.string.text_description_evr_quest), "",
                    getResources().getString(R.string.text_code_quest_daily),false, getResources().getString(R.string.text_code_quest_daily));
            questEntity.add(questEveryDay);
        }
        QuestEntity questReferal = new QuestEntity(questEntity.size(),getResources().getString(R.string.text_title_ref_quest),
                Integer.parseInt(configApp.getReferalBonus()), Integer.parseInt(configApp.getReferalWeight()), getResources().getString(R.string.text_description_ref_quest), "",
                getResources().getString(R.string.text_code_quest_referal),true, getResources().getString(R.string.text_code_quest_referal));
        questEntity.add(questReferal);
        return sort(questEntity);
    }

    //Сортировка списка по весу
    private List<QuestEntity> sort(List<QuestEntity> questEntity){
        boolean needIteration = true;
        while (needIteration) {
            needIteration = false;
            for(int i = 1; i < questEntity.size(); i++){
                if(questEntity.get(i).getWeight() < questEntity.get(i - 1).getWeight()){
                    QuestEntity tmp = questEntity.get(i);
                    questEntity.set(i, questEntity.get(i-1));
                    questEntity.set(i - 1, tmp);
                    needIteration = true;
                }
            }
        }
        return questEntity;
    }

    //Проверка на добавление ежедневного бонуса в список
    private boolean checkVisibleDailyQuest(){
        //Текущая дата
        Date date2 = Calendar.getInstance().getTime();
        //дата с сервера
        Date date1 = null;
        try {
            if(!user.getDaily().equals("")){
                date1 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).parse(user.getDaily());
            } else {
                return true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert date1 != null;
        //-3 из-за разницы во времени с сервером
        long diffHours = ((date2.getTime() - date1.getTime()) / (60 * 60 * 1000)) - 3;
        //Если больше 24 часов, то добавляем в список ежедневное задание
        return diffHours > 24;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Дата начала выполнения задания
        long startQuest = sharedPreferences.getLong("dateStartQuest", 0L);
        if(!(startQuest == 0L)){
            Date dateStartQuest = new Date(startQuest);
            //текущее время
            Date dateNow = new Date();
            //Расчет разницы в секундах
            long diffSeconds = (((dateNow.getTime() - dateStartQuest.getTime()) / 1000));
            //Если пользователь провел больше 10 секунд в задании зачисляем Satoshi
            if(diffSeconds > 10){
                sendTask();
            }
        }
    }

    private void sendTask(){
        String newTasks = user.getTask() + sharedPreferences.getString("currentTask", "");
        Call<StatusMessage> tasksCall = apiService
                .sendTasks(user.getEmail(), newTasks);
        tasksCall.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        int number = sharedPreferences.getInt("currentNumber", 0);
                        if(number != 0){
                            updateBalanceUser(number);
                        }
                        if(!sharedPreferences.getBoolean("repeat", false)){
                            List<QuestEntity> quest = adapterQuest.getData();
                            quest.remove(sharedPreferences.getInt("positionCurrentNumber", 0));
                            adapterQuest.setData(quest);
                            updateTaskUser(newTasks);
                            adapterQuest.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {}
        });
    }

    private void updateBalanceUser(int number){
        int newBalance = user.getValue() + number;
        Call<StatusMessage> balanceCall = apiService
                .sendBalance(user.getEmail(), newBalance);
        balanceCall.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        //обновление баланса в БД
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executorService.execute(() -> handler.post(() -> db.userDAO().updateBalanceFromUser(0,newBalance)));
                        clearSharedPref();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {}
        });
    }

    private void clearSharedPref(){
        sharedPreferences.edit().remove("dateStartQuest").apply();
        sharedPreferences.edit().remove("currentTask").apply();
        sharedPreferences.edit().remove("currentNumber").apply();
        sharedPreferences.edit().remove("positionCurrentNumber").apply();
        sharedPreferences.edit().remove("repeat").apply();
    }

    @Override
    public void startQuest(String codeTask, int number, int position, boolean repeat) {
        //текущее время(время начала выполнения задания)
        Date dateStartQuest = new Date();
        sharedPreferences.edit().putLong("dateStartQuest", dateStartQuest.getTime()).apply();
        sharedPreferences.edit().putString("currentTask", String.format(getResources().getString(R.string.text_delimiter), codeTask)).apply();
        sharedPreferences.edit().putInt("currentNumber", number).apply();
        sharedPreferences.edit().putInt("positionCurrentNumber", position).apply();
        sharedPreferences.edit().putBoolean("repeat", repeat).apply();
    }


    //Сохранение данных о задачах в бд
    public void updateTaskUser(String code){
        if(getResources().getString(R.string.text_code_quest_daily).equals(code)){
            Call<QuestsModel> userCall;
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
                        //обновляем данные о пользователе в БД
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executorService.execute(() -> handler.post(() -> {
                            db.userDAO().updateDailyFromUser(0, response.body().getUsers().get(0).getDaily());
                        }));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {}
            });
        } else {
            db.userDAO().updateTasksFromUser(0, code);
        }
    }
}



