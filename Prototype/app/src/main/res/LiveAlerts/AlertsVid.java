public class MyActivity extends Activity {

    // ... Your existing code ...

    public void onVideoRecordingComplete() {
        // Assuming you have recorded the video and now want to notify the user
        String message = "Video recording completed at " + getCurrentTimeAndDate();
        NotificationHelper.showNotification(this, message);
    }

    private String getCurrentTimeAndDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
