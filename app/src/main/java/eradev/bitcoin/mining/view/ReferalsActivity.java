package eradev.bitcoin.mining.view;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eradev.bitcoin.mining.CallbackFragment;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;
import eradev.bitcoin.mining.data.remote.ApiService;
import eradev.bitcoin.mining.data.remote.models.Referal;
import eradev.bitcoin.mining.data.remote.models.Referals;
import eradev.bitcoin.mining.data.remote.request.Servicey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferalsActivity extends AppCompatActivity implements CallbackFragment {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    Fragment fragment;
    FragmentManager fragmentManager;

    FragmentTransaction fragmentTransaction;

    List<Referal> referalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referals);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addFragmentReferal();

        Button btnBack = findViewById(R.id.btn_back_ref);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    public void addFragmentReferal() {
        ReferalFragment referalFragment = new ReferalFragment();
        referalFragment.setCallbackFragment(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, referalFragment);
        fragmentTransaction.commit();
    }

    public void replaceFragment() {
        fragment = new ListReferalFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.add(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    public void changeFragment() {
        replaceFragment();
    }
}