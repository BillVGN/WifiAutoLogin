package br.edu.ifsp.wifiautologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class CaptivePortalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Passa a intent para a atividade principal:
        // Deve iniciar o aplicativo assim que o portal cativo for detectado
        //intent.setClassName("br.edu.ifsp.wifiautologin", "LoginInfoActivity");
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(intent);

        StringBuilder sb = new StringBuilder();
        sb.append(intent.getAction()).
            append("\n").
            append("New State = ").
            append(intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE).toString());

        Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
    }
}
