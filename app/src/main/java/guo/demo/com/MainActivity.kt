package guo.demo.com

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.bumptech.glide.Glide
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {
    var  path:String = ""
    companion object {
        private val REQUEST_CODE_CHOOSE = 12

        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun grayProc(pixels: IntArray, w: Int, h: Int): IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        choose_album.setOnClickListener {

            RxPermissions(this)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe({
                        if (it) {
                            Matisse.from(this)
                                    .choose(MimeType.of(MimeType.PNG, MimeType.JPEG))

                                    .capture(true)
                                    .captureStrategy(CaptureStrategy(true, "guo.demo.com.fileprovider"))
                                    .maxSelectable(1)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .imageEngine(GlideEngine())
                                    .forResult(REQUEST_CODE_CHOOSE)
                        } else {
                            toast(R.string.permission_camera_never_askagain)
                        }
                    }, {
                        it.printStackTrace()
                    })
        }

        gray_album.setOnClickListener {


            var matrix = Matrix();
            matrix.setScale(0.5f, 0.5f)
            var bmp = BitmapFactory.decodeFile(path)
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);



            val w = bmp.width
            val h = bmp.height
            val pixels = IntArray(w * h)
            bmp.getPixels(pixels,0,w,0,0,w,h)
            val resultInt = grayProc(pixels,w,h)
            var resultImg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            resultImg.setPixels(resultInt, 0, w, 0, 0, w, h)
            image_view.setImageBitmap(resultImg)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val obtainResult = Matisse.obtainResult(data)
           path  = Matisse.obtainPathResult(data)[0]
            val uri = obtainResult[0]
//            image_view.setImageURI(uri)

            Glide.with(this).load(uri).into(image_view)
        }

    }


}
