package br.edu.ifsp.wifiautologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.CaptivePortal;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class CaptivePortalReceiver extends BroadcastReceiver {

    private static final String TAG = "CaptivePortalReceiver";

    public static final String CAPTIVE_PORTAL_LOGIN_ACTION = BuildConfig.APPLICATION_ID + ".action.LOGIN_CAPTIVE_PORTAL";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = context.getSystemService(ConnectivityManager.class);
        Network network = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK);
        CaptivePortal captivePortal = intent.getParcelableExtra(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);

        String ifsp_ssid = "\"IFSP Reitoria\"";
        NetworkInfo networkInfo = cm.getNetworkInfo(network);
        // Checamos se temos a rede da reitoria
        if (networkInfo.getExtraInfo().equals(ifsp_ssid)) {
            cm.bindProcessToNetwork(network);
            Intent loginIntent = new Intent(context, LoginService.class);
            loginIntent.setAction(CAPTIVE_PORTAL_LOGIN_ACTION);
            context.startService(loginIntent);
        }
    }
}
