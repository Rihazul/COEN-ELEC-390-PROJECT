// Video.java
public class Video {
    private String title;
    private String url;

    // Constructor, getters, and setters
    // ...
}

// YourFirebaseMessagingService.java
import android.util.Log;
        import com.google.firebase.messaging.FirebaseMessagingService;
        import com.google.firebase.messaging.RemoteMessage;

public class YourFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "YourFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle the incoming FCM message (notification)
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // You can customize the notification handling here, e.g., display a notification to the user
        }
    }
}

// YourActivity.java
import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

public class YourActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("videos");

        // Add a ChildEventListener to detect new videos
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new video has been added
                Video newVideo = dataSnapshot.getValue(Video.class);
                if (newVideo != null) {
                    // Notify the user or perform any action
                    Log.d("VideoNotification", "New video added: " + newVideo.getTitle());

                    // Send a push notification using FCM
                    sendNotification("New video added: " + newVideo.getTitle());
                }
            }

            // Other methods of ChildEventListener (onChildChanged, onChildRemoved, onChildMoved) can also be implemented if needed

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });
    }

    private void sendNotification(String message) {
        // Use FirebaseMessaging to send a notification
        // For simplicity, you can use the FirebaseMessagingService directly
        // Customize the notification payload according to your needs
        // You can add additional data to the notification payload
        // For more customization options, refer to the FCM documentation
        // https://firebase.google.com/docs/cloud-messaging
        // The notification will be received in YourFirebaseMessagingService's onMessageReceived method
    }
}
