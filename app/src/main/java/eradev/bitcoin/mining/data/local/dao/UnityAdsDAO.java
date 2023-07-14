package eradev.bitcoin.mining.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import eradev.bitcoin.mining.data.local.entity.AdsEntity;

@Dao
public interface UnityAdsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AdsEntity adsEntity);
}
