import torch
import numpy as np
import cv2

# Loading the YOLOv5 model from the Ultralytics repository
yolo_model = torch.hub.load('ultralytics/yolov5', 'yolov5s')

# Performs object detection and render the result
image_results = yolo_model("resources/example.jpg")
# image_results.show()

# Detect objects in real time
video_capture = cv2.VideoCapture(0)
while video_capture.isOpened():
    ret, video_frame = video_capture.read()

    # Integrate with YOLO model
    real_time_results = yolo_model(video_frame)
    cv2.imshow('Detect', np.squeeze(real_time_results.render()))
    if cv2.waitKey(10) & 0xFF == ord('q'):
        break
video_capture.release()
cv2.destroyAllWindows()
