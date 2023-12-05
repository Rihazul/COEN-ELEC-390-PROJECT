import os

import firebase_admin
from firebase_admin import credentials, storage, db
import socket
from notification_service import NotificationService
import google


class FirebaseService:
    def __init__(self):
        self.notification_service = NotificationService()
        self.initialize_firebase()
        self.ref = db.reference('')

    def initialize_firebase(self):
        if not firebase_admin._apps:
            cred = credentials.Certificate('google-services.json')
            firebase_admin.initialize_app(cred, {
                'storageBucket': 'coen-elec-390-ed129.appspot.com',
                'databaseURL': 'https://coen-elec-390-ed129-default-rtdb.firebaseio.com'
            })

    def update_server_ip(self):
        hostname = socket.gethostname()
        server_ip = socket.gethostbyname(hostname)
        ref_server_info = self.ref.reference('server_info')
        ref_server_info.set({
            'ip': server_ip
        })

    def upload_to_firebase(self, video_output_path, video_start_time, video_start_date, device_mac_uploading, message):
        formatted_start_time = video_start_time.strftime("%H:%M:%S") if video_start_time else None
        formatted_start_date = video_start_date.strftime("%Y-%m-%d") if video_start_date else None

        bucket = storage.bucket()
        blob = bucket.blob(f'videos/{video_output_path}')
        blob.upload_from_filename(filename=video_output_path)

        # After uploading, create a publicly accessible URL to the file
        blob.make_public()
        video_url = blob.public_url

        video_title = video_output_path.split('.')[0]

        # Now, add an entry to the Firebase Database with the video URL and timestamp
        video_ref = self.ref.child('devices').child(device_mac_uploading).child('recordings')
        video_ref.child(video_title).set({
            'url': video_url,
            'time': formatted_start_time,
            'date': formatted_start_date,
            'message': message
        })

        homes_ref = db.reference('homes')

        users_ref = db.reference('users')

        registration_ids = []
        home_names = []
        device_names = []

        for home_id, home_data in homes_ref.get().items():
            # Check if the home has the device_mac as a device
            if 'devices' in home_data:
                devices = home_data['devices']
                if device_mac_uploading in devices:
                    # Get the memberUID associated with this home
                    members = home_data.get('members', {})
                    if device_mac_uploading in home_data["devices"]:
                        device_name = home_data['devices'][device_mac_uploading]
                        device_names.append(device_name)
                    for member_uid, access_level in members.items():
                        if access_level == 'owner':  # You can adjust this condition as needed
                            # Retrieve the FCM token of the owner
                            user_data = users_ref.child(member_uid).get()
                            if user_data:
                                fcm_token = user_data.get('fcmToken', None)
                                if fcm_token:
                                    registration_ids.append(fcm_token)
                                    home_name = user_data.get('homes', {}).get(home_id, 'Unknown Home')
                                    home_names.append(home_name)

            self.notification_service.send_notification(registration_ids, home_names, device_names)

    def download_image(self, image_name):
        try:
            bucket = storage.bucket()
            blob = bucket.blob(f'images/{image_name}')
            os.makedirs('temp', exist_ok=True)
            image_path = f"temp/{image_name}"
            blob.download_to_filename(image_path)
            return image_path
        except google.cloud.exceptions.NotFound:
            print(f"The file {image_name} was not found in Firebase Storage.")
            return None
