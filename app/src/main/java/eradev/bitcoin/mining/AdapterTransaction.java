package eradev.bitcoin.mining;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eradev.bitcoin.mining.data.remote.models.Promocode;
import eradev.bitcoin.mining.databinding.RecyclerViewRowTransactionBinding;


public class AdapterTransaction extends RecyclerView.Adapter<AdapterTransaction.MyViewHolder>{

    List<Promocode> promocodesList;
    Context context;

    public AdapterTransaction(Context context, List<Promocode> promocodesList) {
        this.promocodesList = promocodesList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterTransaction.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewRowTransactionBinding binding = RecyclerViewRowTransactionBinding.inflate(inflater, parent, false);
        return new AdapterTransaction.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTransaction.MyViewHolder holder, int position) {
        final Promocode promocode = promocodesList.get(position);
        holder.bindingTransaction.setPromocode(promocode);
        holder.bindingTransaction.executePendingBindings();

        switch (promocodesList.get(position).getStatus()) {
            case 0:
                holder.bindingTransaction.tvStatus.setText(context.getResources().getString(R.string.text_promo_not_used));
                holder.bindingTransaction.tvStatus.setTextColor(context.getResources().getColor(R.color.ORANGE));
                break;
            case 1:
                holder.bindingTransaction.tvStatus.setText(context.getResources().getString(R.string.text_promo_used));
                holder.bindingTransaction.tvStatus.setTextColor(context.getResources().getColor(R.color.GREEN));
                break;
            case 2:
                holder.bindingTransaction.tvStatus.setText(context.getResources().getString(R.string.text_promo_invalid));
                holder.bindingTransaction.tvStatus.setTextColor(context.getResources().getColor(R.color.RED));
                break;
        }

        holder.bindingTransaction.tvCopyDepCode.setOnClickListener(v -> {
            //инициализация глобальный переменных
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("promocode", promocodesList.get(position).getPromocode());
            clipboardManager.setPrimaryClip(clipData);
        });
    }

    @Override
    public int getItemCount() {
        return promocodesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewRowTransactionBinding bindingTransaction;

        public MyViewHolder(@NonNull RecyclerViewRowTransactionBinding bindingTransaction) {
            super(bindingTransaction.getRoot());
            this.bindingTransaction = bindingTransaction;
        }
    }
}

