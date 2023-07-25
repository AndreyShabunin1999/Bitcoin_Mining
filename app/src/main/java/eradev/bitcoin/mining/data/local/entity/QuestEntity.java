package eradev.bitcoin.mining.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import eradev.bitcoin.mining.data.remote.models.Quest;

@Entity(tableName = "QuestEntity")
public class QuestEntity {

    @ColumnInfo(name = "id")
    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "number")
    private Integer number;
    @ColumnInfo(name = "weight")
    private Integer weight;
    @ColumnInfo(name = "text")
    private String text;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "imageUrl")
    private String imageUrl;
    @ColumnInfo(name = "repeat")
    private Boolean repeat;
    @ColumnInfo(name = "code")
    private String code;

    public QuestEntity(Integer id, String title, Integer number, Integer weight, String text, String url, String imageUrl, Boolean repeat, String code) {
        this.id = id;
        this.title = title;
        this.number = number;
        this.weight = weight;
        this.text = text;
        this.url = url;
        this.imageUrl = imageUrl;
        this.repeat = repeat;
        this.code = code;
    }

    public QuestEntity(Quest quest, int id) {
        this.id = id;
        this.title = quest.getTitle();
        this.number = quest.getNumber();
        this.weight = quest.getWeight();
        this.text = quest.getText();
        this.url = quest.getUrl();
        this.imageUrl = quest.getImageUrl();
        this.repeat = quest.getRepeat();
        this.code = quest.getCode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
