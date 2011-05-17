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
#define SQR(x) ((x)*(x))

typedef enum colortransform_Effects {
	COLOR_EFFECT_NONE = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_NONE,
	COLOR_EFFECT_SIMULATE = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_SIMULATE,
	COLOR_EFFECT_FALSE_COLORS = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_FALSE_COLORS,
	COLOR_EFFECT_INTENSIFY_DIFFERENCE = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_INTENSIFY_DIFFERENCE,
	COLOR_EFFECT_DALTONIZE = ch_hsr_eyecam_colormodel_ColorTransform_COLOR_EFFECT_DALTONIZE,
} colortransform_Effects;

/**
 * start definitions of the transformation functions. These get called for each
 * pixel through the global yuvTransPtr function pointer.
 *
 * Due to performance and polymorphic reasons each of the effect functions need
 * to perform the yuv to rgb transformation. Refer to effectNone to see an example
 * algorithm for the transformation. The rgb values transformed are in the range
 * [2^16, 2^24].
 *
 * The transformation functions need to confirm to the following contract:
 * @pre:	y, u, v, integers with yuv values
 * 			r, g, b, integers with rgb values
 * @post:	in-place integer value transformation form yuv to rgb
 */
void effectNone(int* y, int* u, int* v, int* r, int* g, int* b){
	int yMax = 65536 * *y;
	*r = (yMax + 92250 * *v);
	*g = (yMax - 22644 * *u - 46990 * *v);
	*b = (yMax + 116596 * *u);
}

void effectSimulate(int* y, int* u, int* v, int* r, int* g, int* b){
	int yMax = 65536 * *y;
	*r = (yMax - 20099 * *u - 31341 * *v);
	*g = (yMax - 20099 * *u - 31341 * *v);
	*b = (yMax + 116690 * *u + 558 * *v);
}

void effectIntesify(int* y, int* u, int* v, int* r, int* g, int* b){
	*r = (60000 * *y + 92250 * *v);
	*g = (70000 * *y - 22644 * *u - 46990 * *v);
	*b = (65536 * *y + 116596 * *u);
}

void effectFalseColors(int* y, int* u, int* v, int* r, int* g, int* b){
	int yMax = 65536 * *y;
	*r = (yMax + 92250 * *u);
	*g = (yMax - 22644 * *v - 46990 * *u);
	*b = (yMax + 116596 * *v);
}

void effectDaltonize(int* y, int* u, int* v, int* r, int* g, int* b){
	int rSim, gSim, bSim;
	int rDiff,gDiff,bDiff;

	effectNone(y,u,v,r,g,b);
	effectSimulate(y,u,v,&rSim,&gSim,&bSim);
	rDiff = *r - rSim;
	gDiff = *g - gSim;
	bDiff = *b - bSim;

	*g = *g + (7*rDiff)/10 + gDiff;
	*b = *b + (7*rDiff)/10 + bDiff;
}

void (*effectPtr)(int*,int*,int*,int*,int*,int*) = &effectNone;
void (*partialEffectPtr)(int*,int*,int*,int*,int*,int*) = &effectNone;

void partialEffect(int* y, int* u, int* v, int* r, int* g, int* b){
	static int THRESHOLD = SQR(50);
	int rSim, gSim, bSim;
	int rDiff,gDiff,bDiff;

	effectNone(y,u,v,r,g,b);
	effectSimulate(y,u,v,&rSim,&gSim,&bSim);
	rDiff = *r - rSim; rDiff >>= 16;
	gDiff = *g - gSim; gDiff >>= 16;
	bDiff = *b - bSim; bDiff >>= 16;

	int deltaE = 2*SQR(rDiff)+4*SQR(gDiff);
	if (deltaE > THRESHOLD){
		partialEffectPtr(y,u,v,r,g,b);
	}
}

