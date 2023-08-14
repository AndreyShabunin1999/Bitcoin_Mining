package eradev.bitcoin.mining.view;

import static com.unity3d.services.core.properties.ClientProperties.getApplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eradev.bitcoin.mining.AdapterQuest;
import eradev.bitcoin.mining.AdapterReferal;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.data.local.App;
import eradev.bitcoin.mining.data.local.BitcoinMiningDB;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.Referal;
import eradev.bitcoin.mining.data.remote.models.Referals;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import eradev.bitcoin.mining.databinding.FragmentListReferalBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListReferalFragment extends Fragment {

    View view;

    FragmentListReferalBinding binding;

    AdapterReferal adapterReferal;

    List<Referal> referalList = new ArrayList<>();

    TextView center_tv_ref;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_referal, container, false);

        center_tv_ref = view.findViewById(R.id.center_tv_ref);

        recyclerView = view.findViewById(R.id.recycler_view_referal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        getDataReferals();

        return view;
    }

    private void getDataReferals(){
        ApiService apiService = Servicey.getPromoMinerApi();
        BitcoinMiningDB db = App.getInstance().getDatabase();
        Call<Referals> referalsCall = apiService
                .getReferals(db.userDAO().getRefCode());

        referalsCall.enqueue(new Callback<Referals>() {
            @Override
            public void onResponse(@NonNull Call<Referals> call, @NonNull Response<Referals> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1){
                        if(response.body().getReferalList() != null){
                            for(int i = 0; i < response.body().getReferalList().size(); i++){
                                referalList = response.body().getReferalList();
                            }
                            if(referalList.size() > 0){
                                center_tv_ref.setVisibility(View.INVISIBLE);
                            }
                            adapterReferal = new AdapterReferal(referalList);
                            recyclerView.setAdapter(adapterReferal);
                            recyclerView.addItemDecoration(new DividerItemDecoration(getApplication(), DividerItemDecoration.HORIZONTAL));
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Referals> call, @NonNull Throwable t) {}
        });
    }

}