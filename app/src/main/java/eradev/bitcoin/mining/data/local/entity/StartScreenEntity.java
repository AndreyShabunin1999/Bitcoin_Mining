package eradev.bitcoin.mining.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import eradev.bitcoin.mining.data.remote.models.StartScreenModel;

@Entity(tableName = "StartScreen")
public class StartScreenEntity {
    @ColumnInfo(name = "id")
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "isNeedToShow")
    private Boolean isNeedToShow;
    @ColumnInfo(name = "mainText")
    private String mainText;
    @ColumnInfo(name = "buttonText")
    private String buttonText;

    public StartScreenEntity(Boolean isNeedToShow, String mainText, String buttonText) {
        this.id = 0;
        this.isNeedToShow = isNeedToShow;
        this.mainText = mainText;
        this.buttonText = buttonText;
    }

    public StartScreenEntity(StartScreenModel startScreenModel) {
        this.id = 0;
        this.isNeedToShow = startScreenModel.getNeedToShow();
        this.mainText = startScreenModel.getMainText();
        this.buttonText = startScreenModel.getButtonText();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
