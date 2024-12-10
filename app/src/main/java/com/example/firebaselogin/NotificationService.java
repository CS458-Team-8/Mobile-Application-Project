package com.example.firebaselogin;

import androidx.annotation.NonNull;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
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

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println("FCM USER ID: " + userId);
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
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if (notification != null) {
            String title = notification.getTitle();
            String body = notification.getBody();

            Log.d("NotificationService", "Message received: " + remoteMessage.getMessageId());

            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Requests a new FCM token for notifications.
     */
    public static void requestNewToken(){
        FirebaseMessaging.getInstance().deleteToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(getTokenTask -> {
                                    if (getTokenTask.isSuccessful() && getTokenTask.getResult() != null) {
                                        String newToken = getTokenTask.getResult();
                                        Log.d("FirebaseMessaging", "New FCM token: " + newToken);
                                    } else {
                                        Log.e("FirebaseMessaging", "Failed to fetch new FCM token", getTokenTask.getException());
                                    }
                                });
                    } else {
                        Log.e("FirebaseMessaging", "Failed to delete token", task.getException());
                    }
                });

    }
}


