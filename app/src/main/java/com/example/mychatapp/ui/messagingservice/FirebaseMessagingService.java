package com.example.mychatapp.ui.messagingservice;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.mychatapp.R;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.profile_icon)
                .setContentTitle("My Notifications")
                .setContentText("Hello World")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
