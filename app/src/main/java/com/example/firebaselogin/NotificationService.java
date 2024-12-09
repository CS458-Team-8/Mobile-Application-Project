package com.example.firebaselogin;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        System.out.println("SEND TOKEN 1");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            System.out.println("SEND TOKEN 2");
            // Get user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Only update the token if we have an ID

            Map<String, Object> userToken = new HashMap<>();
            userToken.put("fcmToken", token);

            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .update(userToken).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FCM", "Token saved to Firestore.");
                        }
                    })
                    .addOnFailureListener(e -> Log.w("FCM", "Error updating FCM token", e));
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("MESSAGE TRIGGERED");

            RemoteMessage.Notification notification = remoteMessage.getNotification();

            if ( notification != null) {
                String title = notification.getTitle();
                String body = notification.getBody();

                System.out.println(title);
                System.out.println(body);
                Log.d("NotificationService", "Message received: " + remoteMessage.getMessageId());

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                });
            }
    }
}


