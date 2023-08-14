package eradev.bitcoin.mining.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import eradev.bitcoin.mining.BuildConfig;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eradev.bitcoin.mining.CallbackFragment;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.Referal;
import eradev.bitcoin.mining.data.remote.models.Referals;
import eradev.bitcoin.mining.data.remote.models.StatusMessage;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.databinding.FragmentReferalBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferalFragment extends Fragment {

    CallbackFragment callbackFragment;
    private ClipboardManager clipboardManager;
    private BitcoinMiningDB db;
    private FragmentReferalBinding binding;

    ApiService apiService;

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = App.getInstance().getDatabase();
        apiService = Servicey.getPromoMinerApi();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_referal, container, false);
        binding.setUser(db.userDAO().getUser(0));

        view = binding.getRoot();

        //инициализация глобальный переменных
        clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        //Обработка клика по кнопке (значку) копировать
        binding.imgBtnCopy.setOnClickListener(v -> {
            String textCopy = binding.textValueCodeReferal.getText().toString();
            ClipData clipData = ClipData.newPlainText("textCopy", textCopy);
            clipboardManager.setPrimaryClip(clipData);

            LayoutInflater inflaterToast = getLayoutInflater();
            View toastLayout = inflaterToast.inflate(R.layout.toast_layout, (ViewGroup) view.findViewById(R.id.toast_root));

            TextView tvText = toastLayout.findViewById(R.id.text_toast);
            tvText.setText(R.string.text_toast_copy);

            Toast toast = new Toast(requireContext());
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(toastLayout);
            toast.show();
        });

        //Обработка нажатия на кнопку поделиться
        binding.imgBtnShare.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = getString(R.string.intent_share, BuildConfig.APPLICATION_ID, binding.getUser().getRef_code());
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)));
            } catch(Exception e) {
                //e.toString();
            }
        });

        binding.btnViewAllReferals.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(requireActivity(), R.anim.scale);
            v.startAnimation(scale);
            if(callbackFragment != null){
                callbackFragment.changeFragment();
            }
        });

        getDataReferals();

        binding.btnGet.setOnClickListener(v -> {
            int valueRef = Integer.parseInt(db.configAppDao().getReferalBonus());

            Call<StatusMessage> refCall = apiService
                    .updateEneteredCode(binding.getUser().getEmail(), valueRef, binding.etRevValue.getText().toString());

            refCall.enqueue(new Callback<StatusMessage>() {
                @Override
                public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                    if(response.isSuccessful()){
                        assert response.body() != null;
                        if(response.body().getSuccess() == 1){
                            updateBalanceUser(binding.getUser().getValue() + valueRef);
                            binding.etRevValue.setEnabled(false);
                            binding.btnGet.setEnabled(false);
                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());
                            executorService.execute(() -> handler.post(() -> {
                                db.userDAO().updateEnteredCodeUser(binding.etRevValue.getText().toString());
                            }));
                            binding.getUser().setEntered_code(binding.etRevValue.getText().toString());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {}
            });

        });

        binding.etRevValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Проверка на  количество символов
                if (binding.etRevValue.getText().length() == 10) {
                    //Проверка на ввод своего же реф кода
                    if (!binding.etRevValue.getText().toString().equals(binding.getUser().getRef_code())) {
                        binding.btnGet.setEnabled(true);
                    }
                } else {
                    //Проверка на ввод
                    binding.btnGet.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void getDataReferals(){
        Call<Referals> referalsCall = apiService
                .getReferals(binding.getUser().getRef_code());

        referalsCall.enqueue(new Callback<Referals>() {
            @Override
            public void onResponse(@NonNull Call<Referals> call, @NonNull Response<Referals> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        if(response.body().getReferalList() != null){
                            binding.tvCountRef.setText(String.valueOf(response.body().getReferalList().size()));
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Referals> call, @NonNull Throwable t) {}
        });
    }

    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }

    private void updateBalanceUser(int number){
        int newBalance = binding.getUser().getValue() + number;
        Call<StatusMessage> balanceCall = apiService
                .sendBalance(binding.getUser().getEmail(), newBalance);
        balanceCall.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(@NonNull Call<StatusMessage> call, @NonNull Response<StatusMessage> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        //обновление баланса в БД
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executorService.execute(() -> handler.post(() -> db.userDAO().updateBalanceFromUser(0, newBalance)));
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<StatusMessage> call, @NonNull Throwable t) {}
        });
    }
}