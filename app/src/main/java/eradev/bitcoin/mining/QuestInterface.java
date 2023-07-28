package eradev.bitcoin.mining;

public interface QuestInterface {
    void startQuest(String startQuest, int number, int position, boolean repeat);

    void updateTaskUser(String code);
}
