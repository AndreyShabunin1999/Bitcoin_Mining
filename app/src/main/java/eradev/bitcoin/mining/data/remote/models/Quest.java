package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quest {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("number")
    @Expose
    private Integer number;

    @SerializedName("weight")
    @Expose
    private Integer weight;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("repeat")
    @Expose
    private Boolean repeat;

    @SerializedName("code")
    @Expose
    private String code;

    public Quest(String title, Integer number, Integer weight, String text, String url, String imageUrl, Boolean repeat, String code) {
        this.title = title;
        this.number = number;
        this.weight = weight;
        this.text = text;
        this.url = url;
        this.imageUrl = imageUrl;
        this.repeat = repeat;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(Boolean repeat) {
        this.repeat = repeat;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
