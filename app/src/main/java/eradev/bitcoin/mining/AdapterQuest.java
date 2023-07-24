package eradev.bitcoin.mining;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import eradev.bitcoin.mining.data.local.entity.QuestEntity;
import eradev.bitcoin.mining.databinding.RecyclerViewRowQuestBinding;


public class AdapterQuest extends RecyclerView.Adapter<AdapterQuest.MyViewHolder>{

    List<QuestEntity> questList;

    public AdapterQuest(List<QuestEntity> questList) {
        Log.e("ADAPTER LIST", questList.get(0).getText());
        this.questList = questList;

        Log.e("ADAPTER LIST  CHECK", this.questList.get(0).getText());
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
        Log.e("BIND CHECK", questList.get(position).getText());
        final QuestEntity quest = questList.get(position);
        Log.e("ADAPTER IN BIND", quest.getText());
        holder.bindingQuest.setQuest(quest);
        holder.bindingQuest.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String v) {
        Picasso.with(imageView.getContext()).load(v).into(imageView);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewRowQuestBinding bindingQuest;

        public MyViewHolder(@NonNull RecyclerViewRowQuestBinding bindingQuest) {
            super(bindingQuest.getRoot());
            this.bindingQuest = bindingQuest;
        }

    }
}
