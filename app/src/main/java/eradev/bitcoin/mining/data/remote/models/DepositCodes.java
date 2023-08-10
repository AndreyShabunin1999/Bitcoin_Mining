package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DepositCodes {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("promocodes")
    @Expose
    private List<Promocode> promocodes;

    public DepositCodes(String message, Integer success, List<Promocode> promocodes) {
        this.message = message;
        this.success = success;
        this.promocodes = promocodes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public List<Promocode> getPromocodes() {
        return promocodes;
    }

    public void setPromocodes(List<Promocode> promocodes) {
        this.promocodes = promocodes;
    }
}
