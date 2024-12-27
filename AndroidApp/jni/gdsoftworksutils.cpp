#include <string.h>
#include "gdsoftworksutils.h"

JNIEXPORT void JNICALL Java_com_gdsoftworks_androidapp_Bytes_copyTo__Ljava_nio_ByteBuffer_2_3FII
  (JNIEnv *env, jclass jbytes, jobject dst, jfloatArray src, jint offset, jint length) {
    unsigned char* pDst = (unsigned char*) env->GetDirectBufferAddress(dst);
    float* pSrc = (float*) env->GetPrimitiveArrayCritical(src, 0);
    memcpy(pDst, pSrc+offset, length*4);
    env ->ReleasePrimitiveArrayCritical(src, pSrc, 0);
}
JNIEXPORT void JNICALL Java_com_gdsoftworks_androidapp_Bytes_copyTo__Ljava_nio_ShortBuffer_2_3SII
  (JNIEnv *env, jclass jbytes, jobject dst, jshortArray src, jint offset, jint length) {
    unsigned char* pDst = (unsigned char*) env->GetDirectBufferAddress(dst);
    float* pSrc = (float*) env->GetPrimitiveArrayCritical(src, 0);
    memcpy(pDst, pSrc+offset, length*2);
    env ->ReleasePrimitiveArrayCritical(src, pSrc, 0);
}