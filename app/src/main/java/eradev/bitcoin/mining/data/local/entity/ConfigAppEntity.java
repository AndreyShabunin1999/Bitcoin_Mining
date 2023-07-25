package eradev.bitcoin.mining.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import eradev.bitcoin.mining.data.remote.models.ConfigAppModel;

@Entity(tableName = "ConfigApp")
public class ConfigAppEntity {

    @ColumnInfo(name = "id")
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "minimalSummToWithdraw")
    private String minimalSummToWithdraw;
    @ColumnInfo(name = "minBoost")
    private String minBoost;
    @ColumnInfo(name = "maxBoost")
    private String maxBoost;
    @ColumnInfo(name = "miningPerMinute")
    private String miningPerMinute;
    @ColumnInfo(name = "dialogLabel")
    private String dialogLabel;
    @ColumnInfo(name = "dialogText")
    private String dialogText;
    @ColumnInfo(name = "dialogOk")
    private String dialogOk;
    @ColumnInfo(name = "dialogCancel")
    private String dialogCancel;
    @ColumnInfo(name = "dialogUrl")
    private String dialogUrl;
    @ColumnInfo(name = "dailyWeight")
    private String dailyWeight;
    @ColumnInfo(name = "referalWeight")
    private String referalWeight;
    @ColumnInfo(name = "dailyBonus")
    private String dailyBonus;
    @ColumnInfo(name = "referalBonus")
    private String referalBonus;
    @ColumnInfo(name = "courseToUSTD")
    private String courseToUSTD;

    public ConfigAppEntity(String minimalSummToWithdraw, String minBoost, String maxBoost, String miningPerMinute, String dialogLabel, String dialogText, String dialogOk, String dialogCancel, String dialogUrl, String dailyWeight, String referalWeight, String dailyBonus, String referalBonus, String courseToUSTD) {
        this.id = 0;
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

    public ConfigAppEntity(ConfigAppModel configAppModel) {
        this.id = 0;
        this.minimalSummToWithdraw = configAppModel.getMinimalSummToWithdraw();
        this.minBoost = configAppModel.getMinBoost();
        this.maxBoost = configAppModel.getMaxBoost();
        this.miningPerMinute = configAppModel.getMiningPerMinute();
        this.dialogLabel = configAppModel.getDialogLabel();
        this.dialogText = configAppModel.getDialogText();
        this.dialogOk = configAppModel.getDialogOk();
        this.dialogCancel = configAppModel.getDialogCancel();
        this.dialogUrl = configAppModel.getDialogUrl();
        this.dailyWeight = configAppModel.getDailyWeight();
        this.referalWeight = configAppModel.getReferalWeight();
        this.dailyBonus = configAppModel.getDailyBonus();
        this.referalBonus = configAppModel.getReferalBonus();
        this.courseToUSTD = configAppModel.getCourseToUSTD();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
