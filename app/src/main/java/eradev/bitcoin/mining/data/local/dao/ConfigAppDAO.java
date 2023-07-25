package eradev.bitcoin.mining.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import eradev.bitcoin.mining.data.local.entity.ConfigAppEntity;
@Dao
public interface ConfigAppDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ConfigAppEntity configAppEntity);

    @Query("SELECT * FROM ConfigApp LIMIT 1")
    ConfigAppEntity getInfoApp();
}
