package eradev.bitcoin.mining.data.local;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class App extends Application {

    public static App instance;

    private BitcoinMiningDB bitcoinMiningDB;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        RoomDatabase.Callback callback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        };

        bitcoinMiningDB = Room.databaseBuilder(this, BitcoinMiningDB.class, BitcoinMiningDB.NAME).addCallback(callback)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

    }

    public static App getInstance() {
        return instance;
    }

    public BitcoinMiningDB getDatabase() {
        return bitcoinMiningDB;
    }
}