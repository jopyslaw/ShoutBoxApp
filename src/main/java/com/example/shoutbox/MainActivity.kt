package com.example.shoutbox

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent : Intent? = null
        val sharedPreferences: SharedPreferences = getSharedPreferences("LOGIN_DATA",MODE_PRIVATE)
        val login = sharedPreferences.getString("LOGIN_VALUE","NULL")
        val log: String = login.toString()
        if(log != "NULL") {
            intent = Intent(this, DrawerActivity::class.java)
            val intent2 = getIntent()
            val check = intent2.getBooleanExtra("CHANGE_DATA",true)
            if(check != false)
                startActivity(intent)
        }
    }


    fun saveData(view: View)
    {
        var loginField: EditText = findViewById(R.id.loginField)
        var login = loginField.text.toString()
        var sharedPreferences: SharedPreferences = getSharedPreferences("LOGIN_DATA",
            MODE_PRIVATE)
        var sharedPreferencesEditor : SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putString("LOGIN_VALUE", login)
        sharedPreferencesEditor.apply()
        var intent: Intent = Intent(this, DrawerActivity::class.java)
        startActivity(intent)
    }


}


