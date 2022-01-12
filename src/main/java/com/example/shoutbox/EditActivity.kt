package com.example.shoutbox

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditActivity : AppCompatActivity() {

    private var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)


        var intent = intent
        val msg = findViewById<EditText>(R.id.messageData)
        val username = intent.getStringExtra("username")
        val date = intent.getStringExtra("date")
        val message = intent.getStringExtra("message")
        id = intent.getStringExtra("id")


        setData(username, date, message)
        msg.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val msg = msg.text
                sendData(id.toString(), username.toString(), msg.toString())
                changeIntent()
                return@OnKeyListener true
            }
            false
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete -> {
                deletePost(id)
                intent = Intent(this, DrawerActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deletePost(id: String?)
    {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tgryl.pl/shoutbox/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postApi = retrofit.create(PostApi::class.java)
        val call = postApi.deletePost(id!!)

        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                Log.i("SUCESSFUL DELETE DATA", "DATA WAS SUCCESFULL DELETED")
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.e("PROBLEM WITH API CONNECTION", t.message.toString())
            }

        })
    }

    fun setData(username: String?, date: String?, message: String?)
    {
        val msg = findViewById<EditText>(R.id.messageData)
        val userField = findViewById<TextView>(R.id.LoginData)
        val dateField = findViewById<TextView>(R.id.DateData)
        userField.text = username
        dateField.text = date
        msg.setText(message)
    }

    fun changeIntent()
    {
        intent = Intent(this, DrawerActivity::class.java)
        startActivity(intent)
    }

    fun sendData(id: String?, username: String?, message: String?)
    {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tgryl.pl/shoutbox/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postApi = retrofit.create(PostApi::class.java)
        val call = postApi.editPost(id.toString(), message.toString(), username.toString())

        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                Log.i("SUCESSFUL EDIT DATA", "DATA WAS SUCCESFULL ADDED")
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.e("PROBLEM WITH API CONNECTION", t.message.toString())
            }

        })
    }
}