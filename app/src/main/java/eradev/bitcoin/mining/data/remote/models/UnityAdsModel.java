package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.SerializedName;

//POJO Ads Class
public class UnityAdsModel {
    @SerializedName("unityFullScreen")
    private String unityFullScreen;
    @SerializedName("unityID")
    private String unityID;
    @SerializedName("unityReward")
    private String unityReward;

    public String getUnityFullScreen() {
        return unityFullScreen;
    }

    public String getUnityID() {
        return unityID;
    }

    public String getUnityReward() {
        return unityReward;
    }

    public UnityAdsModel(String unityFullScreen, String unityID, String unityReward) {
        this.unityFullScreen = unityFullScreen;
        this.unityID = unityID;
        this.unityReward = unityReward;
    }
}
