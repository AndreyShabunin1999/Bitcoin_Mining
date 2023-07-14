package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import eradev.bitcoin.mining.R;

public class ConnectionErrorActivity extends AppCompatActivity {

    Button btnReconnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_error);

        btnReconnection = findViewById(R.id.btn_reconnection);

        btnReconnection.setOnClickListener(v -> {
            Intent intent = new Intent(ConnectionErrorActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();
        });

    }
}