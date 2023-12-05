import subprocess
import os
import traceback
from datetime import datetime
from firebase_integration import FirebaseService
from face_recognition import FaceRecognition


class VideoProcessor:
    def __init__(self, upload_folder, known_face_encodings, known_face_names):
        self.firebase_service = FirebaseService()
        self.face_recognition = FaceRecognition()
        self.upload_folder = upload_folder
        self.known_face_encodings = known_face_encodings
        self.known_face_names = known_face_names

    def encode_video(self, video_output_path, frame_rate):
        ffmpeg_command = [
            'ffmpeg',
            '-f', 'concat',
            '-safe', '0',
            '-r', str(frame_rate),
            '-i', 'filelist.txt',
            '-c:v', 'libx264',
            '-profile:v', 'high',
            '-crf', '20',
            '-pix_fmt', 'yuv420p',
            video_output_path
        ]

        process = subprocess.Popen(ffmpeg_command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate()
        if process.returncode != 0:
            print(f"FFmpeg Error: {stderr.decode('utf-8')}")
            raise Exception(f"FFmpeg failed with error: {stderr.decode('utf-8')}")

    def cleanup(self, video_output_path, filelist_path):
        # Read the file list to know which frames were used in the video
        with open(filelist_path, 'r') as file_list:
            used_files = [line.strip().split("'")[1] for line in file_list if line.startswith('file')]

        # Delete the used files
        for file_path in used_files:
            os.remove(file_path)

        # Delete the video file after uploading to Firebase
        if os.path.isfile(video_output_path):
            os.remove(video_output_path)

        # Reset the file list for the next batch
        with open(filelist_path, 'w') as file_list:
            file_list.truncate(0)

    def process_video(self, video_start_time, video_start_date, frame_rate, device_mac_process_video):
        try:
            from datetime import datetime
            timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
            # Ensure the file list is updated right before encoding
            image_files_process_video = [img for img in os.listdir(self.upload_folder) if img.lower().endswith('.jpg')]
            # Sort files by creation time to maintain order
            sorted_files = sorted(image_files_process_video,
                                  key=lambda x: os.path.getctime(os.path.join(self.upload_folder, x)))

            with open('filelist.txt', 'w') as file:
                for image_file in sorted_files:
                    file.write(f"file '{os.path.join(self.upload_folder, image_file)}'\n")

            face_detected = False
            for image_file in sorted_files:
                full_path = os.path.join(self.upload_folder, image_file)
                # face_detected, recognized_name = self.face_recognition.detect_and_recognize_faces(full_path)

                if self.face_recognition.detect_faces(full_path) | face_detected:
                    face_detected = True
                    # print(f"Face detected: {recognized_name}")
                    break
                break

            video_output_path = f'output_{timestamp}.mp4'
            self.encode_video(video_output_path, frame_rate)

            if os.path.isfile(video_output_path):
                if face_detected:
                    message = 'Face Detected'
                else:
                    message = 'Face not detected'
                self.firebase_service.upload_to_firebase(video_output_path, video_start_time, video_start_date, device_mac_process_video,
                                   message)
                self.cleanup(video_output_path, 'filelist.txt')
                return 'Video encoded and uploaded', 200
            else:
                return 'Failed to encode video', 500
        except Exception as e:
            traceback.print_exc()
            return str(e), 500

    def stop_motion(self, frame_count, motion_start_time, motion_start_date, device_mac):
        try:
            motion_end_time = datetime.now()  # End time of motion
            motion_duration = (motion_end_time - motion_start_time).total_seconds()
            if motion_duration > 0 and frame_count > 0:
                frame_rate = frame_count / motion_duration
                self.process_video(motion_start_time, motion_start_date, frame_rate, device_mac)
                frame_count = 0
                motion_start_date = None
                motion_start_time = None
                return 'Motion stopped, video processing triggered', 200
            else:
                return 'Invalid frame count or motion duration', 400
        except Exception as e:
            traceback.print_exc()
            return str(e), 500
