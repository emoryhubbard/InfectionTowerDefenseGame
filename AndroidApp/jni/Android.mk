LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE   := gdsoftworksutils
LOCAL_ARM_MODE := arm
LOCAL_SRC_FILES := gdsoftworksutils.cpp

include $(BUILD_SHARED_LIBRARY)