import uuid
import os
import time
import torch
import cv2
import numpy as np

IMAGES_PATH = os.path.join('data', 'images')
target_labels = ['awake', 'drowsy']
num_images_per_label = 20


def capture_images():
    video_capture = cv2.VideoCapture(0)
    for label in target_labels:
        print('Collecting images for {}'.format(label))
        time.sleep(5)
        # Loop through image range
        for img_num in range(num_images_per_label):
            print('Collecting images for {}, image number {}'.format(label, img_num))
            # Webcam feed
            ret, frame = video_capture.read()
            # Naming our image file
            image_name = os.path.join(IMAGES_PATH, label + '.' + str(uuid.uuid1()) + '.jpg')
            # Write out image to file
            cv2.imwrite(image_name, frame)
            # Render to the screen
            cv2.imshow('Image Collection', frame)
            # 2-second delay
            time.sleep(2)

        if cv2.waitKey(10) & 0xFF == ord('q'):
            break
    video_capture.release()
    cv2.destroyAllWindows()


def execute_run():

    model = torch.hub.load('ultralytics/yolov5', 'custom',
                           path='yolov5/runs/train/exp4/weights/last.pt', force_reload=True)
    capture = cv2.VideoCapture(0)
    while capture.isOpened():
        ref, frame = capture.read()
        results = model(frame)
        cv2.imshow('YOLO', np.squeeze(results.render()))
        if cv2.waitKey(10) & 0xFF == ord('q'):
            break
    capture.release()
    cv2.destroyAllWindows()


while True:
    user_choice = input("Press: 'c' to Capture | 'r' to Run | 'any key' to Exit: ").lower()
    if user_choice == 'c':
        capture_images()
    elif user_choice == 'r':
        execute_run()
    else:
        break
