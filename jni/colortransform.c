/*
 * colortransform.c
 *
 *  Created on: Mar 26, 2011
 *      Author: buddelwilly
 */

#include "ch_hsr_eyecam_transform_ColorTransform.h"
#include <android/bitmap.h>
#include <android/log.h>
#include <stdint.h>

#undef LOG_TAG
#define LOG_TAG "libcolortransform"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARNING, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

typedef enum colortransform_Effects {
	COLOR_EFFECT_NONE = ch_hsr_eyecam_transform_ColorTransform_COLOR_EFFECT_NONE,
	COLOR_EFFECT_SIMULATE = ch_hsr_eyecam_transform_ColorTransform_COLOR_EFFECT_SIMULATE,
} colortransform_Effects;

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_transform_ColorTransform_setEffect
  (JNIEnv * env, jclass cl, jint effect){

}

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_transform_ColorTransform_transformImageToBitmap
  (JNIEnv * env, jclass cl, jbyteArray jarray, jint width, jint height, jobject bitmap){
    AndroidBitmapInfo 	info;
	int 				ret;
	int 				i, j, yp;
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
	int frameSize = width * height;

	for (j = 0, yp = 0; j < height; j++) {
		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
		for (i = 0; i < width; i++, yp++) {
			int y = (0xff & ((int) data[yp])) - 16;
			if (y < 0)
				y = 0;
			if ((i & 1) == 0) {
				v = (0xff & data[uvp++]) - 128;
				u = (0xff & data[uvp++]) - 128;
			}

			int y1192 = 1192 * y;
			int r = (y1192 + 1634 * v);
			int g = (y1192 - 833 * v - 400 * u);
			int b = (y1192 + 2066 * u);

			if (r < 0) r = 0;
			else if (r > 262143) r = 262143;

			if (g < 0) g = 0;
			else if (g > 262143) g = 262143;

			if (b < 0) b = 0;
			else if (b > 262143) b = 262143;

			buffer[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2)
					& 0xff00) | ((b >> 10) & 0xff);
		}
	}

	AndroidBitmap_unlockPixels(env, bitmap);
	(*env)->ReleaseByteArrayElements(env, jarray, jdata, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_transform_ColorTransform_transformImageToBuffer
  (JNIEnv * env, jclass cl, jbyteArray jdata, jint width, jint height, jbyteArray buffer){

}
