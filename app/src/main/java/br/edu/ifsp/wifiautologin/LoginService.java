package br.edu.ifsp.wifiautologin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class LoginService extends Service {
    private static final String TAG = "LoginService";

    private CharSequence texto;

    private PendingIntent contentIntent;

    private int NOTIFICATION;

    private PostRequest.OnResponseReceivedListener mRRListener = new PostRequest.OnResponseReceivedListener() {
        @Override
        public void onResponseReceived(String response) {
            updateNotification(response);
            if (response.contains("Conectado")) {
                stopSelf();
            }
        }
    };

    public LoginService() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        texto = getText(R.string.Login_Service_Started);
        NOTIFICATION = R.string.Login_Service_Started;
    }

    private void loginCaptivePortal() {
        try {
            PostRequest postRequest = new PostRequest("sp080652", "wmctr4b41#0", mRRListener);
            postRequest.sendPost();
        } catch (Exception e) {
            Log.d(TAG, "Erro no login do portal cativo: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            createNotification();
        } catch (Exception e) {
            Log.d(TAG, "Erro ao criar a notificação: " + e.getLocalizedMessage(), e);
        }
        loginCaptivePortal();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        NotificationManager nM = getSystemService(NotificationManager.class);
        nM.cancel(NOTIFICATION);
    }

    private void createNotification() {
        contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginInfoActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker(texto)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.Login_Service_Label))
                .setContentText(texto)
                .setContentIntent(contentIntent)
                .build();

        NotificationManager nM = getSystemService(NotificationManager.class);
        nM.notify(NOTIFICATION, notification);
    }

    private void updateNotification(String response) {
        contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginInfoActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker(texto)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.Login_Service_Label))
                .setContentText(response)
                .setContentIntent(contentIntent)
                .build();

        NotificationManager nM = getSystemService(NotificationManager.class);
        nM.notify(NOTIFICATION, notification);
    }
}
