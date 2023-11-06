package com.example.prototype;

public class LiveAlert {
    private CameraFootage cameraFootage;
    private Sensor motionSensor;
    private Sensor usSensor;

    public LiveAlert() {
        cameraFootage = new CameraFootage();
        motionSensor = new Sensor();
        usSensor = new Sensor();
    }

    public LiveAlert(CameraFootage cameraFootage, Sensor motionSensor, Sensor usSensor) {
        this.cameraFootage = cameraFootage;
        this.motionSensor = motionSensor;
        this.usSensor = usSensor;
    }

    public CameraFootage getVideo() {
        return cameraFootage;
    }

    public void setVideo(CameraFootage cameraFootage) {
        this.cameraFootage = cameraFootage;
    }

    public Sensor getMotionSensor() {
        return motionSensor;
    }

    public void setMotionSensor(Sensor motionSensor) {
        this.motionSensor = motionSensor;
    }

    public Sensor getUsSensor() {
        return usSensor;
    }

    public void setUsSensor(Sensor usSensor) {
        this.usSensor = usSensor;
    }
}
