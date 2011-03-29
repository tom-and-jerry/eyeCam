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
} colortransform_Effects;

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_setEffect
  (JNIEnv * env, jclass cl, jint effect){

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

	if (info.format != ANDROID_BITMAP_FORMAT_RGB_565) {
		LOGE("Bitmap format is not RGB_565 !");
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
        nV = *(pUV + (i / 2) * width + bytes_per_pixel * (j / 2));
        nU = *(pUV + (i / 2) * width + bytes_per_pixel * (j / 2) + 1);

        nY -= 16;
        nU -= 128;
        nV -= 128;

        if (nY < 0) nY = 0;

        int y1192 = 1192 * nY;
        int nR = (y1192 + 1634 * nV);
        int nG = (y1192 - 833 * nV - 400 * nU);
        int nB = (y1192 + 2066 * nU);

        if (nR < 0) nR = 0; else if (nR > 262143) nR = 262143;
        if (nG < 0) nG = 0; else if (nG > 262143) nG = 262143;
        if (nB < 0) nB = 0; else if (nB > 262143) nB = 262143;

        buffer[offset++] = 	((nR << 1) & 0xf800) |
							((nG >> 5) & 0x7e00) |
							((nB >> 10) & 0x1f);
      }
   }
}
