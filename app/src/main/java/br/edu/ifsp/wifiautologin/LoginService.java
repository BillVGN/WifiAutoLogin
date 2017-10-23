package br.edu.ifsp.wifiautologin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

public class LoginService extends Service {
    private NotificationManager mNM;

    private int NOTIFICATION = R.string.Login_Service_Started;

    public LoginService() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        createNotification();
        logCaptivePortal();
    }

    private void logCaptivePortal() {
        PostRequest postRequest = new PostRequest();
        // Parei aqui
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LoginService", "Ordem de início recebida id=" + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);

        Toast.makeText(this, R.string.Login_Service_Stopped, Toast.LENGTH_LONG).show();
    }

    private void createNotification() {
        CharSequence texto = getText(R.string.Login_Service_Started);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginInfoActivity.class), 0);

        mNM.createNotificationChannel(new NotificationChannel(getString(R.string.Login_Service_Label),
                getText(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT));

        Notification notification = new Notification.Builder(this, getString(R.string.Login_Service_Label))
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker(texto)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.Login_Service_Label))
                .setContentText(texto)
                .setContentIntent(contentIntent)
                .build();

        mNM.notify(NOTIFICATION, notification);
    }
}
