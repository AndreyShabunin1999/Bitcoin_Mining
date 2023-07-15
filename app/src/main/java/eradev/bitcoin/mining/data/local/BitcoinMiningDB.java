package eradev.bitcoin.mining.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import eradev.bitcoin.mining.data.local.dao.ConfigAppDAO;
import eradev.bitcoin.mining.data.local.dao.QuestDAO;
import eradev.bitcoin.mining.data.local.dao.StartScreenDAO;
import eradev.bitcoin.mining.data.local.dao.UnityAdsDAO;
import eradev.bitcoin.mining.data.local.dao.UserDAO;
import eradev.bitcoin.mining.data.local.entity.ConfigAppEntity;
import eradev.bitcoin.mining.data.local.entity.QuestEntity;
import eradev.bitcoin.mining.data.local.entity.StartScreenEntity;
import eradev.bitcoin.mining.data.local.entity.AdsEntity;
import eradev.bitcoin.mining.data.local.entity.UserEntity;

@Database(entities = {UserEntity.class, StartScreenEntity.class, ConfigAppEntity.class, QuestEntity.class, AdsEntity.class}, version = 12)
public abstract class BitcoinMiningDB extends RoomDatabase {
    public abstract UserDAO userDAO();

    public abstract StartScreenDAO startScreenDAO();

    public abstract ConfigAppDAO configAppDao();

    public abstract QuestDAO questDAO();

    public abstract UnityAdsDAO unityAdsDAO();
    public static final String NAME = "BitcoinMiningDB";
}
