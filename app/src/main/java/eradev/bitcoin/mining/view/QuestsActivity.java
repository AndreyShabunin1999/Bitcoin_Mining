package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;

import eradev.bitcoin.mining.AdapterQuest;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.QuestEntity;
import eradev.bitcoin.mining.databinding.RecyclerViewRowQuestBinding;

public class QuestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_quest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<QuestEntity> quests = getQuestList();
        AdapterQuest adapterQuest = new AdapterQuest(quests);
        recyclerView.setAdapter(adapterQuest);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private List<QuestEntity> getQuestList(){
        BitcoinMiningDB db = App.getInstance().getDatabase();
        List<QuestEntity> questEntity = db.questDAO().getQuest();
        Log.e("QUEST", questEntity.get(0).getText());
        Log.e("QUEST SIZE", String.valueOf(questEntity.size()));
        return questEntity;
    }
}