package com.ttv.facerecog

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nitap.attende.LoginActivity
import com.ttv.face.FaceEngine
import com.ttv.face.FaceFeatureInfo
import com.ttv.face.FaceResult

class  MainActivity : AppCompatActivity(){
    public companion object {
        lateinit var userLists: ArrayList<FaceEntity>
    }

    private var context: Context? = null
    var mydb: DBHelper? = null

    init {
        userLists = ArrayList(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        context = this
        com.nitap.attende.MainActivity.context = this
        com.nitap.attende.MainActivity.check = FaceEngine.getInstance(this).setActivation("")
        FaceEngine.getInstance(this).init(2)

        com.nitap.attende.MainActivity.faceEngine = FaceEngine.getInstance(this)

        mydb = DBHelper(this)
        mydb!!.getAllUsers()
        com.nitap.attende.MainActivity.mydb = mydb;

            val myintent = Intent(this, com.nitap.attende.AdminActivity::class.java)
            startActivity(myintent)
            finish();



    }

    override fun onResume() {
        super.onResume()

    }

    fun display(msg:String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    public fun trainFace(uri: Uri, rollno:String) {
        display("TRAIN face called")
        try {
            display(uri.toString())

            var bitmap: Bitmap = ImageRotator.getCorrectlyOrientedImage(this, uri)
            display(bitmap.toString())
            val faceResults:MutableList<FaceResult> = FaceEngine.getInstance(this).detectFace(bitmap)
            display("faceres size : "+faceResults.size.toString())
            if(faceResults.count() == 1) {
                FaceEngine.getInstance(this).extractFeature(bitmap, true, faceResults)
                display("face feature extracted")
                val userName = String.format("User%03d", userLists.size + 1)
                display("aa")
                val cropRect = Utils.getBestRect(bitmap.width, bitmap.height, faceResults.get(0).rect)
                display("a")
                val headImg = Utils.crop(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), 120, 120)

                val inputView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_input_view, null, false)
                display("b")
                //val editText = inputView.findViewById<EditText>(R.id.et_user_name)
                val ivHead = inputView.findViewById<ImageView>(R.id.iv_head)
                ivHead.setImageBitmap(headImg)
                display("alert showed")

                val s = LoginActivity.myRollno//editText.text.toString()
                if (TextUtils.isEmpty(s)) {
                    //editText.error = application.getString(R.string.name_should_not_be_empty)
                    //return@setOnClickListener
                }

                var exists:Boolean = false
                for(user in userLists) {
                    if(TextUtils.equals(user.userName, s)) {
                        exists = true
                        break
                    }
                }

                if(exists) {
                    //editText.error = application.getString(R.string.duplicated_name)
                   // return@setOnClickListener
                }

                val user_id = mydb!!.insertUser(s, headImg, faceResults.get(0).feature)
                val face = FaceEntity(user_id, s, headImg, faceResults.get(0).feature)
                userLists.add(face)

                val faceFeatureInfo = FaceFeatureInfo(
                    user_id,
                    faceResults.get(0).feature
                )

                FaceEngine.getInstance(this).registerFaceFeature(faceFeatureInfo)


                //findViewById<Button>(R.id.btnVerify).isEnabled = MainActivity.userLists.size > 0
                Toast.makeText(this, "Register succeed!", Toast.LENGTH_SHORT).show()
                //editText.setText(userName)
               /* val confirmUpdateDialog: AlertDialog = AlertDialog.Builder(this)
                    .setView(inputView)
                    .setPositiveButton(
                        "OK", null
                    )
                    .setNegativeButton(
                        "Cancel", null
                    )
                    .create()*/
               // confirmUpdateDialog.show()

               /* confirmUpdateDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener { v: View? ->
                    }*/

            } else if(faceResults.count() > 1) {
                Toast.makeText(this, "Multiple face detected!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No face detected!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
            //handle exception
            e.printStackTrace()
        }
        Toast.makeText(this,"COMPLETED TRAINING", Toast.LENGTH_SHORT).show()
        finish()
    }


}