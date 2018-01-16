#include <jni.h>
#include <string>
#include <opencv2/core/matx.hpp>
#include <opencv2/core/types.hpp>
#include <opencv2/core/bufferpool.hpp>
#include <opencv2/core/mat.inl.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>



#define  LOG_TAG    "JNI_PART"

#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)

#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG, __VA_ARGS__)

#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, __VA_ARGS__)

#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG, __VA_ARGS__)
using namespace std;
using namespace cv;


extern "C"{

JNIEXPORT jstring JNICALL
Java_guo_demo_com_OpenCVActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_guo_demo_com_OpenCVActivity_nativeProcessFrame(JNIEnv *env, jobject instance, jlong addrGray,
                                                    jlong addrRgba) {
    Mat &g = *(Mat *) addrGray;
    Mat &r = *(Mat *) addrRgba;

    vector<KeyPoint> v;
    Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
    detector->detect(g,v);

    for (unsigned int i = 0;i<v.size();i++){

        const KeyPoint &kp = v[i];
        circle(r, Point(kp.pt.x, kp.pt.y), 10, Scalar(0, 255, 255, 255));
    }

}

// 将图片灰度化
JNIEXPORT jarray JNICALL
Java_guo_demo_com_MainActivity_grayProc(JNIEnv *env, jobject instance,
                                         jintArray buf, jint w, jint h) {

    jint *cbuf;
    jboolean ptfalse = false;
    cbuf = env->GetIntArrayElements(buf, &ptfalse);
    if(cbuf == NULL){
        return 0;
    }

    Mat imgData(h, w, CV_8UC4, (unsigned char*)cbuf);

    uchar* ptr = imgData.ptr(0);
    for(int i = 0; i < w*h; i ++){
        uchar grayScale = (uchar)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
        ptr[4*i+1] = grayScale;
        ptr[4*i+2] = grayScale;
        ptr[4*i+0] = grayScale;
    }

    int size=w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}


}
