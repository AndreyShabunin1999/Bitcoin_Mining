package eradev.bitcoin.mining.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import eradev.bitcoin.mining.CallbackFragment;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.DepositCodes;
import eradev.bitcoin.mining.data.remote.models.Referals;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoTransactionFragment extends Fragment {

    CallbackFragment callbackFragment;

    TextView tvBalance, tvRemainder, textYourTrans, textNotTransaction;
    Button btnConclusion, btnTransaction;
    View viewLineTrans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_transaction, container, false);

        initView(view);

        Integer balance = this.getArguments().getInt("balance", 0);
        Integer minimalSummToWithdraw = this.getArguments().getInt("minimalSummToWithdraw", 0);
        tvBalance.setText(getResources().getString(R.string.text_btn_quest_satoshi, balance));
        getTransaction();

        if(balance > minimalSummToWithdraw) {
            btnConclusion.setTextColor(getResources().getColor(R.color.white));
            btnConclusion.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_btn_sign_up, null));
        } else {
            btnConclusion.setTextColor(getResources().getColor(R.color.color_end_icon_password));
            btnConclusion.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_unused_trans, null));
            tvRemainder.setText(getResources().getString(R.string.text_btn_quest_satoshi, minimalSummToWithdraw - balance));
        }

        btnConclusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callbackFragment != null){
                    callbackFragment.changeFragment();
                }
            }
        });
        return view;
    }

    private void getTransaction(){
        ApiService apiService = Servicey.getPromoMinerApi();
        Call<DepositCodes> depositCodesCall = apiService
                .getDepCodes(this.getArguments().getString("email"));

        depositCodesCall.enqueue(new Callback<DepositCodes>() {
            @Override
            public void onResponse(@NonNull Call<DepositCodes> call, @NonNull Response<DepositCodes> response) {
                if(response.isSuccessful()){
                    Log.e("Чет пришло","");
                    Log.e("SUC",String.valueOf(response.body().getSuccess()));
                    Log.e("SUC",String.valueOf(response.body().getMessage()));
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        if(response.body().getPromocodeList().size() > 1) {
                            Log.e("YA TUT1","");
                            btnTransaction.setVisibility(View.VISIBLE);
                            textYourTrans.setVisibility(View.VISIBLE);
                            viewLineTrans.setVisibility(View.INVISIBLE);
                            textNotTransaction.setVisibility(View.INVISIBLE);
                        } else {
                            Log.e("YA TUT0","");
                            btnTransaction.setVisibility(View.INVISIBLE);
                            textYourTrans.setVisibility(View.INVISIBLE);
                            viewLineTrans.setVisibility(View.VISIBLE);
                            textNotTransaction.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DepositCodes> call, @NonNull Throwable t) {
                Log.e("YA TUT_ERROR","");
            }
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
    }

    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }
}