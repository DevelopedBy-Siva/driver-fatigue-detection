import face_recognition
import cv2
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
            bottom_bound = round((right_bound - left_bound - y_range) / 2) + y_max
            top_bound = y_min - round((right_bound - left_bound - y_range) / 2)
        else:
            bottom_bound = round(0.5 * y_range) + y_max
            top_bound = y_min - round(0.5 * y_range)
            right_bound = round((bottom_bound - top_bound - x_range) / 2) + x_max
            left_bound = x_min - round((bottom_bound - top_bound - x_range) / 2)

        # Draw rectangle around the eye
        cv2.rectangle(
            frame, (left_bound, top_bound), (right_bound, bottom_bound), (255, 0, 0), 2
        )

    # Crop the image according to the determined coordinates
    cropped_eye_region = frame[
        top_bound : bottom_bound + 1, left_bound : right_bound + 1
    ]

    # Resize the cropped image to 80x80 pixels
    resized_cropped_eye_region = cv2.resize(cropped_eye_region, (80, 80))

    # Reshape the image for model prediction
    image_for_prediction = resized_cropped_eye_region.reshape(-1, 80, 80, 3)

    return image_for_prediction


def initialize_webcam():
    cap = cv2.VideoCapture(0)
    w = cap.get(cv2.CAP_PROP_FRAME_WIDTH)
    h = cap.get(cv2.CAP_PROP_FRAME_HEIGHT)

    if not cap.isOpened():
        raise IOError("Cannot open webcam")

    return cap, w, h


# Trained model
trained_model = keras.models.load_model("drowsiness_model.keras")

# Initialize webcam
webcam, width, height = initialize_webcam()

# Initialize counters
frame_count = 0
blink_counter = 0

# Run a continuous loop while the webcam is active
while True:
    # Capture frames from the webcam
    ret, frame = webcam.read()

    # Use only every other frame to manage speed and memory usage
    if frame_count == 0:
        frame_count += 1
        pass
    else:
        frame_count = 0
        continue

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
        status = "Open"

    else:
        blink_counter += 1
        status = "Closed"

        # If the blink counter exceeds 2, show an alert for drowsiness
        if blink_counter > 2:
            # Show alert message on Screen
            cv2.rectangle(
                frame,
                (round(width / 2) - 160, round(height) - 200),
                (round(width / 2) + 175, round(height) - 120),
                (0, 0, 255),
                -1,
            )
            cv2.putText(
                frame,
                "Blink, Don't Sleep!",
                (round(width / 2) - 136, round(height) - 146),
                cv2.FONT_HERSHEY_SIMPLEX,
                1,
                (255, 255, 255),
                2,
                cv2.LINE_4,
            )
            cv2.imshow("Drowsiness Detection", frame)
            k = cv2.waitKey(1)
            blink_counter = 1
            continue

    # Show prediction on Screen
    cv2.putText(
        frame,
        f"{prediction}",
        (10, 30),
        cv2.FONT_HERSHEY_SIMPLEX,
        0.7,
        (255, 0, 0),
        2,
        cv2.LINE_AA,
    )
    # Show status (Open or Closed on Screen)
    cv2.putText(
        frame,
        "Status: " + status,
        (10, 70),
        cv2.FONT_HERSHEY_SIMPLEX,
        1,
        (255, 0, 0),
        2,
        cv2.LINE_AA,
    )

    # Display the processed frame
    cv2.imshow("Drowsiness Detection", frame)

    # Exit the loop on pressing the 'Esc' key
    if cv2.waitKey(1) & 0xFF == 27:
        break

# Release the webcam and close all windows
webcam.release()
cv2.destroyAllWindows()
