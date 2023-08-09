package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import eradev.bitcoin.mining.CallbackFragment;
import eradev.bitcoin.mining.R;
import eradev.bitcoin.mining.checkNetwork.NetworkChangeListener;

public class TransactionActivity extends AppCompatActivity implements CallbackFragment {

    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button btnBack = findViewById(R.id.btn_back_trans);

        Intent intent = getIntent();

        bundle = new Bundle();
        bundle.putInt("balance", intent.getIntExtra("balance",0));
        bundle.putInt("minimalSummToWithdraw", intent.getIntExtra("minimalSummToWithdraw",0));
        bundle.putString("email", intent.getStringExtra("email"));

        addFragmentTransaction();

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    public void addFragmentTransaction() {
        InfoTransactionFragment fragment = new InfoTransactionFragment();
        fragment.setCallbackFragment(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_trans, fragment);
        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    public void replaceFragment() {
        fragment = new ConclusionFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.add(R.id.fragment_container_trans, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void changeFragment() {
        replaceFragment();
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
}