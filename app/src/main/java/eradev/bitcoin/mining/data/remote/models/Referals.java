package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Referals {
    @SerializedName("message")
    @Expose
    String message;
    @SerializedName("success")
    @Expose
    Integer success;
    @SerializedName("referals")
    @Expose
    List<Referal> referalList;

    public Referals(String message, Integer success, List<Referal> referalList) {
        this.message = message;
        this.success = success;
        this.referalList = referalList;
    }

    @Override
    public String toString() {
        return "Referals{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", referalList=" + referalList +
                '}';
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

    public List<Referal> getReferalList() {
        return referalList;
    }

    public void setReferalList(List<Referal> referalList) {
        this.referalList = referalList;
    }
}