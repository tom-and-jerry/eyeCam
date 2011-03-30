/*
 * colortransform.c
 *
 *  Created on: Mar 26, 2011
 *      Author: buddelwilly
 */

#include "ch_hsr_eyecam_colormodel_ColorTransform.h"
#include <android/bitmap.h>
#include <android/log.h>
#include <stdint.h>

void transformYuv2Rgb(uint8_t *data, int32_t width, int32_t height, uint32_t *buffer);

#undef LOG_TAG
#define LOG_TAG "libcolortransform"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARNING, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

typedef enum colortransform_Effects {
	COLOR_EFFECT_NONE = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_NONE,
	COLOR_EFFECT_SIMULATE = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_SIMULATE,
	COLOR_EFFECT_NOY = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_NOY,
	COLOR_EFFECT_NOU = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_NOU,
	COLOR_EFFECT_NOV = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_NOV,
	COLOR_EFFECT_SWITCH_UV = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_SWITCH_UV,
} colortransform_Effects;

void transNone(int *x, int *y, int *z){}

void transNoX(int *x, int *y, int *z){
	*x = 0;
}

void transNoY(int *x, int *y, int *z){
	*y = 0;
}

void transNoZ(int *x, int *y, int *z){
	*z = 0;
}

void transSwitchYZ(int *x, int *y, int*z){
	int tmp = *y;
	*y = *z;
	*z = tmp;
}

void (*yuvTransPtr)(int*,int*,int*) = &transNone;

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_setEffect
  (JNIEnv * env, jclass cl, jint effect){
	switch (effect){
	case COLOR_EFFECT_NONE:
		yuvTransPtr = &transNone;
		break;
	case COLOR_EFFECT_NOY:
		yuvTransPtr = &transNoX;
		break;
	case COLOR_EFFECT_NOU:
		yuvTransPtr = &transNoY;
		break;
	case COLOR_EFFECT_NOV:
		yuvTransPtr = &transNoZ;
		break;
	case COLOR_EFFECT_SWITCH_UV:
		yuvTransPtr = &transSwitchYZ;
		break;
	}
}


void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_transformImageToBitmap
  (JNIEnv * env, jclass cl, jbyteArray jarray, jint width, jint height, jobject bitmap){
    AndroidBitmapInfo 	info;
	int 				ret;
	void* 				pixels;
	jboolean 			isCopy;
	jbyte* 				jdata = (*env)->GetByteArrayElements(env, jarray, &isCopy);
	uint8_t* 			data = (uint8_t*) jdata;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		LOGD("Bitmap: %x", bitmap);
		return;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	uint32_t* buffer = (uint32_t*) pixels;
    transformYuv2Rgb(data, (int32_t) width, (int32_t) height, buffer);

    AndroidBitmap_unlockPixels(env, bitmap);
	(*env)->ReleaseByteArrayElements(env, jarray, jdata, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_transformImageToBuffer
  (JNIEnv * env, jclass cl, jbyteArray jdata, jint width, jint height, jbyteArray buffer){

}

void transformYuv2Rgb(uint8_t *data, int32_t width, int32_t height, uint32_t *buffer)
{
	static int bytes_per_pixel = 2;
    int frameSize = width * height;
	int i, j, nY, nV, nU;
	uint8_t *pY = data, *pUV = data + frameSize;
	int offset = 0;

	for (i = 0; i < height; i++)
    {
      for (j = 0; j < width; j++)
      {
        nY = *(pY + i * width + j);
        nU = *(pUV + (i / 2) * width + bytes_per_pixel * (j / 2));
        nV = *(pUV + (i / 2) * width + bytes_per_pixel * (j / 2) + 1);

        nY -= 16;
        nU -= 128;
        nV -= 128;

        if (nY < 0) nY = 0;

        yuvTransPtr(&nY, &nU, &nV);

        int y1192 = 1192 * nY;
        int nR = (y1192 + 1634 * nV);
        int nG = (y1192 - 833 * nV - 400 * nU);
        int nB = (y1192 + 2066 * nU);

        if (nR < 0) nR = 0; else if (nR > 262143) nR = 262143;
        if (nG < 0) nG = 0; else if (nG > 262143) nG = 262143;
        if (nB < 0) nB = 0; else if (nB > 262143) nB = 262143;

        buffer[offset++] = 	0xff000000 |
        		((nR << 6) & 0xff0000) |
        		((nG >> 2) & 0xff00) |
        		((nB >> 10) & 0xff);
      }
   }
}
