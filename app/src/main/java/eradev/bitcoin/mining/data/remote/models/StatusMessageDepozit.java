package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//Pojo class StatusMessage
public class StatusMessageDepozit {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("promocode")
    @Expose
    private String promocode;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public StatusMessageDepozit(Integer success, String message, String promocode) {
        this.success = success;
        this.message = message;
        this.promocode = promocode;
    }
}
