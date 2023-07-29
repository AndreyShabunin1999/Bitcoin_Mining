package eradev.bitcoin.mining.data.remote.models;

import java.util.List;

public class Referals {
    String message;
    Integer success;
    List<Referal> referalList;

    public Referals(String message, Integer success, List<Referal> referalList) {
        this.message = message;
        this.success = success;
        this.referalList = referalList;
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