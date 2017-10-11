package br.edu.ifsp.wifiautologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CaptivePortalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Passa a intent para a atividade principal:
        // Deve iniciar o aplicativo assim que o portal cativo for detectado
        intent.setClassName("br.edu.ifsp.wifiautologin", "LoginInfoActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
