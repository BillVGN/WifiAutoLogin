package br.edu.ifsp.wifiautologin;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class CaptivePortalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        /*
        StringBuilder sb = new StringBuilder();
        sb.append(intent.getAction()).
            append("\n").
            append("New State = ").
            append(networkInfo.getState().toString());

        Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
        */

        ConnectivityManager cm = context.getSystemService(ConnectivityManager.class);

        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            String ifsp_ssid = "\"IFSP Reitoria\"";
            String extra = "";
            NetworkInfo info = null;
            Network[] nets = cm.getAllNetworks();
            for (Network net : nets) {
                //NetworkCapabilities netCaps = cm.getNetworkCapabilities(net);
                info = cm.getNetworkInfo(net);
                extra = (info != null) ? info.getExtraInfo() : null;
                if (ifsp_ssid.equals(extra)) {
                    cm.bindProcessToNetwork(net);
                    /*
                    Intent login = new Intent(context, LoginInfoActivity.class);
                    context.startActivity(login);
                    */
                    callLoginService(context);
                }
            }
        }
    }

    private void callLoginService(Context context) {
    }
}
