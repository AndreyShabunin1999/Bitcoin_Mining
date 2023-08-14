package eradev.bitcoin.mining.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
            v.startAnimation(scale);
            Intent intent = new Intent(ConnectionErrorActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}