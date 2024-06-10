import os
import shutil
import threading
from datetime import datetime

import cv2
import face_recognition
import numpy as np
import requests
from flask import Flask
from flask_socketio import SocketIO
from tensorflow import keras

app = Flask(__name__)
socketio = SocketIO(app)

# Load the model
trained_model = keras.models.load_model('./drowsiness_model.keras', compile=False)

# Output directory for debugging
output_dir = 'result'

# Store timestamps when the status is 'closed'
closed_timestamps = []

# Cloud server URL
cloud_server_url = 'http://10.0.2.2:8080/data'


def extract_eye_region_for_prediction(frame):
    # Detect facial landmarks
    facial_landmarks_list = face_recognition.face_landmarks(frame)

    if not facial_landmarks_list:
        return None

    # Get left and right eye coordinates
    try:
        left_eye_coordinates = facial_landmarks_list[0]['left_eye']
        right_eye_coordinates = facial_landmarks_list[0]['right_eye']
    except KeyError:
        return None

    # Draw rectangles around each eye
    for eye_coordinates in [left_eye_coordinates, right_eye_coordinates]:
        x_max = max([x for x, y in eye_coordinates])
        x_min = min([x for x, y in eye_coordinates])
        y_max = max([y for x, y in eye_coordinates])
        y_min = min([y for x, y in eye_coordinates])

        # Calculate the range
        x_range = x_max - x_min
        y_range = y_max - y_min

        if x_range > y_range:
            right_bound = round(0.5 * x_range) + x_max
            left_bound = x_min - round(0.5 * x_range)
            bottom_bound = round((right_bound - left_bound - y_range) / 2) + y_max
            top_bound = y_min - round((right_bound - left_bound - y_range) / 2)
        else:
            bottom_bound = round(0.5 * y_range) + y_max
            top_bound = y_min - round(0.5 * y_range)
            right_bound = round((bottom_bound - top_bound - x_range) / 2) + x_max
            left_bound = x_min - round((bottom_bound - top_bound - x_range) / 2)

        # Draw rectangle around the eye
        cv2.rectangle(frame, (left_bound, top_bound), (right_bound, bottom_bound), (255, 0, 0), 2)

    # Crop the image based on the coordinates
    cropped_eye_region = frame[top_bound:bottom_bound + 1, left_bound:right_bound + 1]

    # Resize & Reshape the image
    resized_cropped_eye_region = cv2.resize(cropped_eye_region, (80, 80))
    image_for_prediction = resized_cropped_eye_region.reshape(-1, 80, 80, 1)

    return image_for_prediction


def process_and_save_frame(frame, prediction, status):
    try:
        # Show prediction & status on the image
        cv2.putText(frame, f'Prediction: {prediction[0][0]:.2f}', (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 0, 0),
                    2, cv2.LINE_AA)
        cv2.putText(frame, 'Status: ' + status, (10, 70), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 0, 0), 2, cv2.LINE_AA)

        # Save the frame to the disk
        current_time = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
        output_path = os.path.join(output_dir, f'{status}_{current_time}.jpg')
        cv2.imwrite(output_path, frame)
        print(f"Processed frame saved: {output_path}")

        # Store timestamp
        if status == 'Closed':
            closed_timestamps.append(current_time)

    except Exception as e:
        print(f"Error processing frame: {e}")


# folder exists delete it, then recreate
def create_directory():
    if os.path.exists(output_dir):
        shutil.rmtree(output_dir)

    # Create the folder
    os.makedirs(output_dir, exist_ok=True)
    print(f"Result directory is created...")


@socketio.on('connect')
def handle_connect():
    global closed_timestamps
    print('Client connected...')
    closed_timestamps = []
    create_directory()


@socketio.on('disconnect')
def handle_disconnect():
    print('Client disconnected...')
    if closed_timestamps:
        try:
            response = requests.post(cloud_server_url, json={'timestamps': closed_timestamps})
            if response.status_code == 200:
                print('Timestamps successfully sent...')
            else:
                print(f'Failed to send timestamps...')
        except Exception as e:
            print(f"Caught Error sending timestamps: {e}")


@socketio.on('frame')
def handle_frame(data):
    try:
        # Convert & decode frame
        arr_1d = np.frombuffer(data, np.uint8)
        frame = cv2.imdecode(arr_1d, cv2.IMREAD_GRAYSCALE)
        frame = cv2.rotate(frame, -90)

        if frame is None:
            print("No frame received...")
            return

        # Process the frame and retrieve eyes for prediction
        eye_image = extract_eye_region_for_prediction(frame)
        if eye_image is None:
            print("No eyes detected...")
            return

        eye_image = eye_image / 255.0

        # Make predictions
        prediction = trained_model.predict(eye_image)

        if prediction < 0.5:
            status = 'Open'
            print("Eyes are open...")
        else:
            status = 'Closed'
            print("Eyes are closed...")

        # Save the result
        threading.Thread(target=process_and_save_frame, args=(frame, prediction, status)).start()

    except Exception as e:
        print(f"Error processing: {e}")


if __name__ == '__main__':
    socketio.run(app, host='localhost', port=5001, allow_unsafe_werkzeug=True, debug=True)
