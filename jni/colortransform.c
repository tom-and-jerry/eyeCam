/*
 * colortransform.c
 *
 *
 *
 *
 *  Created on: Mar 26, 2011
 *      Author: Dominik Spengler
 */

#include "ch_hsr_eyecam_colormodel_ColorTransform.h"
#include <android/bitmap.h>
#include <android/log.h>
#include <stdint.h>

void transformYuv2Rgb(uint8_t *data, int32_t width, int32_t height, uint16_t *buffer);

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

/**
 * start definitions of the transformation functions. These get called for each
 * pixel through the global yuvTransPtr function pointer.
 *
 * The transformation functions need to confirm to the following contract:
 * @pre:	all three integer values > 0
 * @post:	in-place integer value transformation
 */

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

void reduceGreen(int *r, int *g, int *b){
	*g = *g - *g/8;
}

void (*yuvTransPtr)(int*,int*,int*) = &transNone;
void (*rgbTransPtr)(int*,int*,int*) = &transNone;

/**
 * start definitions of the JNI binding functions
 */

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_setEffect
  (JNIEnv * env, jclass cl, jint effect){
	switch (effect){
	case COLOR_EFFECT_NONE:
		yuvTransPtr = &transNone;
		rgbTransPtr = &transNone;
		break;
	case COLOR_EFFECT_NOY:
		yuvTransPtr = &transNoX;
		rgbTransPtr = &transNone;
		break;
	case COLOR_EFFECT_NOU:
		yuvTransPtr = &transNoY;
		rgbTransPtr = &transNone;
		break;
	case COLOR_EFFECT_NOV:
		yuvTransPtr = &transNoZ;
		rgbTransPtr = &transNone;
		break;
	case COLOR_EFFECT_SWITCH_UV:
		yuvTransPtr = &transSwitchYZ;
		rgbTransPtr = &transNone;
		break;
	case COLOR_EFFECT_SIMULATE:
		yuvTransPtr = &transNoY;
		rgbTransPtr = &reduceGreen;
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
		return;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	uint16_t* buffer = (uint16_t*) pixels;
    transformYuv2Rgb(data, (int32_t) width, (int32_t) height, buffer);

    AndroidBitmap_unlockPixels(env, bitmap);
	(*env)->ReleaseByteArrayElements(env, jarray, jdata, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_transformImageToBuffer
  (JNIEnv * env, jclass cl, jbyteArray jdata, jint width, jint height, jbyteArray buffer){

}

/**
 * start of the transformation methods.
 *
 * We get the data in yuv420sp aka NV21 format from the camera.
 * The data representation looks something like this:
 *
 * <-----------width------------>
 * | y0 | y1 | y2 |........|yW-2|  ^
 * |yW-1| yW |..................|  |
 * |............................|  | height
 * |............................|  |
 * |____________________________|  v
 * |u0,1|v0,1|u2,3|v2,3|........|  ^
 * |............................|  | height/2
 * |____________________________|  v
 *
 * The transformYuv2Rgb function takes the data frames, applies
 * the function yuvTransPtr points to on the elements of YUV
 * colorspace, the function rgbTransPtr points to no the elements
 * of the RGB colorspace  and converts them to the RGB565 format.
 * Each pixel in the RGB565 format looks like the following:
 *  _______________________________________________
 * |B4,B3,B2,B1,B0|G5,G4,G3,G2,G1,G0|R4,R3,R2,R1,R0|
 *  -----------------------------------------------
 *  15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0  Bitnumber
 *
 * @pre:	data in yuv420sp (NV21) format
 * 			width, height > 0
 * @post:	buffer filled with RGB565 values
 */

void transformYuv2Rgb(uint8_t *data, int32_t width, int32_t height, uint16_t *buffer)
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

        nR >>= 10; nR &= 0xff;
        nG >>= 10; nG &= 0xff;
        nB >>= 10; nB &= 0xff;

        rgbTransPtr(&nR, &nG, &nB);

        buffer[offset++] = 	((nB << 8) & 0xf800) |
							((nG << 3) & 0x07e0) |
							((nR >> 3) & 0x001f);
      }
   }
}
