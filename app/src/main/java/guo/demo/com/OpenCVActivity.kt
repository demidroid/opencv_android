package guo.demo.com

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_open_cv.*
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils;
import org.opencv.core.CvType
import org.opencv.core.Mat

class OpenCVActivity : AppCompatActivity(),CameraBridgeViewBase.CvCameraViewListener2 {

    private val TAG = "MainActivity"
    override fun onCameraViewStarted(width: Int, height: Int) {

        rgba = Mat(height,width, CvType.CV_8UC4)
        gray = Mat(height, width, CvType.CV_8UC1)
        mIntermediateMat = Mat(height,width,CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        rgba?.release()
        gray?.release()
        mIntermediateMat?.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        rgba = inputFrame?.rgba()
        gray = inputFrame?.gray()

        nativeProcessFrame(gray!!.nativeObjAddr,rgba!!.nativeObjAddr)
       return rgba!!
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {
        super.onPointerCaptureChanged(hasCapture)
    }

    private var rgba:Mat? = null
    private var gray:Mat? = null
    private var mIntermediateMat:Mat? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_cv)

        activity_camera_view.visibility = CameraBridgeViewBase.VISIBLE
        activity_camera_view.setCvCameraViewListener(this)
        activity_camera_view.isClickable = true

        activity_camera_view.setOnClickListener {
        }
    }


    override fun onPause() {
        super.onPause()

        if (activity_camera_view!=null){

            activity_camera_view.disableView()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!OpenCVLoader.initDebug()) {

            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")

        } else {

            Log.d(TAG, "OpenCV library found inside package. Using it!")

            activity_camera_view.enableView()

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity_camera_view!=null){

            activity_camera_view.disableView()
        }
    }
    external fun stringFromJNI(): String
    external fun nativeProcessFrame(gray:Long,rgba:Long)
    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
