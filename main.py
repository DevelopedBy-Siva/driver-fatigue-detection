import uuid
import os
import time
import torch
import numpy as np
import cv2

IMAGES_PATH = os.path.join('data', 'images')
labels = ['awake', 'drowsy']
number_images = 20

# Loading the YOLOv5 model from the Ultralytics repository
yolo_model = torch.hub.load('ultralytics/yolov5', 'yolov5s')

# Detect objects in real time
video_capture = cv2.VideoCapture(0)
for label in labels:
    print('Collecting images for {}'.format(label))
    time.sleep(5)

    # Loop through image range
    for img_num in range(number_images):
        print('Collecting images for {}, image number'.format(label, img_num))

        # Webcam feed
        ret, frame = video_capture.read()

        # Naming out image to file
        image_name = os.path.join(IMAGES_PATH, label+'.'+str(uuid.uuid1())+'.jpg')
        # Write out image to file
        cv2.imwrite(image_name, frame)
        # Render to the screen
        cv2.imshow('Image Collection', frame)
        # 2 second delay
        time.sleep(2)

    if cv2.waitKey(10) & 0xFF == ord('q'):
        break

video_capture.release()
cv2.destroyAllWindows()
