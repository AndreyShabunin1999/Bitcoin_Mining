package eradev.bitcoin.mining;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eradev.bitcoin.mining.data.remote.models.Referal;
import eradev.bitcoin.mining.databinding.RecyclerViewRowReferalBinding;

public class AdapterReferal extends RecyclerView.Adapter<AdapterReferal.MyViewHolder>{

    List<Referal> referalsList;

    public AdapterReferal(List<Referal> referalsList) {
        this.referalsList = referalsList;}

    @NonNull
    @Override
    public AdapterReferal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewRowReferalBinding binding = RecyclerViewRowReferalBinding.inflate(inflater, parent, false);
        return new AdapterReferal.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Referal referal = referalsList.get(position);
        referal.setEmail(maskEmail(referal.getEmail()));
        holder.bindingReferal.setReferal(referal);
        holder.bindingReferal.executePendingBindings();
    }

    private String maskEmail(String email) {
        int index = email.indexOf("@");
        if(email.substring(0,index).length() > 5){
            email = email.substring(0,index-5)+"*****"+email.substring(index);
        } else if(index/2 == 0){
            return email;
        } else {
            int j = 0;
            for(int i = 1; i <= email.substring(1,index).length(); i++){
                email = email.substring(0,index-i) + '*' + email.substring(index - j);
                j++;
            }
        }
        return email;
    }

    @Override
    public int getItemCount() {
        return referalsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewRowReferalBinding bindingReferal;

        public MyViewHolder(@NonNull RecyclerViewRowReferalBinding bindingReferal) {
            super(bindingReferal.getRoot());
            this.bindingReferal = bindingReferal;
        }
    }
}
