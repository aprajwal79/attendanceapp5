package com.nitap.attende

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.nitap.attende.MainActivity.mydb
//import com.nitap.attende.MainActivity.context
import com.ttv.face.FaceEngine
import com.ttv.face.FaceFeatureInfo
import com.ttv.face.FaceResult
import com.ttv.facerecog.*
import com.ttv.facerecog.MainActivity

 public class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        trainFace(LoginActivity.myUri,LoginActivity.myRollno);





    }
fun display(msg:String) {
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}



     public fun trainFace(uri: Uri, rollno:String) {
         try {
             display(uri.toString())

             var bitmap: Bitmap = ImageRotator.getCorrectlyOrientedImage(com.nitap.attende.MainActivity.context, uri)
             display(bitmap.toString())
             val faceResults:MutableList<FaceResult> = com.nitap.attende.MainActivity.faceEngine.detectFace(bitmap)
             display(faceResults.size.toString())
             if(faceResults.count() == 1) {
                 com.nitap.attende.MainActivity.faceEngine.extractFeature(bitmap, true, faceResults)

                 val userName = String.format("User%03d", MainActivity.userLists.size + 1)
                 val cropRect = Utils.getBestRect(bitmap.width, bitmap.height, faceResults.get(0).rect)
                 val headImg = Utils.crop(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), 120, 120)

                 val inputView = LayoutInflater.from(com.nitap.attende.MainActivity.context)
                     .inflate(R.layout.dialog_input_view, null, false)
                 //val editText = inputView.findViewById<EditText>(R.id.et_user_name)
                 val ivHead = inputView.findViewById<ImageView>(R.id.iv_head)
                 ivHead.setImageBitmap(headImg)
                 //editText.setText(userName)
                 val confirmUpdateDialog: AlertDialog = AlertDialog.Builder(com.nitap.attende.MainActivity.context)
                     .setView(inputView)
                     .setPositiveButton(
                         "OK", null
                     )
                     .setNegativeButton(
                         "Cancel", null
                     )
                     .create()
                 confirmUpdateDialog.show()
                 confirmUpdateDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                     .setOnClickListener { v: View? ->
                         val s = ""//editText.text.toString()
                         if (TextUtils.isEmpty(s)) {
                             //editText.error = application.getString(R.string.name_should_not_be_empty)
                             return@setOnClickListener
                         }

                         var exists:Boolean = false
                         for(user in MainActivity.userLists) {
                             if(TextUtils.equals(user.userName, s)) {
                                 exists = true
                                 break
                             }
                         }

                         if(exists) {
                             //editText.error = application.getString(R.string.duplicated_name)
                             return@setOnClickListener
                         }

                         val user_id = mydb!!.insertUser(s, headImg, faceResults.get(0).feature)
                         val face = FaceEntity(user_id, s, headImg, faceResults.get(0).feature)
                         MainActivity.userLists.add(face)

                         val faceFeatureInfo = FaceFeatureInfo(
                             user_id,
                             faceResults.get(0).feature
                         )

                         com.nitap.attende.MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo)

                         confirmUpdateDialog.cancel()

                         //findViewById<Button>(R.id.btnVerify).isEnabled = MainActivity.userLists.size > 0
                         Toast.makeText(this, "Register succeed!", Toast.LENGTH_SHORT).show()
                     }

             } else if(faceResults.count() > 1) {
                 Toast.makeText(this, "Multiple face detected!", Toast.LENGTH_SHORT).show()
             } else {
                 Toast.makeText(this, "No face detected!", Toast.LENGTH_SHORT).show()
             }
         } catch (e: java.lang.Exception) {
             //handle exception
             e.printStackTrace()
             display(e.toString())
         }
         Toast.makeText(this,"COMPLETED TRAINING",Toast.LENGTH_SHORT).show()
         finish()
     }





}