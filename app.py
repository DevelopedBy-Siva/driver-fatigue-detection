from flask import Flask
from flask_socketio import SocketIO
import cv2
import numpy as np
import os
import face_recognition
from tensorflow import keras

app = Flask(__name__)
socketio = SocketIO(app, cors_allowed_origins='*')

# Load the trained model
try:
    trained_model = keras.models.load_model('drowsiness_model.keras', compile=False)
except Exception as e:
    print(f'Error loading model: {e}')
    trained_model = None

# Create a directory to save the images if it doesn't exist
if not os.path.exists('frames'):
    os.makedirs('frames')

frame_count = 0


@socketio.on('connect')
def handle_connect():
    print('Client connected')


@socketio.on('disconnect')
def handle_disconnect():
    print('Client disconnected')


@socketio.on('frame')
def handle_frame(data):
    global frame_count
    try:
        # Convert bytes to numpy array
        nparr = np.frombuffer(data, np.uint8)
        # Decode image
        img_gray = cv2.imdecode(nparr, cv2.IMREAD_GRAYSCALE)
        if img_gray is not None:
            # Repeat the single channel to create a pseudo-RGB image
            img = np.repeat(img_gray[:, :, np.newaxis], 3, axis=2)

            # Rotate the image to the correct orientation (180 degrees rotation)
            try:
                img = cv2.rotate(img, cv2.ROTATE_180)
            except cv2.error as e:
                print(f'Error rotating image: {e}')
                return

            # Detect faces
            try:
                face_locations = face_recognition.face_locations(img)
            except Exception as e:
                print(f'Error detecting faces: {e}')
                return

            if face_locations:
                try:
                    # Get landmarks for the detected face
                    face_landmarks_list = face_recognition.face_landmarks(img)
                    if face_landmarks_list:
                        left_eye_coordinates = face_landmarks_list[0]['left_eye']
                        right_eye_coordinates = face_landmarks_list[0]['right_eye']

                        for eye_coordinates in [left_eye_coordinates, right_eye_coordinates]:
                            x_max = max([x for x, y in eye_coordinates])
                            x_min = min([x for x, y in eye_coordinates])
                            y_max = max([y for x, y in eye_coordinates])
                            y_min = min([y for x, y in eye_coordinates])

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

                            try:
                                cropped_eye_region = img[top_bound:bottom_bound + 1, left_bound:right_bound + 1]
                                resized_cropped_eye_region = cv2.resize(cropped_eye_region, (80, 80))

                                image_for_prediction = resized_cropped_eye_region.reshape(-1, 80, 80, 3) / 255.0

                                if trained_model:
                                    prediction = trained_model.predict(image_for_prediction)
                                    if prediction < 0.5:
                                        print("Eye Open")
                                    else:
                                        print("Eye Closed")
                                else:
                                    print("Model not loaded, cannot make prediction")

                            except cv2.error as e:
                                print(f'Error processing eye region: {e}')
                            except Exception as e:
                                print(f'Unexpected error: {e}')

                    else:
                        print('No face landmarks detected')

                except KeyError as e:
                    print(f'KeyError: {e}')
                except IndexError as e:
                    print(f'IndexError: {e}')
                except Exception as e:
                    print(f'Error processing face landmarks: {e}')
            else:
                print('No face detected, frame not saved')
        else:
            print('Received frame is None')
    except Exception as e:
        print(f'Error processing frame: {e}')


if __name__ == '__main__':
    socketio.run(app, host='localhost', port=5001, allow_unsafe_werkzeug=True, debug=True)
