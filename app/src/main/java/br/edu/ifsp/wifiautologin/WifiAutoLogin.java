package br.edu.ifsp.wifiautologin;

import android.app.Application;
import android.content.Context;

/**
 * Created by willian on 24/10/17.
 */

public class WifiAutoLogin extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
