import android.app.Activity;
import android.telephony.SmsManager;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyActivity extends Activity {

    // Replace with the recipient's phone number
    private static final String phoneNumber = "1234567890";

    public void sendSMS() {
        try {
            // Get the current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateAndTime = sdf.format(new Date());

            // Create the message
            String message = "Video recording completed at " + currentDateAndTime;

            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();

            // Send the message
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
