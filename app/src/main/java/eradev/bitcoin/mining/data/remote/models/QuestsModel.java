package eradev.bitcoin.mining.data.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//POJO класс заданий
public class QuestsModel {
   @SerializedName("quests")
   @Expose
   List<Quest> quests;

   public List<Quest> getQuests() {
      return quests;
   }

   public void setQuests(List<Quest> quests) {
      this.quests = quests;
   }

   public QuestsModel(List<Quest> quests) {
      this.quests = quests;
   }
}

