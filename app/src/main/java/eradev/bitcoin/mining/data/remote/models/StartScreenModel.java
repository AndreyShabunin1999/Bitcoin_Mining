package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.SerializedName;

//POJO класс стартового экрана
public class StartScreenModel {

    @SerializedName("isNeedToShow")
    private Boolean isNeedToShow;

    @SerializedName("mainText")
    private String mainText;

    @SerializedName("buttonText")
    private String buttonText;

    public StartScreenModel(Boolean isNeedToShow, String mainText, String buttonText) {
        this.isNeedToShow = isNeedToShow;
        this.mainText = mainText;
        this.buttonText = buttonText;
    }

    public Boolean getNeedToShow() {
        return isNeedToShow;
    }

    public void setNeedToShow(Boolean needToShow) {
        isNeedToShow = needToShow;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
