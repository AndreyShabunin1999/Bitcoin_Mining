package eradev.bitcoin.mining.data.remote.models;

import java.util.List;

public class DepositCodes {
    private String message;
    private Integer success;
    private List<Promocode> promocodeList;

    public DepositCodes(String message, Integer success, List<Promocode> promocodeList) {
        this.message = message;
        this.success = success;
        this.promocodeList = promocodeList;
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

    public List<Promocode> getPromocodeList() {
        return promocodeList;
    }

    public void setPromocodeList(List<Promocode> promocodeList) {
        this.promocodeList = promocodeList;
    }
}
