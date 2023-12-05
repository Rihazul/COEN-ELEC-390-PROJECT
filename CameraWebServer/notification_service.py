from pyfcm import FCMNotification


class NotificationService:
    def __init__(self):
        self.push_service = FCMNotification(
            api_key="AAAAsSD20xY:APA91bE8IN0YGxx198qJkVkCX6EG_H2b1d1VABGc8YVICLeqlDJbeh7NYWD6fyPWwJeX9xmsvDuz7w1HoHoCWlnG1RKrXL70POZfyhKMABfJ3Y6zTEq54Oh9W4RunHpyqpMnLYgYgzGX")

    def send_notification(self, registration_ids, home_names, device_names):
        for i, registration_id in enumerate(registration_ids):
            message_title = "Alert"
            # Customize the message body for each user
            message_body = f"Someone is at the door of Home: {home_names[i]}, and Device: {device_names[i]}!"
            notification_message = {
                "title": message_title,
                "body": message_body,
            }

            # Send the notification to all collected FCM tokens
            self.push_service.notify_single_device(
                registration_id=registration_id,
                message_title=message_title,
                message_body=message_body,
                data_message=notification_message,
                sound="default"
            )
