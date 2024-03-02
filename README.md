# driver-fatigue-detection

## Description
Driver Drowsiness Detection is an Android app that monitors the driver in real-time. It sends video frames to an edge server, where a CNN (Convolutional Neural Network) model detects whether the driver is 'drowsy' or 'awake' and triggers an alarm accordingly. Additionally, the system periodically sends the drowsiness patterns to a cloud server, which can be viewed from the Android app.

## Applications

- [Android Application](https://github.com/DevelopedBy-Siva/driver-fatigue-detection/tree/android-app)
  
- [Edge Server](https://github.com/DevelopedBy-Siva/driver-fatigue-detection/tree/local-server)
  - Contains the CNN model for drowsiness detection.
  
- [Cloud Server](https://github.com/DevelopedBy-Siva/driver-fatigue-detection/tree/cloud-server)
  
- [Yolo Model](https://github.com/DevelopedBy-Siva/driver-fatigue-detection/tree/yolo-model)
  - This Yolo model is not part of the application.
