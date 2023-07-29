package eradev.bitcoin.mining.data.remote.models;

public class Referal {
    String email;
    String server_time;
    Integer value;

    public Referal(String email, String server_time, Integer value) {
        this.email = email;
        this.server_time = server_time;
        this.value = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}