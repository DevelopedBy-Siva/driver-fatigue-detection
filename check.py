import os
import cv2
import numpy as np
import face_recognition
from tensorflow import keras
import matplotlib.pyplot as plt


# Load the trained model
trained_model = keras.models.load_model('drowsiness_model.keras', compile=False)


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

        # Calculate the range of x and y coordinates
        x_range = x_max - x_min
        y_range = y_max - y_min

        # Ensure the full eye is captured by calculating coordinates of a square with a 50% cushion
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

    # Crop the image according to the determined coordinates
    cropped_eye_region = frame[top_bound:bottom_bound + 1, left_bound:right_bound + 1]

    # Convert to grayscale
    gray_eye_region = cv2.cvtColor(cropped_eye_region, cv2.COLOR_BGR2GRAY)

    # Resize the cropped image to 80x80 pixels
    resized_cropped_eye_region = cv2.resize(gray_eye_region, (80, 80))

    # Reshape the image for model prediction
    image_for_prediction = resized_cropped_eye_region.reshape(-1, 80, 80, 1)

    return image_for_prediction


def analyze_image(image_path):
    # Load the image
    frame = cv2.imread(image_path)

    # Process the frame to get the eye for prediction
    eye_image = extract_eye_region_for_prediction(frame)
    if eye_image is None:
        print("No eyes detected in the image.")
        return

    eye_image = eye_image / 255.0  # Normalize the image

    # Get prediction from the trained model
    prediction = trained_model.predict(eye_image)

    # Display status based on the prediction ("Open Eyes" or "Closed Eyes")
    if prediction < 0.5:
        status = 'Open'
    else:
        status = 'Closed'

    # Show prediction on Screen
    cv2.putText(frame, f'Prediction: {prediction[0][0]:.2f}', (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 0, 0), 2,
                cv2.LINE_AA)
    # Show status (Open or Closed) on Screen
    cv2.putText(frame, 'Status: ' + status, (10, 70), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 0, 0), 2, cv2.LINE_AA)

    # Display the processed frame
    plt.imshow(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
    plt.axis('off')
    plt.show()


# Path to the image for analysis
image_path = "frames/frame_00214.jpg"

# Analyze the image
analyze_image(image_path)
