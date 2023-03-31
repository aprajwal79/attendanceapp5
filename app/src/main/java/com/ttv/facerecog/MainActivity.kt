package com.ttv.facerecog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ttv.face.FaceEngine

class  MainActivity : AppCompatActivity(){
    public companion object {
        lateinit var userLists: ArrayList<FaceEntity>
    }

    private var context: Context? = null
    private var mydb: DBHelper? = null

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


        val myintent = Intent(this, com.nitap.attende.LoginActivity::class.java)
        startActivity(myintent)
        finish();

    }

    override fun onResume() {
        super.onResume()

    }


}