package eradev.bitcoin.mining.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import eradev.bitcoin.mining.data.remote.models.User;

@Entity(tableName = "User")
public class UserEntity {

    @ColumnInfo(name = "id")
    @PrimaryKey
    Integer id;

    //Почта пользователя
    @ColumnInfo(name="email")
    String email;

    //Пароль пользователя
    @ColumnInfo(name="password")
    String password;

    //введенный реферальный код (если пользователь использовал чей то реферальный код)
    @ColumnInfo(name = "entered_code")
    String entered_code;

    //реферальный код пользователя
    @ColumnInfo(name = "ref_code")
    String ref_code;

    //сколько пользователь получил за рефералов
    @ColumnInfo(name = "ref_value")
    Integer ref_value;

    //текущее время на сервере
    @ColumnInfo(name="server_time")
    String server_time;

    //баланс пользователя
    @ColumnInfo(name = "value")
    Integer value;

    //Нажималась ли кнопка "Start Mining"
    @ColumnInfo(name = "mining_is_started")
    Integer mining_is_started;

    @ColumnInfo(name = "boost")
    String boost;

    @Ignore
    public UserEntity() {

    }

    public UserEntity(String email, String password) {
        this.id = 0;
        this.email = email;
        this.password = password;
        this.entered_code = "";
        this.ref_code = "";
        this.ref_value = 0;
        this.value = 0;
        this.server_time = "";
        this.mining_is_started = 0;
        this.boost = "";
    }

    public UserEntity(User user, String password) {
        this.id = 0;
        this.email = user.getEmail();
        this.password = password;
        this.entered_code = user.getEntered_code();
        this.ref_code = user.getRef_code();
        this.ref_value = user.getRef_value();
        this.server_time = user.getServer_time();
        this.value = user.getValue();
        this.mining_is_started = user.getMining_is_started();
        this.boost = user.getBoost();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEntered_code() {
        return entered_code;
    }

    public void setEntered_code(String entered_code) {
        this.entered_code = entered_code;
    }

    public String getRef_code() {
        return ref_code;
    }

    public void setRef_code(String ref_code) {
        this.ref_code = ref_code;
    }

    public Integer getRef_value() {
        return ref_value;
    }

    public void setRef_value(Integer ref_value) {
        this.ref_value = ref_value;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMining_is_started() {
        return mining_is_started;
    }

    public String getBoost() {
        return boost;
    }

    public void setBoost(String boost) {
        this.boost = boost;
    }

    public void setMining_is_started(Integer mining_is_started) {
        this.mining_is_started = mining_is_started;
    }
}
