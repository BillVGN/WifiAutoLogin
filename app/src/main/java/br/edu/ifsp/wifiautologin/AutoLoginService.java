package br.edu.ifsp.wifiautologin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class AutoLoginService extends Service {

    private NotificationManager mNM;

    private String NOTIFICATION = AutoLoginService.class.toString();

    private int NOTIFICATION_ID = 80652;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannel();
        showNotification();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.WifiAutoLogin);
            String description = getString(R.string.Login_Service_Label);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNM.createNotificationChannel(channel);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionExtra = intent.getStringExtra("action");
        if (actionExtra == null) {
            return START_STICKY;
        } else {
            stopSelf();
            return START_NOT_STICKY;
        }
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.Login_Service_Label);

        // activityIntent lança o app quando a notificação é tocada
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginInfoActivity.class)
                        .putExtra("origin", "service"), 0);

        // stopIntent botão para encerrar o serviço. Chama a si mesmo
        PendingIntent stopIntent = PendingIntent.getService(this, 0,
                new Intent(this, AutoLoginService.class)
                        .putExtra("action", "stop"), 0);

        // Set the info for the views that show in the notification panel.
        NotificationCompat.Builder ncBuilder = new NotificationCompat.Builder(this, NOTIFICATION)
                .setSmallIcon(R.drawable.notification_icon)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.Login_Service_Started))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(activityIntent)  // The intent to send when the entry is clicked
                .addAction(R.drawable.notification_icon, getText(R.string.Login_Service_Stop), stopIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION_ID, ncBuilder.build());
    }
}
