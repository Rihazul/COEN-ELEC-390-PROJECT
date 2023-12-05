#ifndef CAMERA_MODULE_H
#define CAMERA_MODULE_H

#include "esp_camera.h"

bool initializeCamera();
camera_fb_t* captureFrame();
void releaseFrame(camera_fb_t* frame);

#endif // CAMERA_MODULE_H
