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
    @ColumnInfo(name = "test")
    private String test;

    public QuestEntity(Integer id, String title, Integer number, Integer weight, String text, String url, String imageUrl, Boolean repeat, String test) {
        this.id = id;
        this.title = title;
        this.number = number;
        this.weight = weight;
        this.text = text;
        this.url = url;
        this.imageUrl = imageUrl;
        this.repeat = repeat;
        this.test = test;
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
        this.test = quest.getTest();
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

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
