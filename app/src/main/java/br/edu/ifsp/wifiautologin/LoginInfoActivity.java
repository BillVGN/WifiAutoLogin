package br.edu.ifsp.wifiautologin;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import static android.net.ConnectivityManager.*;

public class LoginInfoActivity extends AppCompatActivity {
    private static final String TAG = "LoginInfoActivity";

    private TextView inputUser = null;
    private TextView inputPassword = null;
    private TextView textResponse = null;
    public static String PREFS_NAME = "walprefs";

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendPostRequest(inputUser.getText().toString(), inputPassword.getText().toString());
        }
    };

    private PostRequest.OnResponseReceivedListener mRRListener = new PostRequest.OnResponseReceivedListener() {
        @Override
        public void onResponseReceived(String response) {
            textResponse.setText(response);
        }
    };

    private void sendPostRequest(final String login, final String password) {
        try {
            if (makeWifiAvailable()) {
                PostRequest postRequest = new PostRequest(login, password, mRRListener);
                postRequest.sendPost();
            }
        } catch (Exception e) {
            Log.d(TAG, getString(R.string.ERROR_CAPTIVE_PORTAL_LOGIN) + e.getLocalizedMessage(), e);
        }
    }

    private void updateSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.prefs_user), inputUser.getText().toString());
        editor.putString(getString(R.string.prefs_pass), inputPassword.getText().toString());
        editor.apply();
    }

    private void readSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        inputUser.setText(sharedPreferences.getString(getString(R.string.prefs_user), ""));
        inputPassword.setText(sharedPreferences.getString(getString(R.string.prefs_pass), ""));
    }

    private View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            updateSettings();
        }
    };

    private void assignViewListeners() {
        // Referenciar os campos da View, caso ainda não estejam
        if (inputUser == null || inputPassword == null) {
            inputUser = (TextView) findViewById(R.id.inputUser);
            inputPassword = (TextView) findViewById(R.id.inputPassword);
        }
        textResponse = (TextView) findViewById(R.id.textResponse);

        // Referenciar os eventos aos listeners
        inputUser.setOnFocusChangeListener(mFocusListener);
        inputPassword.setOnFocusChangeListener(mFocusListener);
        findViewById(R.id.btnPostRequest).setOnClickListener(mClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);
        boolean captive = getIntent().getBooleanExtra("captive", false);
        assignViewListeners();
        readSettings();
        if (!captive) {
            setNetworkCallback();
        }
    }

    private void setNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Intent intent = new Intent(this, LoginInfoActivity.class);
        intent.putExtra("captive", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
                .build();
        cm.registerNetworkCallback(networkRequest,pendingIntent);
    }

    private boolean makeWifiAvailable() {
        boolean available = false;

        ConnectivityManager cm = getSystemService(ConnectivityManager.class);

        ArrayList<Network> wifiNetworks = getWifiNetworks(cm);

        if(wifiNetworks.isEmpty()) return false;

        for (Network net: wifiNetworks) {
            NetworkCapabilities netCaps = cm != null ? cm.getNetworkCapabilities(net) : null;
            if (netCaps != null &&
                    netCaps.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)) {
                cm.bindProcessToNetwork(net);
                available = true;
            }
        }

        return available;
    }

    private ArrayList<Network> getWifiNetworks(ConnectivityManager cm) {
        ArrayList<Network> networks = new ArrayList<>();
        NetworkInfo netInfo;

        for (Network net: cm.getAllNetworks()) {
            netInfo = cm.getNetworkInfo(net);
            if (netInfo != null && netInfo.getType() == TYPE_WIFI) {
                networks.add(net);
            }
        }

        return networks;
    }
}
