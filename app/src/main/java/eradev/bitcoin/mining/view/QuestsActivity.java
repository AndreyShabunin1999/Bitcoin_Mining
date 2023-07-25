package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eradev.bitcoin.mining.AdapterQuest;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.ConfigAppEntity;
import eradev.bitcoin.mining.data.local.entity.QuestEntity;
import eradev.bitcoin.mining.data.local.entity.UserEntity;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.databinding.RecyclerViewRowQuestBinding;
import retrofit2.Call;

public class QuestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_quest);
        Button btnBack = findViewById(R.id.btn_back);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterQuest adapterQuest = new AdapterQuest(getQuestList());
        recyclerView.setAdapter(adapterQuest);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Обработка нажатия клавиши назад
        btnBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    private List<QuestEntity> getQuestList(){
        BitcoinMiningDB db = App.getInstance().getDatabase();
        List<QuestEntity> questEntity = db.questDAO().getQuest();
        ConfigAppEntity configApp = db.configAppDao().getInfoApp();
        //Проверка для ежедневного задания
        if(checkVisibleDailyQuest(db)){
            QuestEntity questEveryDay = new QuestEntity(questEntity.size(), getResources().getString(R.string.text_title_evr_quest),
                    Integer.parseInt(configApp.getDailyBonus()), Integer.parseInt(configApp.getDailyWeight()), getResources().getString(R.string.text_description_evr_quest), "",
                    getResources().getString(R.string.text_type_evr_quest),false, getResources().getString(R.string.text_code_quest));
            questEntity.add(questEveryDay);
        }
        QuestEntity questReferal = new QuestEntity(questEntity.size(),getResources().getString(R.string.text_title_ref_quest),
                Integer.parseInt(configApp.getReferalBonus()), Integer.parseInt(configApp.getReferalWeight()), getResources().getString(R.string.text_description_ref_quest), "",
                getResources().getString(R.string.text_type_ref_quest),true, getResources().getString(R.string.text_code_quest));
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
    private boolean checkVisibleDailyQuest(BitcoinMiningDB db){
        UserEntity user = db.userDAO().getUser(0);
        //Текущая дата
        Date date2 = Calendar.getInstance().getTime();
        //дата с сервера
        Date date1 = null;
        try {
            if(!user.getDaily().equals("")){
                date1 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).parse(user.getDaily());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert date1 != null;
        Log.e("DATE NOW", String.valueOf(date2));
        Log.e("DATE SERVER DAILY", String.valueOf(date1));
        //-3 из-за разницы во времени с сервером
        long diffHours = ((date2.getTime() - date1.getTime()) / (60 * 60 * 1000)) - 3;
        Log.e("TIME", String.valueOf(diffHours));
        //Если больше 24 часов, то добавляем в список ежедневное задание
        return diffHours > 24;
    }


}