package eradev.bitcoin.mining.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import java.util.List;

import eradev.bitcoin.mining.BuildConfig;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.Referal;
import eradev.bitcoin.mining.data.remote.models.Referals;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.databinding.ActivityReferalsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferalsActivity extends AppCompatActivity {

    private ActivityReferalsBinding binding;

    private ClipboardManager clipboardManager;
    private BitcoinMiningDB db;

    List<Referal> referalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referals);

        //инициализация глобальный переменных
        db = App.getInstance().getDatabase();
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        //Загрузка данных
        loadDataBinding();

        //Обработка нажатия кнопки назад
        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        //Обработка клика по кнопке (значку) копировать
        binding.imgBtnCopy.setOnClickListener(v -> {
            String textCopy = binding.textValueCodeReferal.getText().toString();
            ClipData clipData = ClipData.newPlainText("textCopy", textCopy);
            clipboardManager.setPrimaryClip(clipData);
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

        getDataReferals();
    }

    private void getDataReferals(){
        ApiService apiService = Servicey.getPromoMinerApi();
        Log.e("CHECK1", binding.textValueCodeReferal.getText().toString());
        Log.e("CHECK2", binding.getUser().getRef_code());
        Call<Referals> referalsCall = apiService
                .getReferals(binding.getUser().getRef_code());

        referalsCall.enqueue(new Callback<Referals>() {
            @Override
            public void onResponse(@NonNull Call<Referals> call, @NonNull Response<Referals> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        for(int i = 0; i < response.body().getReferalList().size(); i ++){
                            referalList = response.body().getReferalList();
                            Log.e("CHECK", response.body().getReferalList().get(0).getEmail());
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Referals> call, @NonNull Throwable t) {}
        });
    }

    //Загрузка данных в DataBinding
    private void loadDataBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_referals);
        binding.setUser(db.userDAO().getUser(0));
    }
}