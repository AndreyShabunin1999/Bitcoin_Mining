package eradev.bitcoin.mining.checkNetwork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eradev.bitcoin.mining.view.ConnectionErrorActivity;

public class NetworkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Если нет подключения к интернету
        if(!Common.isConnectedInternet(context)) {
            Intent intent_splash = new Intent(context, ConnectionErrorActivity.class);
            context.startActivity(intent_splash);
        }
    }
}
