package eradev.bitcoin.mining.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import eradev.bitcoin.mining.data.local.entity.QuestEntity;

@Dao
public interface QuestDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(QuestEntity questEntity);

    @Query("SELECT * FROM QuestEntity")
    List<QuestEntity> getQuest();
}
