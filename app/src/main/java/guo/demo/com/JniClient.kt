package guo.demo.com

/**
 * Created by admin on 2018/1/11.
 */
class JniClient{

    external fun CMYoloDetect(vImgPath: String, vModelType: Int, img_data: ByteArray, cols: Int, rows: Int, ch: Int): FloatArray

    companion object {
        init {
            System.loadLibrary("yoloso")
        }
    }
}