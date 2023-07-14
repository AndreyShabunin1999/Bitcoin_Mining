package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.SerializedName;

//POJO класс
public class ConfigAppModel {
    private String minimalSummToWithdraw;
    private String minBoost;
    private String maxBoost;
    private String miningPerMinute;
    private String dialogLabel;
    private String dialogText;
    private String dialogOk;
    private String dialogCancel;
    private String dialogUrl;
    private String dailyWeight;
    private String referalWeight;
    private String dailyBonus;
    private String referalBonus;
    private String courseToUSTD;

    public ConfigAppModel(String minimalSummToWithdraw, String minBoost, String maxBoost, String miningPerMinute, String dialogLabel, String dialogText, String dialogOk, String dialogCancel, String dialogUrl, String dailyWeight, String referalWeight, String dailyBonus, String referalBonus, String courseToUSTD) {
        this.minimalSummToWithdraw = minimalSummToWithdraw;
        this.minBoost = minBoost;
        this.maxBoost = maxBoost;
        this.miningPerMinute = miningPerMinute;
        this.dialogLabel = dialogLabel;
        this.dialogText = dialogText;
        this.dialogOk = dialogOk;
        this.dialogCancel = dialogCancel;
        this.dialogUrl = dialogUrl;
        this.dailyWeight = dailyWeight;
        this.referalWeight = referalWeight;
        this.dailyBonus = dailyBonus;
        this.referalBonus = referalBonus;
        this.courseToUSTD = courseToUSTD;
    }

    public String getMinimalSummToWithdraw() {
        return minimalSummToWithdraw;
    }

    public void setMinimalSummToWithdraw(String minimalSummToWithdraw) {
        this.minimalSummToWithdraw = minimalSummToWithdraw;
    }

    public String getMinBoost() {
        return minBoost;
    }

    public void setMinBoost(String minBoost) {
        this.minBoost = minBoost;
    }

    public String getMaxBoost() {
        return maxBoost;
    }

    public void setMaxBoost(String maxBoost) {
        this.maxBoost = maxBoost;
    }

    public String getMiningPerMinute() {
        return miningPerMinute;
    }

    public void setMiningPerMinute(String miningPerMinute) {
        this.miningPerMinute = miningPerMinute;
    }

    public String getDialogLabel() {
        return dialogLabel;
    }

    public void setDialogLabel(String dialogLabel) {
        this.dialogLabel = dialogLabel;
    }

    public String getDialogText() {
        return dialogText;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public String getDialogOk() {
        return dialogOk;
    }

    public void setDialogOk(String dialogOk) {
        this.dialogOk = dialogOk;
    }

    public String getDialogCancel() {
        return dialogCancel;
    }

    public void setDialogCancel(String dialogCancel) {
        this.dialogCancel = dialogCancel;
    }

    public String getDialogUrl() {
        return dialogUrl;
    }

    public void setDialogUrl(String dialogUrl) {
        this.dialogUrl = dialogUrl;
    }

    public String getDailyWeight() {
        return dailyWeight;
    }

    public void setDailyWeight(String dailyWeight) {
        this.dailyWeight = dailyWeight;
    }

    public String getReferalWeight() {
        return referalWeight;
    }

    public void setReferalWeight(String referalWeight) {
        this.referalWeight = referalWeight;
    }

    public String getDailyBonus() {
        return dailyBonus;
    }

    public void setDailyBonus(String dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    public String getReferalBonus() {
        return referalBonus;
    }

    public void setReferalBonus(String referalBonus) {
        this.referalBonus = referalBonus;
    }

    public String getCourseToUSTD() {
        return courseToUSTD;
    }

    public void setCourseToUSTD(String courseToUSTD) {
        this.courseToUSTD = courseToUSTD;
    }
}
