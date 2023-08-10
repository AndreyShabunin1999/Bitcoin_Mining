package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import eradev.bitcoin.mining.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Button btnInfo = findViewById(R.id.btn_back_info);
        Button btnOk = findViewById(R.id.btn_ok_info);

        btnInfo.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        btnOk.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

    }
}