package eradev.bitcoin.mining;

import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.QuestEntity;
import eradev.bitcoin.mining.databinding.RecyclerViewRowQuestBinding;
import eradev.bitcoin.mining.view.ReferalsActivity;


public class AdapterQuest extends RecyclerView.Adapter<AdapterQuest.MyViewHolder>{

    List<QuestEntity> questList;

    public AdapterQuest(List<QuestEntity> questList) {
        this.questList = questList;
    }

    @NonNull
    @Override
    public AdapterQuest.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewRowQuestBinding binding = RecyclerViewRowQuestBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterQuest.MyViewHolder holder, int position) {
        //Стилизация карточек по четно-нечетной позиции
        if(position %2 == 0){
            holder.bindingQuest.imgQuest.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_btn_quest_var1));
            holder.bindingQuest.linearQuest.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.quest_item_var1));
            holder.bindingQuest.tvTitleQuest.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_title_quest_var1));
            holder.bindingQuest.tvDescriptionQuest.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_black));
            holder.bindingQuest.btnActivity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_black));
            holder.bindingQuest.btnActivity.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_btn_in_quest_var1));
            holder.bindingQuest.linearOutBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_out_quest_var1));
            holder.bindingQuest.tvBtnOutActivity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_black));
        } else {
            holder.bindingQuest.imgQuest.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_btn_quest_var2));
            holder.bindingQuest.linearQuest.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.quest_item_var2));
            holder.bindingQuest.tvTitleQuest.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_title_quest_var2));
            holder.bindingQuest.tvDescriptionQuest.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            holder.bindingQuest.btnActivity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            holder.bindingQuest.btnActivity.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_btn_in_quest_var2));
            holder.bindingQuest.linearOutBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_out_quest_var2));
            holder.bindingQuest.tvBtnOutActivity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
        final QuestEntity quest = questList.get(position);
        holder.bindingQuest.setQuest(quest);
        holder.bindingQuest.executePendingBindings();

        //Обработка нажатия по значку поделиться приложением
        holder.bindingQuest.imgQuest.setOnClickListener(v -> {
            //Если пользователь нажал по значку поделиться на реферальном задании
            if(Objects.equals(questList.get(position).getImageUrl(), getApplicationContext().getString(R.string.text_type_ref_quest))) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = getApplicationContext().getString(R.string.intent_share, BuildConfig.APPLICATION_ID,getRefCode());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    getApplicationContext().startActivity(Intent.createChooser(shareIntent, "Bitcoin mining"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        //обработка внутренней кнопки перейти для задания с рефералами
        holder.bindingQuest.btnActivity.setOnClickListener(v -> sendIntent(position));

        //обработка внешней кнопки перейти для задания с рефералами
        holder.bindingQuest.linearOutBtn.setOnClickListener(v -> sendIntent(position));
    }

    private void sendIntent(int position){
        if(Objects.equals(questList.get(position).getImageUrl(), getApplicationContext().getString(R.string.text_type_ref_quest))){
            getApplicationContext().startActivity(new Intent(getApplicationContext(), ReferalsActivity.class));
        }
    }

    //Получение реферального кода пользователя
    private String getRefCode(){
        BitcoinMiningDB db = App.getInstance().getDatabase();
        return db.userDAO().getRefCode();
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        if(!url.equals("")) {
            switch (url){
                case "surprise":
                    imageView.setImageResource(R.drawable.surprise);
                    break;
                case "share":
                    imageView.setImageResource(R.drawable.share);
                    break;
                default:
                    Picasso.with(getApplicationContext()).load(url).into(imageView);
                    break;
            }
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewRowQuestBinding bindingQuest;

        public MyViewHolder(@NonNull RecyclerViewRowQuestBinding bindingQuest) {
            super(bindingQuest.getRoot());
            this.bindingQuest = bindingQuest;
        }

    }
}
