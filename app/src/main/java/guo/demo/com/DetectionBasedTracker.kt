package guo.demo.com

import org.opencv.core.MatOfRect
import org.opencv.core.Mat



/**
 * Created by admin on 2018/1/9.
 */
class DetectionBasedTracker(cascadeName: String, minFaceSize: Int) {

    private var mNativeObj: Long = 0

    init {
        mNativeObj = nativeCreateObject(cascadeName, minFaceSize)
    }

    fun start() {
        nativeStart(mNativeObj)
    }

    fun stop() {
        nativeStop(mNativeObj)
    }

    fun setMinFaceSize(size: Int) {
        nativeSetFaceSize(mNativeObj, size)
    }

    fun detect(imageGray: Mat, faces: MatOfRect) {
        nativeDetect(mNativeObj, imageGray.nativeObjAddr, faces.nativeObjAddr)
    }

    fun release() {
        nativeDestroyObject(mNativeObj)
        mNativeObj = 0
    }

    private external fun nativeCreateObject(cascadeName: String, minFaceSize: Int): Long
    private external fun nativeDestroyObject(thiz: Long)
    private external fun nativeStart(thiz: Long)
    private external fun nativeStop(thiz: Long)
    private external fun nativeSetFaceSize(thiz: Long, size: Int)
    private external fun nativeDetect(thiz: Long, inputImage: Long, faces: Long)
}
