LOCAL_PATH := $(call my-dir)

   include $(CLEAR_VARS)

   LOCAL_MODULE    := colortransform
   LOCAL_SRC_FILES := colortransform.c
   LOCAL_LDLIBS    += -ljnigraphics -llog

include $(BUILD_SHARED_LIBRARY)
