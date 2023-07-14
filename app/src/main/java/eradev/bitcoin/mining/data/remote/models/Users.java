package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Users {
    @SerializedName("success")
    private Integer success;
    @SerializedName("message")
    private String message;
    @SerializedName("users")
    private List<User> users;

    public Users(Integer success, String message, List<User> users) {
        this.success = success;
        this.message = message;
        this.users = users;
    }

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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}