package eradev.bitcoin.mining.data.remote.models;

public class Promocode {
    private String creation_time;
    private String email;
    private String promocode;
    private String server_time;
    private Integer status;
    private String value;

    public Promocode(String creation_time, String email, String promocode, String server_time, Integer status, String value) {
        this.creation_time = creation_time;
        this.email = email;
        this.promocode = promocode;
        this.server_time = server_time;
        this.status = status;
        this.value = value;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}