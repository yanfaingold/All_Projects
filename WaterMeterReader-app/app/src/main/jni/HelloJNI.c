
#include "com_domproject_app_myapplication_MainActivity.h"

/*
 * Class:     com_domproject_app_myapplication_MainActivity
 * Method:    HelloJNI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_domproject_app_myapplication_MainActivity_HelloJNI
  (JNIEnv *env, jobject obj)
{
(*env)->NewStringUTF(env,"Hello from jni");
}

