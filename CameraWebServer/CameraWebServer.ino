#include <WiFi.h>
#include <WiFiClient.h>
#include "CameraModule.h"
#include <HTTPClient.h>
#include <ESP32Time.h>
#include <WebServer.h>
#include <WiFiManager.h>
#include <ArduinoJson.h>
#include <EEPROM.h>

// ===================
// Camera and Sensor Configuration
// ===================
#define CAMERA_MODEL_WROVER_KIT
#define EEPROM_SIZE 512
#define USER_ID_ADDR 0

// const int trigPin = 33;
// const int echoPin = 32;

const int inputPin = 33;
int pirState = LOW;
int val = 0;
unsigned long motionDebounceStartTime = 0;
const unsigned long DEBOUNCE_DELAY = 5000;

const int STABLE_MOTION_COUNT = 10;
int stableMotionCounter = 0;
const unsigned long NO_MOTION_RESET_DELAY = 10000;

String serverIP = "";
const int port = 5000;
const char* endpoint = "/upload";

String deviceMAC;

ESP32Time rtc;
bool motionDetected = false;
unsigned long motionStartTime = 0;

void setup() {
  Serial.begin(115200);
  Serial.setDebugOutput(true);

  if (!initializeCamera()) {
    Serial.println("Camera module initialization failed");
    return;
  }

// Setup LED flash if LED pin is defined in camera_pins.h
#if defined(LED_GPIO_NUM)
  setupLedFlash(LED_GPIO_NUM);
#endif

  Serial.println("Waiting for configuration...");

  WiFiManager wifiManager;
  //wifiManager.resetSettings();
  wifiManager.autoConnect("ESP32CamAP");
  Serial.println("Connected to WiFi!");

  rtc.setTime(1656609600);

  deviceMAC = WiFi.macAddress();
  Serial.print("Device MAC Address: ");
  Serial.println(deviceMAC);

  // pinMode(trigPin, OUTPUT);
  // pinMode(echoPin, INPUT);

  pinMode(inputPin, INPUT);

  serverIP = getServerIP();
  if (serverIP.isEmpty()) {
    Serial.println("Failed to get server IP");
  } else {
    Serial.println("Server IP: " + serverIP);
  }
}

void loop() {
  // digitalWrite(trigPin, LOW);
  // delayMicroseconds(2);
  // digitalWrite(trigPin, HIGH);
  // delayMicroseconds(10);
  // digitalWrite(trigPin, LOW);

  // long duration = pulseIn(echoPin, HIGH);
  // long distance = duration * 0.034 / 2;

  // Serial.print("Distance: ");
  // Serial.println(distance);

  val = digitalRead(inputPin);

  // if (distance <= 150 && !motionDetected) {
  // if (val == HIGH) {
  //   if (!motionDetected) {
  //     if (motionDebounceStartTime == 0) {
  //       motionDebounceStartTime = millis();
  //     } else if ((millis() - motionDebounceStartTime) > DEBOUNCE_DELAY) {
  //       motionDetected = true;
  //       motionStartTime = millis();
  //       Serial.println("Motion detected!");
  //       motionDebounceStartTime = 0; 
  //     }
  //   }
  // }
  // if (val == HIGH && !motionDetected) {
  //   motionDetected = true;
  //   motionStartTime = millis();

  //   Serial.println("Motion detected!");

  // } 
  // else {
  //   Serial.println("Motion ended!");
  //   motionDebounceStartTime = 0;
  // }

 if (val == HIGH) {
    stableMotionCounter++;
    if (!motionDetected && stableMotionCounter >= STABLE_MOTION_COUNT) {
      motionDetected = true;
      motionStartTime = millis();
      Serial.println("Motion detected!");
      stableMotionCounter = 0; 
    }
  } else {
    if (motionDetected && (millis() - motionStartTime) >= 60000) {
      sendStopMotionSignal();
      motionDetected = false;
      Serial.println("Motion ended after 60 seconds!");
      stableMotionCounter = 0;
    } else if (stableMotionCounter > 0) {
      stableMotionCounter--;
    }
  }

  if (motionDetected && (millis() - motionStartTime) < 60000) {
    camera_fb_t* fb = captureFrame();
    if (fb) {
      String imageName = "capture_" + String(rtc.getEpoch()) + ".jpg";
      sendImage(fb, imageName);
      releaseFrame(fb);

      unsigned long processTime = millis() - motionStartTime;
      unsigned long delayTime = max(0UL, 250UL - processTime % 250);
      //delay(delayTime);
    }
  }

  if (motionDetected && (millis() - motionStartTime) >= 60000) {
    sendStopMotionSignal();
    motionDetected = false;
  }

  if (!motionDetected) {
    delay(200);
  }
}


void sendImage(camera_fb_t* fb, const String& imageName) {
  if (serverIP.isEmpty()) {
    Serial.println("Server IP not available");
    return;
  }

  WiFiClient client;
  HTTPClient http;
  http.begin(client, "http://" + serverIP + ":" + String(port) + endpoint);
  http.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

  String head = "--" + String("----WebKitFormBoundary7MA4YWxkTrZu0gW") + "\r\nContent-Disposition: form-data; name=\"frame\"; filename=\"" + imageName + "\"\r\nContent-Type: image/jpeg\r\n\r\n";
  String tail = "\r\n--" + String("----WebKitFormBoundary7MA4YWxkTrZu0gW") + "--\r\n";

  http.addHeader("X-DEVICE-MAC", deviceMAC);

  http.POST(head + String((char*)fb->buf, fb->len) + tail);
  http.end();
}

void sendStopMotionSignal() {
  if (serverIP.isEmpty()) {
    Serial.println("Server IP not available");
    return;
  }

  WiFiClient client;
  HTTPClient http;
  http.begin(client, "http://" + serverIP + ":" + String(port) + "/stop_motion");
  http.POST("");
  http.end();
}

String getServerIP() {
  HTTPClient http;
  http.begin("https://coen-elec-390-ed129-default-rtdb.firebaseio.com/server_info/ip.json");
  int httpCode = http.GET();

  if (httpCode == HTTP_CODE_OK) {
    String payload = http.getString();
    DynamicJsonDocument doc(1024);
    deserializeJson(doc, payload);
    String ip = doc.as<String>();
    return ip;
  } else {
    Serial.printf("GET request failed, error: %s\n", http.errorToString(httpCode).c_str());
  }
  http.end();
  return "";
}