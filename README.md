# Sistemas_Embutidos
Project carried out in the Embedded Systems course 

## Project description

* **Doorbell camera 🕭🎦**
    *  This project will be to place a **camera on the door of a house/apartment**.
    * The camera should be activated if the doorbell is pressed or if movement is detected.
        * In these cases, the images should be recorded with "stop-motion" (i.e., with several photos instead of film in order to save space).
        * The system stops after 5 minutes after it stops detecting anyone.
* When the doorbell rings, the user should be notified on their smartphone.
    * In this case, they can request a video stream other than "stop-motion".
    * On the smartphone, it should be possible to view the recorded "stop-motions" and delete them.
    * On the smartphone, it should also be possible to activate the camera to view the streaming.
* There should be a red light indicating that the camera is recording.

### Requirements 

* Presence detection at the door must be < than 3 sec
* Recording start from detection or ringing of the doorbell must be < than 3 sec
* Video stream request from the smartphone must have a delay of < than 10 sec
* Presence must be detected at least 50 cm away

## Project Contents

### Strutucture     
    ├── Arduino
    │   └── arduino_config.ino
    ├── RPI
    │   ├── LiveStream-WebRTC-Flask-OpenCV-master
    |   |   ├── src
    │   |   │   ├── server.py
    │   |   ├── static
    │   |   │   └── main.js
    │   |   └── templates
    │   |       └── index.html
    |   └── Storage-Acess
    |       ├── credentials.json
    |       ├── img
    |       ├── server.py
    |       └── test.py
    ├── Android
        └── SE
            └── app 
                └── (...)

### Arduino 

* Contains ```arduino_config.ino```, which handles the following functionalities:
    * WiFi connection
    * Motion detection via sensor
    * Button press detection
    * Sending HTTP requests to the Raspberry Pi server

* This project uses the **Arduino UNO R4 WiFi board**.



### Android

* Contains a simple Android application that allows users to:
    * View live video feed from the Raspberry Pi camera

    * Receive notifications via Firebase when events are triggered

    * Access and delete files stored on the Raspberry Pi (photos captured during the "stop-motion" event)

* ⚠️ ```Important```: You must update the server path constants in the following files to match your Raspberry Pi server configuration:
    * StorageAccess.java

    * StreamingActivity.java

    * MainActivity.java

### RPI

* For **LiveStream-WebRTC-Flask-OpenCV-master** we used [this implementation](https://github.com/supersjgk/LiveStream-WebRTC-Flask-OpenCV) made by [supersjgk](https://github.com/supersjgk)

    * **To run**

        * ```Server side```

            * cd RPI/LiveStream-WebRTC-Flask-OpenCV-master/src
            * python server.py 

        * ```Client side```

            * To view the live stream from a Server's webcam/IP camera in a client machine, simply open a web browser and type http://127.0.0.1:<port>/ (client on same machine) OR http://<server_IP_address>:<port>/ (client on different machine).

* For **Storage-Acess** we created a flask server that handles the backend logic for receiving events, capturing stop-motion images, and managing communication with the Android app via Firebase.
    * **Main features**
     Main Features:
        * Push Notifications via Firebase
        * Stop-Motion Image Capture
        * Image Management
        * Storage Initialization
    * **To run**
        * cd RPI/Storage-Acess/
        * python server.py 
