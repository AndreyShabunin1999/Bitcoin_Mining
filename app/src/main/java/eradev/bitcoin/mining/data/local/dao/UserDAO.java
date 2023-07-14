package eradev.bitcoin.mining.data.local.dao;

import android.app.SearchManager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import eradev.bitcoin.mining.data.local.entity.UserEntity;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("SELECT * FROM User WHERE id = :id")
    UserEntity getUser(int id);

    @Query("UPDATE User SET email = :email," +
            "entered_code = :entered_code," +
            "ref_code = :ref_code," +
            "ref_value = :ref_value," +
            "server_time = :server_time," +
            "value = :value WHERE id = :id")
    void updateUser(int id, String email, String entered_code, String ref_code, Integer ref_value, String server_time, Integer value);
}