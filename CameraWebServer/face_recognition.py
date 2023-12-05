import os

import cv2
import face_recognition
from firebase_integration import FirebaseService


class FaceRecognition:
    def __init__(self):
        self.firebase_service = FirebaseService()
        self.known_face_encodings = []
        self.known_face_names = []
        self.load_known_faces()

    def load_known_faces(self):
        device_mac = 'E8:31:CD:C6:F6:8C'
        known_face_encodings_load_faces = []
        known_face_names_load_faces = []

        homes_ref = self.firebase_service.ref.child('homes')

        users_ref = self.firebase_service.ref.child('users')

        image_names = []

        for home_id, home_data in homes_ref.get().items():
            # Check if the home has the device_mac as a device
            if 'devices' in home_data:
                devices = home_data['devices']
                if device_mac in devices:
                    # Get the memberUID associated with this home
                    members = home_data.get('members', {})
                    for member_uid, access_level in members.items():
                        if access_level == 'owner':
                            print(f"Image: " + member_uid + '.jpg')
                            image_names.append(member_uid + '.jpg')
                            user_data = users_ref.child(member_uid).get()
                            if user_data:
                                first_name = user_data.get('firstName', None)
                                if first_name:
                                    print(f"Name: " + first_name)
                                    self.known_face_names.append(first_name)

        for image_name in image_names:
            image_path = self.firebase_service.download_image(image_name)
            if image_path:  # Check if the image path is not None
                # image = face_recognition.load_image_file(image_path)
                # encoding = face_recognition.face_encodings(image)[0]
                # known_face_encodings_load_faces.append(encoding)
                os.remove(image_path)

    def detect_and_recognize_faces(self, image_path):
        unknown_image = face_recognition.load_image_file(image_path)
        face_locations = face_recognition.face_locations(unknown_image)
        face_encodings = face_recognition.face_encodings(unknown_image, face_locations)

        name = "Unknown"

        for face_encoding in face_encodings:
            matches = face_recognition.compare_faces(self.known_face_encodings_recognise_faces, face_encoding)

            if True in matches:
                first_match_index = matches.index(True)
                name = self.known_face_names_recognise_faces[first_match_index]

            print(f"Recognized: {name}")

        return len(face_locations) > 0, name


    def detect_faces(self, image_path):
        face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
        image = cv2.imread(image_path)
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, 1.1, 4)
        return len(faces) > 0  # Returns True if faces are detected
