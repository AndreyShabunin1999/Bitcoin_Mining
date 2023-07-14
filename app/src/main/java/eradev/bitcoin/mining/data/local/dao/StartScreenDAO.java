package eradev.bitcoin.mining.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import eradev.bitcoin.mining.data.local.entity.StartScreenEntity;

@Dao
public interface StartScreenDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StartScreenEntity startScreenEntity);

    @Query("SELECT * FROM StartScreen where id = :id")
    StartScreenEntity getInfoStartScreen(int id);
}
