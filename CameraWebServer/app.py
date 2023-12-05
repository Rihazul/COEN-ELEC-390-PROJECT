from flask import Flask, request
from werkzeug.utils import secure_filename
from datetime import datetime
import os
from threading import Lock
from firebase_integration import FirebaseService
from video_processing import VideoProcessor
from face_recognition import FaceRecognition
from notification_service import NotificationService


class ESP32CameraServer:
    def __init__(self):
        self.app = Flask(__name__)
        self.UPLOAD_FOLDER = 'uploaded_frames'
        os.makedirs(self.UPLOAD_FOLDER, exist_ok=True)
        self.known_face_encodings = []
        self.known_face_names = []
        self.firebase_service = FirebaseService()
        self.video_processor = VideoProcessor(self.UPLOAD_FOLDER, self.known_face_encodings, self.known_face_names)
        self.face_recognition = FaceRecognition()
        self.notification_service = NotificationService()
        self.frame_count = 0
        self.motion_start_time = None
        self.motion_start_date = None
        self.device_mac = None
        self.frame_lock = Lock()
        self.setup_routes()

    def setup_routes(self):
        @self.app.route('/upload', methods=['POST'])
        def upload_frame():
            self.device_mac = request.headers.get('X-DEVICE-MAC')
            if 'frame' not in request.files:
                return 'No frame part in the request', 400
            f = request.files['frame']
            if f.filename == '':
                return 'No selected file', 400
            if f:
                timestamp = datetime.now().strftime("%Y%m%d%H%M%S%f")
                unique_filename = f"{timestamp}_{secure_filename(f.filename)}"
                file_path = os.path.join(self.UPLOAD_FOLDER, unique_filename)
                f.save(file_path)
                with self.frame_lock:
                    self.frame_count += 1
                    if self.frame_count == 1:
                        self.motion_start_date = datetime.now()
                        self.motion_start_time = datetime.now()
            return 'Frame saved', 200

        @self.app.route('/process', methods=['POST'])
        def process_route():
            return self.video_processor.process_video()

        @self.app.route('/stop_motion', methods=['POST'])
        def stop_motion_route():
            return self.video_processor.stop_motion(
                self.frame_count,
                self.motion_start_time,
                self.motion_start_date,
                self.device_mac
            )

    def run(self):
        self.firebase_service.initialize_firebase()
        self.app.run(host='0.0.0.0', port=5000)


if __name__ == '__main__':
    app = ESP32CameraServer()
    app.run()
