package eradev.bitcoin.mining.view;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unity3d.ads.UnityAds;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eradev.bitcoin.mining.CallbackFragment;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.local.entity.ConfigAppEntity;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.DepositCodes;
import eradev.bitcoin.mining.data.remote.models.Referals;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.models.StatusMessageDepozit;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoTransactionFragment extends Fragment {

    CallbackFragment callbackFragment;

    TextView tvBalance, tvRemainder, textYourTrans, textNotTransaction, textInfoConclusion, textValueUSTD;
    Button btnConclusion, btnTransaction;
    View viewLineTrans;
    ApiService apiService;
    BitcoinMiningDB db;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_transaction, container, false);

        initView(view);

        //инициализация глобальный переменных
        apiService = Servicey.getPromoMinerApi();
        db = App.getInstance().getDatabase();
        sharedPreferences = requireContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

        //Получение информации о транзакциях
        getTransaction();

        Integer balance = this.getArguments().getInt("balance", 0);
        Integer minimalSummToWithdraw = this.getArguments().getInt("minimalSummToWithdraw", 0);
        tvBalance.setText(getResources().getString(R.string.text_btn_quest_satoshi, balance));

        if(balance > minimalSummToWithdraw) {
            btnConclusion.setTextColor(getResources().getColor(R.color.white));
            btnConclusion.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_btn_sign_up, null));
            textInfoConclusion.setText(getResources().getString(R.string.text_congratulation_trans));
            tvRemainder.setText(R.string.text_empty);
        } else {
            textInfoConclusion.setText(getResources().getString(R.string.text_not_money_trans));
            btnConclusion.setTextColor(getResources().getColor(R.color.color_end_icon_password));
            btnConclusion.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_unused_trans, null));
            tvRemainder.setText(getResources().getString(R.string.text_btn_quest_satoshi, minimalSummToWithdraw - balance));
        }

        Double valueUSTD = balance / this.getArguments().getDouble("courseToUSTD", 0);
        textValueUSTD.setText(getResources().getString(R.string.text_value_ustd_trans, valueUSTD));

        //Обработка клика по кнопки "Вывести"
        btnConclusion.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
            v.startAnimation(scale);
            if(balance > minimalSummToWithdraw) {
                //Обновление баланса Пользователя на сервере и БД
                updateBalanceOnServer(-balance);
                //Создание депозитного кода
                createDepCode();
            } else {
                showDialog();
            }
        });

        btnTransaction.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
            v.startAnimation(scale);
            Intent intent = new Intent(requireActivity(), ListTransactionActivity.class);
            intent.putExtra("email", this.getArguments().getString("email"));
            startActivity(intent);
        });
        return view;
    }

    //Создание диалогового окна о невозможности вывести средства
    private void showDialog(){
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.custom_dialog_boost);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnDialog = dialog.findViewById(R.id.btn_dialog);
        ImageView imgCross = dialog.findViewById(R.id.img_cross);
        ImageView imgDialog = dialog.findViewById(R.id.img_dialog);
        imgDialog.setImageResource(R.drawable.cash);
        TextView textTitle = dialog.findViewById(R.id.text_title_dialog);
        textTitle.setText(getResources().getString(R.string.text_title_dialog_trans));
        TextView textDescription = dialog.findViewById(R.id.text_description_dialog);
        textDescription.setText(getResources().getString(R.string.text_desc_dialog_trans));
        //Обработка нажатия на кнопку "ОК"
        btnDialog.setOnClickListener(v -> dialog.cancel());
        //Обработка нажатия на крестик (закрытие диалогового окна)
        imgCross.setOnClickListener(v -> dialog.cancel());
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    private void updateBalanceOnServer(int newBalance){
        Call<StatusMessage> balanceCall = apiService
                .sendBalance(this.getArguments().getString("email"), newBalance);

        balanceCall.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        //Обновление данных в БД
                        db.userDAO().updateBalanceFromUser(0, 0);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {}
        });
    }

    private void createDepCode(){
        Call<StatusMessageDepozit> depCodeCall = apiService
                .createDepCodes(this.getArguments().getString("email"), 100D,
                        getResources().getString(R.string.text_generate, requireContext().getPackageName()), requireContext().getPackageName());

        depCodeCall.enqueue(new Callback<StatusMessageDepozit>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessageDepozit> call, @NonNull Response<StatusMessageDepozit> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        if(callbackFragment != null){
                            sharedPreferences.edit().putString("promocode", response.body().getPromocode()).apply();
                            callbackFragment.changeFragment();
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<StatusMessageDepozit> call, @NonNull Throwable t) {}
        });
    }

    private void getTransaction(){
        Call<DepositCodes> depositCodesCall = apiService
                .getDepCodes(this.getArguments().getString("email"));

        depositCodesCall.enqueue(new Callback<DepositCodes>() {
            @Override
            public void onResponse(@NonNull Call<DepositCodes> call, @NonNull Response<DepositCodes> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        if(response.body().getPromocodes().size() > 1) {
                            btnTransaction.setVisibility(View.VISIBLE);
                            textYourTrans.setVisibility(View.VISIBLE);
                            viewLineTrans.setVisibility(View.INVISIBLE);
                            textNotTransaction.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        btnTransaction.setVisibility(View.INVISIBLE);
                        textYourTrans.setVisibility(View.INVISIBLE);
                        viewLineTrans.setVisibility(View.VISIBLE);
                        textNotTransaction.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DepositCodes> call, @NonNull Throwable t) {}
        });
    }

    private void initView(View view){
        tvBalance = view.findViewById(R.id.text_balance_trans);
        tvRemainder = view.findViewById(R.id.tv_remainder);
        btnConclusion = view.findViewById(R.id.btn_conclusion);
        btnTransaction = view.findViewById(R.id.btn_transaction);
        textYourTrans = view.findViewById(R.id.text_your_trans);
        viewLineTrans = view.findViewById(R.id.view_line_trans);
        textNotTransaction = view.findViewById(R.id.text_not_transaction);
        textInfoConclusion = view.findViewById(R.id.text_info_conclusion);
        textValueUSTD = view.findViewById(R.id.text_value_USTD);
    }

    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }
}