/**
 * start definitions of the JNI binding functions
 */

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_setEffect
  (JNIEnv * env, jclass cl, jint effect){
	switch (effect){
	case COLOR_EFFECT_NONE:
		effectPtr = &effectNone;
		break;
	case COLOR_EFFECT_SIMULATE:
		effectPtr = &effectSimulate;
		break;
	case COLOR_EFFECT_INTENSIFY_DIFFERENCE:
		effectPtr = &effectIntesify;
		break;
	case COLOR_EFFECT_FALSE_COLORS:
		effectPtr = &effectFalseColors;
		break;
	case COLOR_EFFECT_DALTONIZE:
		effectPtr = &effectDaltonize;
		break;
	}
}

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_setPartialEffect
  (JNIEnv * env, jclass cl, jint effect){
	Java_ch_hsr_eyecam_colormodel_ColorTransform_setEffect(env,cl,effect);

	if (	effect == COLOR_EFFECT_NONE ||
				effect == COLOR_EFFECT_SIMULATE ||
				effect == COLOR_EFFECT_DALTONIZE)
			return;

	partialEffectPtr = effectPtr;
	effectPtr = &partialEffect;
}

void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_transformImageToBitmap
  (JNIEnv * env, jclass cl, jbyteArray jarray, jint width, jint height, jobject bitmap){
	int 				ret;
	void* 				pixels;
	jboolean 			isCopy;
	jbyte* 				jdata = (*env)->GetByteArrayElements(env, jarray, &isCopy);
	uint8_t* 			data = (uint8_t*) jdata;

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		//TODO: throw exception
	}

	uint16_t* buffer = (uint16_t*) pixels;
    transformYuv2Rgb(data, (int32_t) width, (int32_t) height, buffer);

    AndroidBitmap_unlockPixels(env, bitmap);
	(*env)->ReleaseByteArrayElements(env, jarray, jdata, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_ch_hsr_eyecam_colormodel_ColorTransform_transformImageToBuffer
  (JNIEnv * env, jclass cl, jbyteArray jarray, jint width, jint height, jbyteArray buffer){
	jboolean 			isCopy;
	jbyte* 				jdata = (*env)->GetByteArrayElements(env, jarray, &isCopy);
	uint8_t* 			data = (uint8_t*) jdata;
	jbyte* 				jbuffer = (*env)->GetByteArrayElements(env, buffer, &isCopy);
	uint16_t* 			pixels = (uint16_t*) jbuffer;

    transformYuv2Rgb(data, (int32_t) width, (int32_t) height, buffer);

	(*env)->ReleaseByteArrayElements(env, jarray, jdata, JNI_ABORT);
	(*env)->ReleaseByteArrayElements(env, buffer, jbuffer, JNI_ABORT);
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
 * |v0,1|u0,1|v2,3|u2,3|........|  ^
 * |............................|  | height/2
 * |____________________________|  v
 *
 * The transformYuv2Rgb function takes the data frames, applies
 * the function yuvTransPtr points to on the elements of YUV
 * colorspace, the function rgbTransPtr points to no the elements
 * of the RGB colorspace  and converts them to the RGB565 format.
 * Each pixel in the RGB565 format looks like the following:
 *  _______________________________________________
 * |R4,R3,R2,R1,R0|G5,G4,G3,G2,G1,G0|B4,B3,B2,B1,B0|
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
	int nY,nU,nV,nR,nG,nB;
    int frameSize = width * height;
	int i, j;
	uint8_t *pY = data, *pUV = data + frameSize;
	int offset = 0;

	for (i = 0; i < height; i++)
    {
      for (j = 0; j < width; j++)
      {
        nY = *(pY + i * width + j);
        nU = *(pUV + (i / 2) * width + bytes_per_pixel * (j / 2) + 1);
        nV = *(pUV + (i / 2) * width + bytes_per_pixel * (j / 2));

        nU -= 128;
        nV -= 128;

        if (nY < 0) nY = 0;

        effectPtr(&nY,&nU,&nV,&nR,&nG,&nB);

        if (nR < 0) nR = 0; else if (nR > 16777215) nR = 16777215;
        if (nG < 0) nG = 0; else if (nG > 16777215) nG = 16777215;
        if (nB < 0) nB = 0; else if (nB > 16777215) nB = 16777215;

        buffer[offset++] = 	((nR >> 8) & 0xf800) |
							((nG >> 13) & 0x07e0) |
							((nB >> 19) & 0x001f);
      }
   }
}
