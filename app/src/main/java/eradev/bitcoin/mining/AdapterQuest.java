package eradev.bitcoin.mining;

import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import eradev.bitcoin.mining.data.local.entity.UserEntity;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.databinding.RecyclerViewRowQuestBinding;
import eradev.bitcoin.mining.view.ReferalsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdapterQuest extends RecyclerView.Adapter<AdapterQuest.MyViewHolder>{

    List<QuestEntity> questList;
    Context context;

    public AdapterQuest(Context context, List<QuestEntity> questList) {
        this.questList = questList;
        this.context = context;}

    public void setData(List<QuestEntity> questList){
        this.questList = questList;
    }

    public List<QuestEntity> getData(){
        return this.questList;
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
            holder.bindingQuest.imgQuest.setBackground(ContextCompat.getDrawable(context,R.drawable.round_btn_quest_var1));
            holder.bindingQuest.linearQuest.setBackground(ContextCompat.getDrawable(context,R.drawable.quest_item_var1));
            holder.bindingQuest.tvTitleQuest.setTextColor(ContextCompat.getColor(context, R.color.text_title_quest_var1));
            holder.bindingQuest.tvDescriptionQuest.setTextColor(ContextCompat.getColor(context, R.color.color_text_acceleration));
            holder.bindingQuest.btnActivity.setTextColor(ContextCompat.getColor(context, R.color.color_text_acceleration));
            holder.bindingQuest.btnActivity.setBackground(ContextCompat.getDrawable(context,R.drawable.gradient_btn_in_quest_var1));
            holder.bindingQuest.linearOutBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.btn_out_quest_var1));
            holder.bindingQuest.tvBtnOutActivity.setTextColor(ContextCompat.getColor(context, R.color.color_text_acceleration));
        } else {
            holder.bindingQuest.imgQuest.setBackground(ContextCompat.getDrawable(context,R.drawable.round_btn_quest_var2));
            holder.bindingQuest.linearQuest.setBackground(ContextCompat.getDrawable(context,R.drawable.quest_item_var2));
            holder.bindingQuest.tvTitleQuest.setTextColor(ContextCompat.getColor(context, R.color.text_title_quest_var2));
            holder.bindingQuest.tvDescriptionQuest.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.bindingQuest.btnActivity.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.bindingQuest.btnActivity.setBackground(ContextCompat.getDrawable(context,R.drawable.gradient_btn_in_quest_var2));
            holder.bindingQuest.linearOutBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.btn_out_quest_var2));
            holder.bindingQuest.tvBtnOutActivity.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        final QuestEntity quest = questList.get(position);
        holder.bindingQuest.setQuest(quest);
        holder.bindingQuest.executePendingBindings();
        //Надпрись пригласить для реферального задания
        if(holder.bindingQuest.getQuest().getCode().equals(context.getString(R.string.text_code_quest_referal))){
            holder.bindingQuest.tvBtnOutActivity.setText(context.getString(R.string.text_send_ref));
            holder.bindingQuest.btnActivity.setText(context.getString(R.string.text_send_ref));
        }

        //Обработка нажатия по значку поделиться приложением
        holder.bindingQuest.imgQuest.setOnClickListener(v -> {
            //Если пользователь нажал по значку поделиться на реферальном задании
            if(Objects.equals(questList.get(position).getCode(), context.getString(R.string.text_code_quest_referal))) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = context.getString(R.string.intent_share, BuildConfig.APPLICATION_ID,getRefCode());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.app_name)));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        //обработка внутренней кнопки перейти для задания с рефералами
        holder.bindingQuest.btnActivity.setOnClickListener(v -> {
            checkRefQuest(position);
            checkDailyQuest(position);
            urlIntent(position);
        });

        //обработка внешней кнопки перейти для задания с рефералами
        holder.bindingQuest.linearOutBtn.setOnClickListener(v -> {
            checkRefQuest(position);
            checkDailyQuest(position);
            urlIntent(position);
        });
    }

    private void urlIntent(int position){
        if (!Objects.equals(questList.get(position).getUrl(), "")) {
            ((QuestInterface)context).startQuest(questList.get(position).getCode(),
                    questList.get(position).getNumber(), position, questList.get(position).getRepeat());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(questList.get(position).getUrl()));
            context.startActivity(browserIntent);
        }
    }

    //Проверка на ежедневное задание
    private void checkDailyQuest(int position) {
        if (Objects.equals(questList.get(position).getCode(), context.getString(R.string.text_code_quest_daily))) {
            ((QuestInterface)context).startQuest(questList.get(position).getCode(),
                    questList.get(position).getNumber(), position, questList.get(position).getRepeat());
            //Отправка на сервер уведомления об активации ежедневного задания
            sendDailyQuest(position);
        }
    }

    //Проверка на реферальное задание
    private void checkRefQuest(int position){
        if(Objects.equals(questList.get(position).getCode(), context.getString(R.string.text_code_quest_referal))){
            Intent intent = new Intent(context, ReferalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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
                case "daily":
                    imageView.setImageResource(R.drawable.surprise);
                    break;
                case "referal":
                    imageView.setImageResource(R.drawable.share);
                    break;
                default:
                    Picasso.with(getApplicationContext()).load(url).into(imageView);
                    break;
            }
        } else {
            //Если картинки нет imageView скрываем
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    //Отправка уведомления на сервер о выполнении ежедневного бонуса
    public void sendDailyQuest(int position) {
        BitcoinMiningDB db = App.getInstance().getDatabase();
        UserEntity user = db.userDAO().getUser(0);
        ApiService apiService = Servicey.getPromoMinerApi();
        Call<StatusMessage> dailyCall = apiService
                .sendDailyBonus(user.getEmail());
        dailyCall.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        //Обновление
                        ((QuestInterface)context).updateTaskUser(questList.get(position).getCode());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {}
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewRowQuestBinding bindingQuest;

        public MyViewHolder(@NonNull RecyclerViewRowQuestBinding bindingQuest) {
            super(bindingQuest.getRoot());
            this.bindingQuest = bindingQuest;
        }
    }
}
