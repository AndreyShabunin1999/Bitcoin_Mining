package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("email")
    private String email;
    @SerializedName("entered_code")
    private String entered_code;
    @SerializedName("ref_code")
    private String ref_code;
    @SerializedName("ref_value")
    private Integer ref_value;
    @SerializedName("server_time")
    private String server_time;
    @SerializedName("value")
    private Integer value;
    @SerializedName("mining_is_started")
    private Integer mining_is_started;

    @SerializedName("tasks")
    private String task;

    @SerializedName("daily")
    private String daily;
    @SerializedName("boost")
    private String boost;

    public User(String email, String entered_code, String ref_code, Integer ref_value, String server_time, Integer value, Integer mining_is_started, String task, String daily, String boost) {
        this.email = email;
        this.entered_code = entered_code;
        this.ref_code = ref_code;
        this.ref_value = ref_value;
        this.server_time = server_time;
        this.value = value;
        this.mining_is_started = mining_is_started;
        this.boost = boost;
        this.daily = daily;
        this.task = task;
    }

    public String getBoost() {
        return boost;
    }

    public void setBoost(String boost) {
        this.boost = boost;
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

    public Integer getMining_is_started() {
        return mining_is_started;
    }

    public void setMining_is_started(Integer mining_is_started) {
        this.mining_is_started = mining_is_started;
    }

    public String getDaily() {
        return daily;
    }

    public void setDaily(String daily) {
        this.daily = daily;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
