import socket

import cv2
import face_recognition
import numpy as np
from tensorflow import keras


def extract_eye_region_for_prediction(frame):
    # Detect facial landmarks
    facial_landmarks_list = face_recognition.face_landmarks(frame)

    if not facial_landmarks_list:
        return

    # Get left and right eye coordinates
    try:
        left_eye_coordinates = facial_landmarks_list[0]["left_eye"]
        right_eye_coordinates = facial_landmarks_list[0]["right_eye"]
    except KeyError:
        return

    # Draw rectangles around each eye
    for eye_coordinates in [left_eye_coordinates, right_eye_coordinates]:
        x_max = max([x for x, y in eye_coordinates])
        x_min = min([x for x, y in eye_coordinates])
        y_max = max([y for x, y in eye_coordinates])
        y_min = min([y for x, y in eye_coordinates])

        # Calculate the range of x and y coordinates
        x_range = x_max - x_min
        y_range = y_max - y_min

        # Ensure the full eye is captured by calculating coordinates of a square with a 50% cushion
        if x_range > y_range:
            right_bound = round(0.5 * x_range) + x_max
            left_bound = x_min - round(0.5 * x_range)
            bottom_bound = round(
                (right_bound - left_bound - y_range) / 2) + y_max
            top_bound = y_min - round((right_bound - left_bound - y_range) / 2)
        else:
            bottom_bound = round(0.5 * y_range) + y_max
            top_bound = y_min - round(0.5 * y_range)
            right_bound = round(
                (bottom_bound - top_bound - x_range) / 2) + x_max
            left_bound = x_min - \
                         round((bottom_bound - top_bound - x_range) / 2)

        # Draw rectangle around the eye
        cv2.rectangle(
            frame, (left_bound, top_bound), (right_bound,
                                             bottom_bound), (255, 0, 0), 2
        )

    # Crop the image according to the determined coordinates
    cropped_eye_region = frame[
                         top_bound: bottom_bound + 1, left_bound: right_bound + 1
                         ]

    # Resize the cropped image to 80x80 pixels
    resized_cropped_eye_region = cv2.resize(cropped_eye_region, (80, 80))

    # Reshape the image for model prediction
    image_for_prediction = resized_cropped_eye_region.reshape(-1, 80, 80, 3)

    return image_for_prediction


def receive_frame(sock):
    # Receive frame size
    frame_size = sock.recv(1024)
    frame_size = int.from_bytes(frame_size, byteorder='big')

    # Receive frame data
    frame_data = b""
    while len(frame_data) < frame_size:
        data = sock.recv(4096)
        if not data:
            break
        frame_data += data

    # Convert frame data to numpy array
    frame_bytes = np.frombuffer(frame_data, dtype=np.uint8)
    frame = cv2.imdecode(frame_bytes, cv2.IMREAD_COLOR)

    return frame


def initialize_server(host, port):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(1)
    print("Server listening...")
    client_socket, client_address = server_socket.accept()
    print(f"Connection from {client_address} established.")
    return client_socket


# Trained model
trained_model = keras.models.load_model("drowsiness_model.keras")

# Initialize server
client_socket = initialize_server("localhost", 50000)

# Initialize counters
frame_count = 0
blink_counter = 0

# Run a continuous loop while monitoring
while True:
    # Receive frame from client
    frame = receive_frame(client_socket)

    # Process the frame to get the eye for prediction
    eye_image = extract_eye_region_for_prediction(frame)
    try:
        eye_image = eye_image / 255.0
    except:
        continue

    # Get prediction from the trained model
    prediction = trained_model.predict(eye_image)

    # Display status based on the prediction ("Open Eyes" or "Closed Eyes")
    if prediction < 0.5:
        blink_counter = 0
    else:
        blink_counter += 1

        # If the blink counter exceeds 2, show an alert for drowsiness
        if blink_counter > 2:
            # TODO: Send notification to client
            print("DROWSY")
            blink_counter = 1
            continue

    # Exit the loop on receiving stop signal from client
    message = client_socket.recv(1024).decode()
    if message == "STOP_MONITORING":
        break

# Close connection
client_socket.close()
