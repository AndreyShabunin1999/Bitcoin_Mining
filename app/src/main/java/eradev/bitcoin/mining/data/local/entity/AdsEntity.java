package eradev.bitcoin.mining.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import eradev.bitcoin.mining.data.remote.models.UnityAdsModel;

@Entity(tableName = "Ads")
public class AdsEntity {
    @ColumnInfo(name = "id")
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "unityFullScreen")
    private String unityFullScreen;
    @ColumnInfo(name = "unityID")
    private String unityID;
    @ColumnInfo(name = "unityReward")
    private String unityReward;

    public AdsEntity(String unityFullScreen, String unityID, String unityReward) {
        this.id = 0;
        this.unityFullScreen = unityFullScreen;
        this.unityID = unityID;
        this.unityReward = unityReward;
    }

    public AdsEntity(UnityAdsModel unityAdsModel) {
        this.id = 0;
        this.unityFullScreen = unityAdsModel.getUnityFullScreen();
        this.unityID = unityAdsModel.getUnityID();
        this.unityReward = unityAdsModel.getUnityReward();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnityFullScreen() {
        return unityFullScreen;
    }

    public void setUnityFullScreen(String unityFullScreen) {
        this.unityFullScreen = unityFullScreen;
    }

    public String getUnityID() {
        return unityID;
    }

    public void setUnityID(String unityID) {
        this.unityID = unityID;
    }

    public String getUnityReward() {
        return unityReward;
    }

    public void setUnityReward(String unityReward) {
        this.unityReward = unityReward;
    }
}
