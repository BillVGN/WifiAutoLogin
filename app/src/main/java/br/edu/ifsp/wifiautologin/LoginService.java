package br.edu.ifsp.wifiautologin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class LoginService extends Service {
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
        PostRequest postRequest = new PostRequest(
                "sp080652",
                "wmctr4b41#0",
                mRRListener
        );

        postRequest.sendPost(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, getClass().getName() + ": Criando notificação", Toast.LENGTH_LONG).show();

        createNotification();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginCaptivePortal();
            }
        }).start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        NotificationManager nM = getSystemService(NotificationManager.class);
        nM.cancel(NOTIFICATION);

        Toast.makeText(this, R.string.Login_Service_Stopped, Toast.LENGTH_LONG).show();
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
