package br.edu.ifsp.wifiautologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class CaptivePortalReceiver extends BroadcastReceiver {

    private static final String TAG = "CaptivePortalReceiver";

    public static final String CAPTIVE_PORTAL_LOGIN_ACTION = BuildConfig.APPLICATION_ID + ".action.LOGIN_CAPTIVE_PORTAL";

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

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
                    Intent login = new Intent(context, LoginService.class);
                    login.setAction(CAPTIVE_PORTAL_LOGIN_ACTION);
                    Toast.makeText(context, getClass().getName() + ": Iniciando LoginService", Toast.LENGTH_LONG).show();
                    context.startService(login);
                }
            }
        }
    }
}
